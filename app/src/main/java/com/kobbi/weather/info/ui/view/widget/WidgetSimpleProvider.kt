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
import kotlin.concurrent.thread

class WidgetSimpleProvider : AppWidgetProvider() {
    companion object {
        private const val TAG = "WidgetSimpleProvider"
        private const val REFRESH_WIDGET_ACTION = ".action.refresh.widget.data"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        DLog.writeLogFile(
            context, TAG,
            "WidgetSimpleProvider.onUpdate() --> appWidgetIds : ${appWidgetIds.toList()}"
        )
        thread {
            getRemoteViews(context, appWidgetIds[0])?.run {
                updateAppWidget(context, this)
            }
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        DLog.e(TAG, "onDeleted() --> ids : ${appWidgetIds?.toList()}")
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        thread {
            getRemoteViews(context, appWidgetId)?.run {
                updateAppWidget(context, this)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        val action = intent?.action
        if (action == getAction(context)) {
            val id = intent.extras?.getInt("id")
            id?.let {
                thread {
                    getRemoteViews(context, id)?.run {
                        updateAppWidget(context, this)
                    }
                }
            }
        }
    }

    private fun getRemoteViews(context: Context, appWidgetId: Int): RemoteViews? {
        val weatherRepository = WeatherRepository.getInstance(context)
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
                    R.id.tv_widget_update_time,
                    String.format(
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
                            setTextViewText(
                                R.id.tv_widget_dust_pm10,
                                getAirValue(context, AirCode.PM10, pm10)
                            )
                            setTextViewText(
                                R.id.tv_widget_dust_pm25,
                                getAirValue(context, AirCode.PM25, pm25)
                            )
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

    private fun updateAppWidget(context: Context, remoteViews: RemoteViews) {
        AppWidgetManager.getInstance(context).run {
            updateAppWidget(
                ComponentName(context, WidgetSimpleProvider::class.java), remoteViews
            )
        }
    }

    private fun getPendingIntent(context: Context, appWidgetId: Int): PendingIntent {
        context.applicationContext.let {
            val intent = Intent(it, WidgetSimpleProvider::class.java).apply {
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
}