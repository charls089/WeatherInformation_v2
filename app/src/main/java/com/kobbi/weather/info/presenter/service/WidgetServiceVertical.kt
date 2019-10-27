package com.kobbi.weather.info.presenter.service

import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.os.postDelayed
import com.kobbi.weather.info.R
import com.kobbi.weather.info.presenter.model.type.AirCode
import com.kobbi.weather.info.presenter.repository.WeatherRepository
import com.kobbi.weather.info.ui.view.activity.MainActivity
import com.kobbi.weather.info.ui.view.widget.WidgetProvider
import com.kobbi.weather.info.ui.view.widget.WidgetProviderVertical
import com.kobbi.weather.info.util.*
import kotlin.concurrent.thread

class WidgetServiceVertical : Service() {
    companion object {
        private const val TAG = "WidgetService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        applicationContext?.let { context ->
            startF()
            thread {
                val weatherRepository = WeatherRepository.getInstance(context)
                weatherRepository.getWeatherInfo()?.run {
                    val remoteViews =
                        RemoteViews(context.packageName, R.layout.widget_weather_vertical).apply {
                            setImageViewResource(
                                R.id.iv_widget_sky, WeatherUtils.getSkyIcon(dateTime, pty, sky)
                            )
                            val splitAddress = LocationUtils.splitAddressLine(address)
                            val cityName = splitAddress.lastOrNull()
                            setTextViewText(R.id.tv_widget_address, cityName)
                            setTextViewText(
                                R.id.tv_widget_tpr,
                                String.format(context.getString(R.string.holder_current_tpr), tpr)
                            )
                            setTextViewText(
                                R.id.tv_widget_wct,
                                String.format(context.getString(R.string.holder_sensory_tpr), wct)
                            )
                            setTextViewText(
                                R.id.tv_widget_update_time,
                                String.format(
                                    context.getString(R.string.holder_update_time),
                                    Utils.convertDateTime(type = 2)
                                )
                            )
                            if (splitAddress.size >= 2) {
                                weatherRepository
                                    .findAirMeasureData(splitAddress[0], splitAddress[1]).run {
                                        fun setDustValue(code: AirCode, value: String) {
                                            val dustValue = getAirValue(code, value)
                                            val resId = when (code) {
                                                AirCode.PM10 -> R.id.tv_widget_dust_info_pm10
                                                AirCode.PM25 -> R.id.tv_widget_dust_info_pm25
                                            }
                                            setTextViewText(
                                                resId,
                                                String.format(
                                                    getString(R.string.holder_dust_info),
                                                    code.codeName,
                                                    dustValue.first,
                                                    dustValue.second
                                                )
                                            )
                                        }
                                        setDustValue(AirCode.PM10, pm10)
                                        setDustValue(AirCode.PM25, pm25)
                                    }
                            }
                            setOnClickPendingIntent(
                                R.id.lo_widget_weather_info,
                                PendingIntent.getActivity(
                                    context,
                                    0,
                                    Intent(context, MainActivity::class.java).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    },
                                    0
                                )
                            )
                            val serviceIntent = Intent(context, WidgetServiceVertical::class.java)
                            setOnClickPendingIntent(
                                R.id.tv_widget_update_time,
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    PendingIntent.getForegroundService(
                                        context, 0, serviceIntent, 0
                                    )
                                } else {
                                    PendingIntent.getService(
                                        context, 0, serviceIntent, 0
                                    )
                                }
                            )

                            setViewVisibility(R.id.pb_widget, View.VISIBLE)
                            setViewVisibility(R.id.lo_widget_container, View.GONE)
                            Handler(Looper.getMainLooper()).postDelayed(500) {
                                setViewVisibility(R.id.pb_widget, View.GONE)
                                setViewVisibility(R.id.lo_widget_container, View.VISIBLE)
                                updateAppWidget(this)
                            }
                        }
                    updateAppWidget(remoteViews)
                }
            }
            stopF()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun updateAppWidget(remoteViews: RemoteViews) {
        applicationContext?.let { context ->
            AppWidgetManager.getInstance(context).run {
                updateAppWidget(
                    ComponentName(context, WidgetProviderVertical::class.java),
                    remoteViews
                )
            }
        }
    }

    private fun startF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DLog.d(TAG, "startForeground")
            val notificator = Notificator.getInstance()
            val type = Notificator.ChannelType.TYPE_POLARIS
            val notification = notificator.getNotification(applicationContext, type)
            startForeground(notificator.getNotificationId(type), notification)
        }
    }

    private fun stopF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DLog.d(TAG, "stopForeground")
            stopForeground(true)
        }
    }

    private fun getAirValue(code: AirCode, dustValue: String): Pair<String, String> {
        val value =
            if (TextUtils.isEmpty(dustValue)) getString(R.string.text_checking_data) else dustValue
        val level = AirCode.getAirLevel(code.codeNo, value)
        return Pair(value, level)
    }
}