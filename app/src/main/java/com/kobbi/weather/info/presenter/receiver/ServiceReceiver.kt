package com.kobbi.weather.info.presenter.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kobbi.weather.info.presenter.service.ServiceManager
import com.kobbi.weather.info.util.DLog

class ServiceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            DLog.writeLogFile(
                context,
                "ServiceReceiver",
                "context : $context / action : ${intent?.action}"
            )
            intent?.action?.let { action ->
                when (action) {
                    Intent.ACTION_BOOT_COMPLETED -> ServiceManager.restartService(it, true)
                    ServiceManager.getAction(context, ServiceManager.ACTION_RESTART) -> ServiceManager.restartService(
                        it,
                        false
                    )
                    ServiceManager.getAction(context, ServiceManager.ACTION_NOTIFY) -> ServiceManager.notifyWeather()
                }
            }
        }
    }
}