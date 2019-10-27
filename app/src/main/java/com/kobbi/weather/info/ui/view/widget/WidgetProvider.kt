package com.kobbi.weather.info.ui.view.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.kobbi.weather.info.presenter.service.WidgetService
import com.kobbi.weather.info.util.DLog

class WidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        DLog.writeLogFile(
            context,
            "WigetProvider",
            "WidgetProvider.onUpdate() --> appWidgetIds : ${appWidgetIds.toList()}"
        )
        val intent = Intent(context, WidgetService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val metrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(metrics)
        Log.e("####", "${metrics.widthPixels}/${metrics.heightPixels}")


        newOptions?.run {
            val minWidth = getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
            val minHeight = getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
            val maxWidth = getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
            val maxHeight = getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
            Log.e(
                "####",
                "minWidth : $minWidth, minHeight : $minHeight, maxWidth : $maxWidth, maxHeight : $maxHeight"
            )


        }
    }
}