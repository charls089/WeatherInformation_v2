package com.kobbi.weather.info.ui.view.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.RemoteViews
import androidx.core.os.postDelayed
import com.kobbi.weather.info.R
import com.kobbi.weather.info.presenter.listener.CompleteListener
import com.kobbi.weather.info.presenter.model.data.WeatherInfo
import com.kobbi.weather.info.presenter.model.type.ReturnCode
import com.kobbi.weather.info.presenter.viewmodel.WidgetViewModel
import com.kobbi.weather.info.util.DLog
import com.kobbi.weather.info.util.SharedPrefHelper
import com.kobbi.weather.info.util.Utils
import kotlin.concurrent.thread
import kotlin.math.absoluteValue

abstract class BaseWidgetProvider : AppWidgetProvider() {
    enum class ViewDip(
        val id: Int,
        val size: Int
    ) {
        ADDRESS(R.id.tv_widget_address, 6),
        TEMPERATURE(R.id.tv_widget_tpr, 12),
        SENSORY_TPR(R.id.tv_widget_wct, 5),
        UPDATE_TIME(R.id.tv_widget_update_time, 4),
        PM10(R.id.tv_widget_dust_pm10, 4),
        PM25(R.id.tv_widget_dust_pm25, 4)
    }

    companion object {
        private const val TAG = "BaseWidgetProvider"
        private const val REFRESH_WIDGET_ACTION = ".action.refresh.widget.data"
    }

    abstract fun createRemoteViews(context: Context, weatherInfo: WeatherInfo): RemoteViews

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        DLog.writeLogFile(
            context, TAG, "onUpdate() --> appWidgetIds : ${appWidgetIds.toList()}"
        )
        DLog.d(TAG, "onUpdate() --> getWidgetId : ${getWidgetId(context)}")
        if (getWidgetId(context) == Int.MIN_VALUE)
            setWidgetId(context, appWidgetIds[0])
        updateAppWidget(context)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        updateAppWidget(context)
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        val action = intent?.action
        DLog.writeLogFile(context, TAG, "onReceive() --> action : $action")
        if (action == getAction(context)) {
            updateAppWidget(context)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        DLog.writeLogFile(
            context, TAG, "onDeleted() --> appWidgetIds : ${appWidgetIds.toList()}"
        )
        super.onDeleted(context, appWidgetIds)
        resetWidgetId(context)
    }

    private fun setRemoteViews(context: Context) {
        showProgressView(context)
        WidgetViewModel(context).getWeatherInfo(object : CompleteListener {
            override fun onComplete(code: ReturnCode, data: Any) {
                DLog.writeLogFile(context, TAG, "setRemoteViews.onComplete() --> code : $code, data : $data")
                val remoteViews =
                    if (Utils.getNeedToRequestPermissions(context).isNotEmpty())
                        getErrPageView(context, R.string.info_widget_permission_not_checked)
                    else
                        when (code) {
                            ReturnCode.NO_ERROR -> {
                                if (data is WeatherInfo) {
                                    createRemoteViews(context, data)
                                } else {
                                    getErrPageView(context, R.string.info_widget_data_load_error)
                                }
                            }
                            ReturnCode.NETWORK_DISABLED -> {
                                getErrPageView(context, R.string.info_network_disabled)
                            }
                            ReturnCode.SOCKET_TIMEOUT -> {
                                getErrPageView(context, R.string.info_network_timeout)
                            }
                            else -> {
                                getErrPageView(context, R.string.info_widget_data_load_error)
                            }
                        }

                Handler(Looper.getMainLooper()).postDelayed(500) {
                    updateAppWidget(context, remoteViews)
                }
            }
        })
    }

    open fun getWidgetWidth(context: Context?, options: Bundle?): Int {
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
        return 2
    }

    open fun getDip(widthSize: Int, value: Int): Float {
        return if (widthSize <= 3) 2 * value * 1.5f else 3 * value * 1.5f
    }

    open fun getPendingIntent(context: Context, clazz: Class<*>): PendingIntent {
        context.applicationContext.let {
            val intent = Intent(it, clazz).apply {
                action = getAction(it)
            }
            return PendingIntent.getBroadcast(it, getWidgetId(context), intent, 0)
        }
    }

    open fun updateAppWidget(context: Context, remoteViews: RemoteViews) {
        DLog.writeLogFile(context, message = "updateAppWidget() --> remoteViews : $remoteViews")
        AppWidgetManager.getInstance(context)
            .updateAppWidget(getWidgetId(context), remoteViews)
    }

    private fun updateAppWidget(context: Context) {
        thread {
            setRemoteViews(context)
        }
    }

    private fun getErrPageView(context: Context, resId: Int): RemoteViews {
        DLog.d(TAG, "getErrPageView()")
        return RemoteViews(context.packageName, R.layout.widget_weather_error).apply {
            setOnClickPendingIntent(
                R.id.iv_error_refresh, getPendingIntent(context, getWidgetProvider(context))
            )
            setTextViewText(R.id.tv_widget_error, context.getString(resId))
        }
    }

    private fun showProgressView(context: Context) {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_loading)
        updateAppWidget(context, remoteViews)
    }

    private fun getWidgetProvider(context: Context): Class<out BaseWidgetProvider> {
        return AppWidgetManager.getInstance(context).getAppWidgetInfo(getWidgetId(context)).run {
            when (provider?.className) {
                WidgetProvider::class.java.name -> WidgetProvider::class.java
                else -> SimpleWidgetProvider::class.java
            }
        }
    }

    fun getWidgetId(context: Context): Int {
        return SharedPrefHelper.getInt(context, SharedPrefHelper.KEY_APP_WIDGET_ID)
    }

    private fun setWidgetId(context: Context, value: Int) {
        SharedPrefHelper.setInt(context, SharedPrefHelper.KEY_APP_WIDGET_ID, value)
    }

    private fun resetWidgetId(context: Context) {
        SharedPrefHelper.setInt(context, SharedPrefHelper.KEY_APP_WIDGET_ID, Int.MIN_VALUE)
    }

    private fun getAction(context: Context) = context.packageName + REFRESH_WIDGET_ACTION
}