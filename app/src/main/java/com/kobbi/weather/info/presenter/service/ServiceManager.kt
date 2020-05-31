package com.kobbi.weather.info.presenter.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.kobbi.weather.info.presenter.receiver.ServiceReceiver
import com.kobbi.weather.info.util.DLog
import java.util.*

object ServiceManager {
    const val ACTION_NOTIFY = ".action.notify.weather.service"

    const val SERVICE_TYPE_NAME = "type"
    const val SERVICE_TYPE_UNKNOWN = -1
    const val SERVICE_TYPE_RESET = 0
    const val SERVICE_TYPE_NOTIFICATION = 1
    const val SERVICE_TYPE_UPDATE = 2

    private const val NOTIFY_REQUEST_CODE = 987
    private const val NOTIFY_INFO_INTERVAL = 12 * 60 * 60 * 1000L

    private const val TAG = "ServiceManager"

    fun getAction(context: Context, action: String) = context.packageName + action

    @Synchronized
    fun getWeatherInfo(context: Context, init: Boolean = false) {
        DLog.d(tag = TAG, message = "ServiceManager.getWeatherInfo($init)")
        startServiceWithType(context, if (init) SERVICE_TYPE_RESET else SERVICE_TYPE_UPDATE)
    }

    fun notifyWeather(context: Context) {
        startServiceWithType(context, SERVICE_TYPE_NOTIFICATION)
    }

    private fun startServiceWithType(context: Context, type: Int) {
        val intent = Intent(context, WeatherService::class.java).apply {
            putExtra(SERVICE_TYPE_NAME, type)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
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

    fun registerNotifyReceiver(context: Context) {
        val intent = Intent(context, ServiceReceiver::class.java).apply {
            this.action = getAction(context, ACTION_NOTIFY)
        }
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
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC,
            triggerTime,
            NOTIFY_INFO_INTERVAL,
            pendingIntent
        )
    }
}