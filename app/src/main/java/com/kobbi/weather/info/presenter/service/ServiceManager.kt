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
import com.kobbi.weather.info.presenter.receiver.ServiceReceiver
import com.kobbi.weather.info.util.DLog
import com.kobbi.weather.info.util.SharedPrefHelper
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
    private var mPolarisService: UpDownService? = null

    private val mWeatherServiceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            DLog.i(TAG, "WeatherService was disconnected.")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            DLog.i(TAG, "WeatherService was connected.")
            val binder = service as WeatherService.LocalBinder
            mWeatherService = binder.service
            getWeatherInfo()
        }
    }

    private val mPolarisServiceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            DLog.i(TAG, "UpDownService was disconnected.")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            DLog.i(TAG, "UpDownService was connected.")
            val binder = service as UpDownService.LocalBinder
            mPolarisService = binder.service
            mPolarisService?.echo()
        }
    }

    fun restartService(context: Context, init: Boolean) {
        DLog.writeLogFile(context, TAG, "ServiceManager.restartService($init)")
        context.applicationContext?.let {
            if (init) {
                registerRestartReceiver(it)
                if (SharedPrefHelper.getBool(it,SharedPrefHelper.KEY_AGREE_TO_USE_NOTIFICATION))
                    registerNotifyReceiver(it)
            }

            bindService(it, WeatherService::class.java, mWeatherServiceConnection)
            bindService(it, UpDownService::class.java, mPolarisServiceConnection)

            val beforeCheckTime = WeatherApplication.getUpdateCheckTime(it)
            if (System.currentTimeMillis() - beforeCheckTime > CHECK_WEATHER_INFO_INTERVAL)
                getWeatherInfo()
        }
        mPolarisService?.echo()
    }

    @Synchronized
    fun getWeatherInfo(init: Boolean = false) {
        DLog.d(TAG, "ServiceManager.getWeatherInfo($init)")
        mWeatherService?.runService(init)
    }

    fun startF() {
        mPolarisService?.upPolaris()
    }

    fun stopF() {
        mPolarisService?.downPolaris()
    }

    fun notifyWeather() {
        DLog.d(TAG, "ServiceManager.notifyWeather()")
        mWeatherService?.notifyMyLocation()
    }

    fun getAction(context: Context, action: String) = context.packageName + action

    private fun getActionIntent(context: Context, action: String): Intent {
        return Intent(context, ServiceReceiver::class.java).apply {
            this.action = getAction(context, action)
        }
    }

    private fun getPendingIntent(
        context: Context,
        requestCode: Int,
        intent: Intent,
        flag: Int = PendingIntent.FLAG_CANCEL_CURRENT
    ): PendingIntent {
        return PendingIntent.getBroadcast(context, requestCode, intent, flag)
    }

    private fun setAlarmRepeat(
        context: Context,
        type: Int,
        time: Long,
        interval: Long,
        pendingIntent: PendingIntent
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(type, time, interval, pendingIntent)
    }

    private fun registerRestartReceiver(context: Context) {
        val intent = getActionIntent(context, ACTION_RESTART)
        val pendingIntent = getPendingIntent(context, RESTART_REQUEST_CODE, intent)
        setAlarmRepeat(
            context,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime(),
            RESTART_SERVICE_INTERVAL,
            pendingIntent
        )
    }

    private fun registerNotifyReceiver(context: Context) {
        val intent = getActionIntent(context, ACTION_NOTIFY)
        val pendingIntent = getPendingIntent(context, NOTIFY_REQUEST_CODE, intent)
        val cal = GregorianCalendar().apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        var triggerTime = cal.timeInMillis
        while (System.currentTimeMillis() > triggerTime)
            triggerTime += NOTIFY_INFO_INTERVAL

        setAlarmRepeat(
            context,
            AlarmManager.RTC,
            triggerTime,
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
}