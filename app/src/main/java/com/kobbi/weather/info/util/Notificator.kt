package com.kobbi.weather.info.util

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationCompat


class Notificator private constructor() {
    enum class ChannelType constructor(
        val channelId: String,
        val channelName: String,
        val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ) {
        TYPE_DEFAULT("NotificationChannel", "NotificationService"),
        TYPE_POLARIS("PolarisChannel", "Polaris", NotificationManager.IMPORTANCE_LOW)
    }

    companion object {
        private var sNotificator: Notificator? = null

        @JvmStatic
        fun getInstance(): Notificator {
            return sNotificator ?: synchronized(this) {
                Notificator().also {
                    sNotificator = it
                }
            }
        }
    }

    @JvmOverloads
    fun showNotification(
        context: Context,
        channelType: ChannelType,
        title: String,
        message: String,
        icon: Int = context.applicationContext.applicationInfo.icon,
        pendingIntent: PendingIntent? = null
    ) {
        val notification = getNotification(context, channelType, title, message, icon, pendingIntent)
        val manager = getNotificationManager(context)
        val notificationId = getNotificationId()
        manager.notify(notificationId, notification)
    }

    @JvmOverloads
    fun getNotification(
        context: Context,
        channelType: ChannelType,
        title: String? = null,
        message: String? = null,
        icon: Int = context.applicationContext.applicationInfo.icon,
        pendingIntent: PendingIntent? = null
    ): Notification {
        val applicationContext = context.applicationContext
        val builder: NotificationCompat.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(applicationContext, channelType)
            NotificationCompat.Builder(applicationContext, channelType.channelId)
        } else {
            NotificationCompat.Builder(applicationContext)
        }
        if (title != null && message != null)
            with(builder) {
                setContentTitle(title)
                setContentText(message)
                setSmallIcon(icon)
                setAutoCancel(true)

                pendingIntent?.let {
                    setContentIntent(pendingIntent)
                }
            }

        return builder.build()
    }

    fun getNotificationId() = SystemClock.elapsedRealtime().toInt()

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel(
        context: Context,
        channelType: ChannelType
    ) {
        val notificationChannel =
            NotificationChannel(channelType.channelId, channelType.channelName, channelType.importance)
        val manager = getNotificationManager(context)
        manager.createNotificationChannel(notificationChannel)
    }

    private fun getNotificationManager(context: Context) =
        context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}