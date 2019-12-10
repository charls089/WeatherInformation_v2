package com.kobbi.weather.info.ui.view.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.RemoteViews
import androidx.core.os.postDelayed
import com.kobbi.weather.info.R
import com.kobbi.weather.info.presenter.model.data.WeatherInfo
import com.kobbi.weather.info.ui.view.activity.MainActivity
import com.kobbi.weather.info.util.DLog
import com.kobbi.weather.info.util.LocationUtils
import com.kobbi.weather.info.util.Utils
import com.kobbi.weather.info.util.WeatherUtils

class SimpleWidgetProvider : BaseWidgetProvider() {
    override fun createRemoteViews(context: Context, weatherInfo: WeatherInfo): RemoteViews {
        DLog.d("SimpleWidgetProvider", "createRemoteViews()")
        return getRemoteViews(context, weatherInfo)
    }

    private fun getRemoteViews(context: Context, weatherInfo: WeatherInfo): RemoteViews {
        return weatherInfo.run {
            RemoteViews(context.packageName, R.layout.widget_weather_simple).apply {
                val splitAddress = LocationUtils.splitAddressLine(address)
                val cityName = splitAddress.lastOrNull()
                setTextViewText(R.id.tv_widget_address, cityName)
                setTextViewText(R.id.tv_widget_tpr, tpr)
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
            }
        }
    }
}