package com.kobbi.weather.info.ui.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
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
import kotlin.concurrent.thread
import kotlin.math.absoluteValue

class WidgetProvider : AppWidgetProvider() {
    enum class ViewDip(
        val id: Int,
        val size: Int
    ) {
        ADDRESS(R.id.tv_widget_address, 8),
        TEMPERATURE(R.id.tv_widget_tpr, 12),
        SENSORY_TPR(R.id.tv_widget_wct, 5),
        UPDATE_TIME(R.id.tv_widget_update_time, 4),
        PM10(R.id.tv_widget_dust_pm10, 4),
        PM25(R.id.tv_widget_dust_pm25, 4)

    }

    companion object {
        private const val TAG = "WidgetProvider"
        private const val REFRESH_WIDGET_ACTION = ".action.refresh.widget.data"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        DLog.writeLogFile(
            context, TAG,
            "WidgetProvider.onUpdate() --> appWidgetIds : ${appWidgetIds.toList()}"
        )
        val options = appWidgetManager.getAppWidgetOptions(appWidgetIds[0])
        getWidgetWidth(context, options)?.let { resId ->
            thread {
                getRemoteViews(context, resId, appWidgetIds[0])?.run {
                    updateAppWidget(context, this)
                }
            }
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        getWidgetWidth(context, newOptions)?.let { resId ->
            thread {
                getRemoteViews(context, resId, appWidgetId)?.run {
                    updateAppWidget(context, this)
                }
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        val action = intent?.action
        if (action == getAction(context)) {
            val id = intent.extras?.getInt("id")
            id?.let {
                val options =
                    AppWidgetManager.getInstance(context).getAppWidgetOptions(id)
                getWidgetWidth(context, options)?.let { resId ->
                    thread {
                        getRemoteViews(context, resId, id)?.run {
                            updateAppWidget(context, this)
                        }
                    }
                }
            }
        }
    }

    private fun getRemoteViews(context: Context, widthSize: Int, appWidgetId: Int): RemoteViews? {
        val weatherRepository = WeatherRepository.getInstance(context)
        return weatherRepository.getWeatherInfo()?.run {
            val resId =
                if (widthSize > 3) R.layout.widget_weather else R.layout.widget_weather_vertical
            RemoteViews(context.packageName, resId).apply {
                val splitAddress = LocationUtils.splitAddressLine(address)
                val cityName = splitAddress.lastOrNull()
                fun setTextDynamic(viewDip: ViewDip, textValue: String?) {
                    with(viewDip) {
                        setTextViewText(id, textValue)
                        setTextViewTextSize(
                            id, TypedValue.COMPLEX_UNIT_DIP, getDip(widthSize, size)
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
                            setTextDynamic(ViewDip.PM25, getAirValue(context, AirCode.PM10, pm25))
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
                    R.id.tv_widget_update_time, getPendingIntent(context, appWidgetId)
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

    private fun getWidgetWidth(context: Context?, options: Bundle?): Int? {
        options?.run {
            val maxWidth = getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
            val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            windowManager?.run {
                val metrics = DisplayMetrics()
                defaultDisplay.getMetrics(metrics)
                val xdpi = metrics.xdpi.toInt() / 5
                var count = 0
                var tmp = Int.MAX_VALUE
                for (i in 1..5) {
                    val gap = (maxWidth - xdpi * i).absoluteValue
                    if (gap < tmp) {
                        tmp = gap
                        count++
                    }
                }
                return count
            }
        }
        return null
    }

    private fun updateAppWidget(context: Context, remoteViews: RemoteViews) {
        AppWidgetManager.getInstance(context).run {
            updateAppWidget(
                ComponentName(context, WidgetProvider::class.java), remoteViews
            )
        }
    }

    private fun getPendingIntent(context: Context, appWidgetId: Int): PendingIntent {
        context.applicationContext.let {
            val intent = Intent(it, WidgetProvider::class.java).apply {
                action = getAction(it)
                this.putExtra("id", appWidgetId)
            }
            return PendingIntent.getBroadcast(it, appWidgetId, intent, 0)
        }
    }

    private fun getAction(context: Context) = context.packageName + REFRESH_WIDGET_ACTION

    private fun getAirValue(
        context: Context,
        code: AirCode,
        dustValue: String
    ): String {
        val level = AirCode.getAirLevel(code.codeNo, dustValue)
        val holder =
            if (dustValue.isEmpty()) R.string.holder_dust_value_empty else R.string.holder_dust_value
        return String.format(context.getString(holder), code.codeName, level, dustValue)
    }

    private fun getDip(widthSize: Int, value: Int): Float {
        return if (widthSize <= 3) 2 * value * 1.5f else 3 * value * 1.5f
    }
}