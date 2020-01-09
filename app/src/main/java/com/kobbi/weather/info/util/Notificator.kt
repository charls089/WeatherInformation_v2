package com.kobbi.weather.info.util

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.kobbi.weather.info.R


class Notificator private constructor() {
    enum class ChannelType(
        val channelId: String,
        val channelName: String,
        val importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
        val isRepeat: Boolean = true,
        val id: Int = -1
    ) {
        DEFAULT("NotificationChannel", "NotificationService"),
        WEATHER("WeatherChannel", "Weather", isRepeat = false, id = 703),
        POLARIS("PolarisChannel", "Polaris", NotificationManager.IMPORTANCE_LOW)
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
        icon: Int? = null,
        pendingIntent: PendingIntent? = null
    ) {
        val notification =
            getNotification(context, channelType, title, message, icon, pendingIntent)
        val manager = getNotificationManager(context)
        val notificationId = getNotificationId(channelType)
        manager.notify(notificationId, notification)
    }

    @JvmOverloads
    fun getNotification(
        context: Context,
        channelType: ChannelType,
        title: String? = null,
        message: String? = null,
        icon: Int? = null,
        pendingIntent: PendingIntent? = null
    ): Notification {
        val applicationContext = context.applicationContext
        val builder: NotificationCompat.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel(applicationContext, channelType)
                NotificationCompat.Builder(applicationContext, channelType.channelId)
            } else {
                NotificationCompat.Builder(applicationContext)
            }
        with(builder) {
            setSmallIcon(R.drawable.notify_img2)
            if (title != null && message != null) {
                setContentTitle(title)
                setContentText(message)
                setAutoCancel(true)
                if (icon != null)
                    setLargeIcon(BitmapFactory.decodeResource(context.resources, icon))

                pendingIntent?.let {
                    setContentIntent(pendingIntent)
                }
            }
        }

        return builder.build()
    }

    fun getNotificationId(type: ChannelType) =
        if (type.isRepeat) SystemClock.elapsedRealtime().toInt() + 1 else type.id


    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel(
        context: Context,
        channelType: ChannelType
    ) {
        val notificationChannel =
            NotificationChannel(
                channelType.channelId,
                channelType.channelName,
                channelType.importance
            )
        val manager = getNotificationManager(context)
        manager.createNotificationChannel(notificationChannel)
    }

    private fun getNotificationManager(context: Context) =
        context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}