package com.kobbi.weather.info.ui.view.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
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
        context.startService(Intent(context, WidgetService::class.java))
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}