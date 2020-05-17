package com.kobbi.weather.info.presenter.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
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

    fun restartService(context: Context, init: Boolean) {
        DLog.i(context, TAG, "ServiceManager.restartService()")
        context.applicationContext?.let {
            if (init) {
                registerRestartReceiver(it)
                if (SharedPrefHelper.getBool(it, SharedPrefHelper.KEY_AGREE_TO_USE_NOTIFICATION))
                    registerNotifyReceiver(it)
            }

            val beforeCheckTime = WeatherApplication.getUpdateCheckTime(it)
            if (System.currentTimeMillis() - beforeCheckTime > CHECK_WEATHER_INFO_INTERVAL)
                getWeatherInfo(it)
        }
    }

    @Synchronized
    fun getWeatherInfo(context: Context, init: Boolean = false) {
        DLog.d(tag = TAG, message = "ServiceManager.getWeatherInfo($init)")
        startServiceWithType(context, if (init) 0 else 2)
    }

    fun notifyWeather(context: Context) {
        startServiceWithType(context, 1)
    }

    private fun startServiceWithType(context: Context, type: Int) {
        val intent = Intent(context, WeatherService::class.java).apply {
            putExtra("type", type)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
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
            SystemClock.elapsedRealtime() + RESTART_SERVICE_INTERVAL,
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