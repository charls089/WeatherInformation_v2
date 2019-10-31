package com.kobbi.weather.info.ui.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.core.os.postDelayed
import com.kobbi.weather.info.R
import com.kobbi.weather.info.presenter.model.type.AirCode
import com.kobbi.weather.info.presenter.repository.WeatherRepository
import com.kobbi.weather.info.ui.view.activity.MainActivity
import com.kobbi.weather.info.util.DLog
import com.kobbi.weather.info.util.LocationUtils
import com.kobbi.weather.info.util.Utils
import com.kobbi.weather.info.util.WeatherUtils

class WidgetProvider : BaseWidgetProvider() {

    override fun createRemoteViews(context: Context): RemoteViews? {
        DLog.d("WidgetProvider", "createRemoteViews()")
        val options =
            AppWidgetManager.getInstance(context).getAppWidgetOptions(getWidgetId(context))
        return getWidgetWidth(context, options)?.let { wigetWidth ->
            getRemoteViews(context, wigetWidth)
        }
    }

    private fun getRemoteViews(context: Context, widgetWidth: Int): RemoteViews? {
        val weatherRepository = WeatherRepository.getInstance(context)
        return weatherRepository.getWeatherInfo()?.run {
            val resId =
                if (widgetWidth > 3) R.layout.widget_weather_horizontal else R.layout.widget_weather_vertical
            RemoteViews(context.packageName, resId).apply {
                val splitAddress = LocationUtils.splitAddressLine(address)
                val cityName = splitAddress.lastOrNull()
                fun setTextDynamic(viewDip: ViewDip, textValue: String?) {
                    with(viewDip) {
                        setTextViewText(id, textValue)
                        setTextViewTextSize(
                            id, TypedValue.COMPLEX_UNIT_DIP, getDip(widgetWidth, size)
                        )
                    }
                }
                setTextDynamic(ViewDip.ADDRESS, cityName)
                setTextDynamic(
                    ViewDip.TEMPERATURE,
                    String.format(context.getString(R.string.holder_current_tpr), tpr)
                )
                setTextDynamic(
                    ViewDip.SENSORY_TPR,
                    String.format(context.getString(R.string.holder_sensory_tpr), wct)
                )
                setTextDynamic(
                    ViewDip.UPDATE_TIME, String.format(
                        context.getString(R.string.holder_update_time),
                        Utils.convertDateTime(type = Utils.DateType.SHORT)
                    )
                )
                setImageViewResource(
                    R.id.iv_widget_sky, WeatherUtils.getSkyIcon(dateTime, pty, sky)
                )

                if (splitAddress.size >= 2) {
                    weatherRepository
                        .findAirMeasureData(splitAddress[0], splitAddress[1]).run {
                            setTextDynamic(ViewDip.PM10, getAirValue(context, AirCode.PM10, pm10))
                            setTextDynamic(ViewDip.PM25, getAirValue(context, AirCode.PM25, pm25))
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
                setOnClickPendingIntent(
                    R.id.lo_refresh_container, getPendingIntent(context, this@WidgetProvider.javaClass)
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

    private fun getAirValue(
        context: Context,
        code: AirCode,
        dustValue: String
    ): String {
        val level = AirCode.getAirLevel(code, dustValue)
        val holder =
            if (dustValue.isEmpty()) R.string.holder_dust_value_empty else R.string.holder_dust_value
        return String.format(context.getString(holder), code.codeName, level, dustValue)
    }
}