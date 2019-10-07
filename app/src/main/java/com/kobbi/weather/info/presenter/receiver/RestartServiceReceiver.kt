package com.kobbi.weather.info.presenter.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kobbi.weather.info.presenter.service.ServiceManager
import com.kobbi.weather.info.util.DLog

class RestartServiceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            DLog.writeLogFile(
                context,
                "RestartServiceReceiver",
                "context : $context / action : ${intent?.action}"
            )
            intent?.action?.let { action ->
                when (action) {
                    Intent.ACTION_BOOT_COMPLETED -> ServiceManager.restartService(it, true)
                    ServiceManager.getRestartAction(context) -> ServiceManager.restartService(
                        it,
                        false
                    )
                }
            }
        }
    }
}