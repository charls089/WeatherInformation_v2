package com.kobbi.weather.info.presenter.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.kobbi.weather.info.util.DLog
import com.kobbi.weather.info.util.Notificator

class UpDownService : Service() {
    companion object {
        private const val TAG = "UpDownService"
    }

    private val mBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        internal val service: UpDownService get() = this@UpDownService
    }

    override fun onBind(intent: Intent): IBinder = mBinder

    fun echo() {
        DLog.v(applicationContext, TAG, "echo")
        upService()
        downService()
    }

    fun upService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DLog.d(tag = TAG, message =  "up")
            val notificator = Notificator.getInstance()
            val type = Notificator.ChannelType.POLARIS
            val notification = notificator.getNotification(applicationContext, type)
            startForeground(notificator.getNotificationId(type), notification)
        }
    }

    fun downService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DLog.d(tag = TAG, message =  "down")
            stopForeground(true)
        }
    }
}