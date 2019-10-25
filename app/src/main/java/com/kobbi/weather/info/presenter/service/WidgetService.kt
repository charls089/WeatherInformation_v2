package com.kobbi.weather.info.presenter.service

import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import com.kobbi.weather.info.R
import com.kobbi.weather.info.presenter.repository.WeatherRepository
import com.kobbi.weather.info.ui.view.activity.MainActivity
import com.kobbi.weather.info.ui.view.widget.WidgetProvider
import com.kobbi.weather.info.util.LocationUtils
import com.kobbi.weather.info.util.Utils
import com.kobbi.weather.info.util.WeatherUtils
import kotlin.concurrent.thread

class WidgetService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        applicationContext?.let { context ->
            thread {
                val weatherRepository = WeatherRepository.getInstance(context)
                weatherRepository.getWeatherInfo()?.run {
                    val remoteViews =
                        RemoteViews(context.packageName, R.layout.widget_weather).apply {
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
                                    Utils.convertDateTime()
                                )
                            )
                            if (splitAddress.size >= 2) {
                                weatherRepository
                                    .findAirMeasureData(splitAddress[0], splitAddress[1]).run {
                                        setTextViewText(
                                            R.id.tv_widget_dust_info,
                                            String.format(
                                                getString(R.string.holder_dust_info), pm10, pm25
                                            )
                                        )
                                    }
                            }
                            setOnClickPendingIntent(
                                R.id.lo_widget_container,
                                PendingIntent.getActivity(
                                    context,
                                    0,
                                    Intent(context, MainActivity::class.java).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    },
                                    0
                                )
                            )

                            setOnClickPendingIntent(
                                R.id.iv_widget_refresh,
                                PendingIntent.getService(
                                    context, 0, Intent(context, WidgetService::class.java), 0
                                )
                            )
                        }
                    AppWidgetManager.getInstance(context).run {
                        updateAppWidget(
                            ComponentName(context, WidgetProvider::class.java),
                            remoteViews
                        )
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}