package com.kobbi.weather.info.presenter.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import com.kobbi.weather.info.presenter.WeatherApplication
import com.kobbi.weather.info.presenter.receiver.RestartServiceReceiver
import com.kobbi.weather.info.util.DLog
import com.kobbi.weather.info.util.SharedPrefHelper

object ServiceManager {
    private const val ACTION_RESTART = ".action.restart.weather.service"
    private const val RESTART_SERVICE_INTERVAL = 1 * 60 * 1000L
    private const val CHECK_WEATHER_INFO_INTERVAL = 5 * 60 * 1000L

    private const val TAG = "ServiceManager"

    private var mWeatherService: WeatherService? = null
    private var mLocationService: LocationService? = null

    private val mWeatherServiceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            DLog.d(TAG, "WeatherService was disconnected.")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            DLog.d(TAG, "WeatherService was connected.")
            val binder = service as WeatherService.LocalBinder
            mWeatherService = binder.service
            getWeatherInfo()
        }
    }

    private val mLocationServiceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            DLog.d(TAG, "LocationService was disconnected.")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            DLog.d(TAG, "LocationService was connected.")
            val binder = service as LocationService.LocalBinder
            mLocationService = binder.service
            echo()
        }
    }

    fun restartService(context: Context, init: Boolean) {
        context.applicationContext?.let {
            if (init) {
                registerRestartReceiver(it)
            }

            bindService(it, LocationService::class.java, mLocationServiceConnection)
            bindService(it, WeatherService::class.java, mWeatherServiceConnection)
            echo()

            val beforeCheckTime = WeatherApplication.getUpdateCheckTime(it)
            if (System.currentTimeMillis() - beforeCheckTime > CHECK_WEATHER_INFO_INTERVAL)
                getWeatherInfo()
        }
    }

    @Synchronized
    fun getWeatherInfo(init: Boolean = false) {
        val serviceRunning = WeatherApplication.getServiceRunning()
        DLog.d(TAG, "getWeatherInfo() - WeatherService is Running : $serviceRunning")
        if (serviceRunning)
            return
        WeatherApplication.setServiceRunning(true)
        mWeatherService?.runService(init) ?: WeatherApplication.setServiceRunning(false)
    }

    fun startF() {
        mLocationService?.startF()
    }

    fun stopF() {
        mLocationService?.stopF()
    }

    fun getRestartAction(context: Context) = context.packageName + ACTION_RESTART

    private fun registerRestartReceiver(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RestartServiceReceiver::class.java).apply {
            action = getRestartAction(context)
        }
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime(),
            RESTART_SERVICE_INTERVAL,
            pendingIntent
        )
    }

    private fun bindService(
        context: Context,
        clazz: Class<*>,
        serviceConnection: ServiceConnection
    ) {
        Intent(context, clazz).run {
            context.bindService(this, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun echo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startF()
            stopF()
        }
    }

}