package com.kobbi.weather.info.presenter.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.SystemClock
import com.kobbi.weather.info.presenter.WeatherApplication
import com.kobbi.weather.info.presenter.receiver.RestartServiceReceiver
import com.kobbi.weather.info.util.DLog
import java.util.*

object ServiceManager {
    const val ACTION_RESTART = ".action.restart.weather.service"
    const val ACTION_NOTIFY = ".action.notify.weather.service"
    private const val RESTART_REQUEST_CODE = 986
    private const val NOTIFY_REQUEST_CODE = 987
    private const val RESTART_SERVICE_INTERVAL = 1 * 60 * 1000L
    private const val NOTIFY_INFO_INTERVAL = 12 * 60 * 60 * 1000L
    private const val CHECK_WEATHER_INFO_INTERVAL = 5 * 60 * 1000L

    private const val TAG = "ServiceManager"

    private var mWeatherService: WeatherService? = null

    private val mWeatherServiceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            DLog.d(TAG, "WeatherService was disconnected.")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            DLog.d(TAG, "WeatherService was connected.")
            val binder = service as WeatherService.LocalBinder
            mWeatherService = binder.service
            getWeatherInfo()
        }
    }

    fun restartService(context: Context, init: Boolean) {
        DLog.writeLogFile(context, TAG, "ServiceManager.restartService($init)")
        context.applicationContext?.let {
            if (init) {
                registerRestartReceiver(it)
                registerNotifyReceiver(it)
            }

            bindService(it, WeatherService::class.java, mWeatherServiceConnection)

            val beforeCheckTime = WeatherApplication.getUpdateCheckTime(it)
            if (System.currentTimeMillis() - beforeCheckTime > CHECK_WEATHER_INFO_INTERVAL)
                getWeatherInfo()
        }
        echoService()
    }

    @Synchronized
    fun getWeatherInfo(init: Boolean = false) {
        DLog.d(TAG, "ServiceManager.getWeatherInfo($init)")
        mWeatherService?.runService(init)
    }

    fun notifyWeather() {
        DLog.d(TAG, "ServiceManager.notifyWeather()")
        mWeatherService?.notifyMyLocation()
    }

    fun getAction(context: Context, action: String) = context.packageName + action

    private fun registerRestartReceiver(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RestartServiceReceiver::class.java).apply {
            action = getAction(context, ACTION_RESTART)
        }
        val pendingIntent =
            PendingIntent.getBroadcast(context, RESTART_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime(),
            RESTART_SERVICE_INTERVAL,
            pendingIntent
        )
    }

    private fun registerNotifyReceiver(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RestartServiceReceiver::class.java).apply {
            action = getAction(context, ACTION_NOTIFY)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, NOTIFY_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val cal = GregorianCalendar().apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            cal.timeInMillis,
            NOTIFY_INFO_INTERVAL,
            pendingIntent
        )
    }

    private fun bindService(
        context: Context,
        clazz: Class<*>,
        serviceConnection: ServiceConnection
    ) {
        Intent(context, clazz).run {
            context.bindService(this, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun echoService() {
        mWeatherService?.echoService()
    }
}