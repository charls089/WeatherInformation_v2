package com.kobbi.weather.info.ui.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.kobbi.weather.info.R
import com.kobbi.weather.info.presenter.repository.WeatherRepository
import com.kobbi.weather.info.ui.view.activity.MainActivity
import com.kobbi.weather.info.util.Utils
import com.kobbi.weather.info.util.WeatherUtils
import kotlin.concurrent.thread

class WidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.e("####", "WidgetProvider.onUpdate() --> appWidgetIds : ${appWidgetIds.toList()}")
        thread {
            appWidgetIds.forEach { widgetId ->
                WeatherRepository.getInstance(context).getWeatherInfo()?.run {
                    Log.e("####", "weatherInfo : $this")
                    val remoteView =
                        RemoteViews(context.packageName, R.layout.widget_weather).apply {
                            setImageViewResource(
                                R.id.iv_widget_sky, WeatherUtils.getSkyIcon(dateTime, pty, sky)
                            )
                            val cityName = address.split(" ").lastOrNull()
                            setTextViewText(R.id.tv_widget_address, cityName)
                            setTextViewText(
                                R.id.tv_widget_tpr,
                                String.format(
                                    context.getString(R.string.holder_current_tpr), tpr
                                )
                            )
                            setTextViewText(
                                R.id.tv_widget_wct,
                                String.format(
                                    context.getString(R.string.holder_sensory_tpr), wct
                                )
                            )
                            setTextViewText(
                                R.id.tv_widget_update_time,
                                String.format(
                                    context.getString(R.string.holder_update_time),
                                    Utils.convertDateTime()
                                )
                            )
                            val intent = Intent(context, MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            }
                            val pendingIntent: PendingIntent =
                                PendingIntent.getActivity(context, 0, intent, 0)
                            setOnClickPendingIntent(R.id.lo_widget_container, pendingIntent)
                        }
                    appWidgetManager.updateAppWidget(widgetId, remoteView)
                }
            }
            super.onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }
}