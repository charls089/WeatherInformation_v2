package com.kobbi.weather.info.presenter.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.kobbi.weather.info.util.Notificator

class LocationService : Service() {

    private val mBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        internal val service: LocationService get() = this@LocationService
    }

    override fun onBind(intent: Intent?): IBinder? = mBinder

    fun startF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificator = Notificator.getInstance()
            val notification = notificator.getNotification(
                applicationContext,
                Notificator.ChannelType.TYPE_POLARIS
            )
            startForeground(notificator.getNotificationId(), notification)
        }
    }

    fun stopF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            stopForeground(true)
    }
}