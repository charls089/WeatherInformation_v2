package com.kobbi.weather.info.ui.view.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
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
}