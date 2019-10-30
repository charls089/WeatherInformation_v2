package com.kobbi.weather.info.ui.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.RemoteViews
import androidx.core.os.postDelayed
import com.kobbi.weather.info.R
import com.kobbi.weather.info.presenter.repository.WeatherRepository
import com.kobbi.weather.info.ui.view.activity.MainActivity
import com.kobbi.weather.info.util.LocationUtils
import com.kobbi.weather.info.util.Utils
import com.kobbi.weather.info.util.WeatherUtils

class SimpleWidgetProvider : BaseWidgetProvider() {
    override fun createRemoteViews(context: Context): RemoteViews? {
        return getRemoteViews(context)
    }

    private fun getRemoteViews(context: Context): RemoteViews? {
        val weatherRepository = WeatherRepository.getInstance(context.applicationContext)
        return weatherRepository.getWeatherInfo()?.run {
            RemoteViews(context.packageName, R.layout.widget_weather_simple).apply {
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
                    R.id.tv_widget_update_time, Utils.convertDateTime(type = Utils.DateType.SHORT)
                )
                setImageViewResource(
                    R.id.iv_widget_sky, WeatherUtils.getSkyIcon(dateTime, pty, sky)
                )

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
                setOnClickPendingIntent(
                    R.id.tv_widget_update_time,
                    getPendingIntent(context, this@SimpleWidgetProvider.javaClass)
                )
                setViewVisibility(R.id.pb_widget, View.VISIBLE)
                setViewVisibility(R.id.lo_widget_container, View.GONE)
                Handler(Looper.getMainLooper()).postDelayed(500) {
                    setViewVisibility(R.id.pb_widget, View.GONE)
                    setViewVisibility(R.id.lo_widget_container, View.VISIBLE)
                    updateAppWidget(context, this)
                }
            }
        }
    }
}