package com.kobbi.weather.info.presenter.service

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Build
import android.os.SystemClock
import com.kobbi.weather.info.R
import com.kobbi.weather.info.presenter.WeatherApplication
import com.kobbi.weather.info.presenter.listener.CompleteListener
import com.kobbi.weather.info.presenter.listener.LocationCompleteListener
import com.kobbi.weather.info.presenter.location.LocationManager
import com.kobbi.weather.info.presenter.model.type.OfferType
import com.kobbi.weather.info.presenter.model.type.ReturnCode
import com.kobbi.weather.info.presenter.repository.ApiRequestRepository
import com.kobbi.weather.info.presenter.repository.WeatherRepository
import com.kobbi.weather.info.ui.view.activity.SplashActivity
import com.kobbi.weather.info.util.*
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.thread

class WeatherService : IntentService(WeatherService::class.simpleName) {
    companion object {
        private const val TAG = "WeatherService"
        private const val ACTION_RETRY = "com.kobbi.weather.info.action.retry"
    }

    private val mWeatherRepository by lazy { WeatherRepository.getInstance(applicationContext) }
    private val mListener = object : CompleteListener {
        override fun onComplete(code: ReturnCode, data: Any) {
            val message: String
            when (code) {
                ReturnCode.NO_ERROR -> {
                    message = getString(R.string.info_network_success)
                }
                ReturnCode.NOT_UPDATE_TIME -> {
                    message = getString(R.string.info_not_update_time)
                }
                ReturnCode.NETWORK_DISABLED -> {
                    message = getString(R.string.info_network_disabled)
                    if (data is OfferType)
                        registerRetryReceiver(data)
                }
                ReturnCode.SOCKET_TIMEOUT -> {
                    message = getString(R.string.info_network_timeout)
                    if (data is OfferType)
                        requestWeather(true, data)
                }
                ReturnCode.DATA_IS_NULL -> {
                    message = getString(R.string.info_data_is_empty)
                }
                else -> {
                    message = getString(R.string.info_network_unknown)
                }
            }
            if (data is OfferType) {
                DLog.d(applicationContext, TAG, "[$data] $message")
            }
        }
    }

    override fun onHandleIntent(workIntent: Intent?) {
        DLog.i(tag = TAG, message = "WeatherService2.onHandleIntent()")
        workIntent?.run {
            when (getIntExtra("type", -1)) {
                -1 -> {

                }
                0 -> {
                    runService(true)
                }
                1 -> {
                    notifyMyLocation()
                }
                2 -> {
                    runService(false)
                }
                else -> {

                }
            }
        }
    }

    private fun runService(init: Boolean) {
        DLog.d(tag = TAG, message = "runService() - init : $init")
        applicationContext?.let { context ->
            startService()
            WeatherApplication.setUpdateCheckTime(context)
            if (SharedPrefHelper.getBool(context, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION))
                requestLocation()
            requestAllWeather(init)
        }
    }

    private fun requestLocation() {
        applicationContext?.let { context ->
            LocationManager.getLocation(context, object : LocationCompleteListener {
                override fun onComplete(
                    responseCode: LocationManager.ResponseCode,
                    location: Location?
                ) {
                    if (LocationManager.ResponseCode.isComplete(responseCode)) {
                        location?.let {
                            val address =
                                LocationUtils.getAddressLine(context, location)
                            val addrList = LocationUtils.splitAddressLine(address)
                            mWeatherRepository.insertArea(
                                context,
                                addrList,
                                Constants.STATE_CODE_LOCATED
                            )
                        }
                    }
                    DLog.i(
                        context, TAG, "getLocation.onComplete() --> responseCode : $responseCode"
                    )
                }
            })
        }
    }

    private fun notifyMyLocation() {
        applicationContext?.let { context ->
            startService()
            val isUse =
                SharedPrefHelper.getBool(context, SharedPrefHelper.KEY_AGREE_TO_USE_NOTIFICATION)
            DLog.d(tag = TAG, message = "notifyWeather() - isUse : $isUse")
            if (isUse)
                thread {
                    val locatedArea = mWeatherRepository.loadLocatedArea()
                    mWeatherRepository.loadWeatherInfo(locatedArea)?.run {
                        Notificator.getInstance().showNotification(
                            context,
                            Notificator.ChannelType.WEATHER,
                            address,
                            String.format(
                                getString(R.string.holder_weather_notify), tpr, wct
                            ),
                            WeatherUtils.getSkyIcon(dateTime, pty, sky),
                            PendingIntent.getActivity(
                                context,
                                0,
                                Intent(context, SplashActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                },
                                0
                            )
                        )
                    }
                    stopService()
                }
            else
                stopService()
        } ?: run { stopService() }
    }

    private fun requestAllWeather(init: Boolean) {
        OfferType.values().forEach { type ->
            requestWeather(init, type)
        }
    }

    private fun requestWeather(init: Boolean, type: OfferType) {
        if (init || OfferType.isNeedToUpdate(type)) {
            applicationContext?.let { context ->
                thread {
                    when (type) {
                        OfferType.CURRENT, OfferType.DAILY -> {
                            mWeatherRepository.loadAllGridData().forEach { gridData ->
                                ApiRequestRepository.requestVillage(
                                    context, type, gridData, mListener
                                )
                            }
                        }
                        OfferType.WEEKLY -> {
                            mWeatherRepository.loadAllAreaCode().forEach { areaCode ->
                                ApiRequestRepository.requestMiddle(
                                    context,
                                    ApiConstants.API_MIDDLE_LAND_WEATHER,
                                    areaCode,
                                    mListener
                                )
                                ApiRequestRepository.requestMiddle(
                                    context,
                                    ApiConstants.API_MIDDLE_TEMPERATURE,
                                    areaCode,
                                    mListener
                                )
                            }
                        }
                        OfferType.LIFE_TIME, OfferType.LIFE_DAY -> {
                            mWeatherRepository.loadAllAreaNo().forEach { areaNo ->
                                ApiRequestRepository.requestLife(
                                    context, type, areaNo, mListener
                                )
                            }
                        }
                        OfferType.AIR -> {
                            mWeatherRepository.loadAllSidoName().forEach { sidoName ->
                                ApiRequestRepository.requestAirMeasure(
                                    context, sidoName, mListener
                                )
                            }
                        }
                        OfferType.BASE -> {
                            ApiRequestRepository.requestNews(context, mListener)
                        }
                        OfferType.YESTERDAY -> {
                            mWeatherRepository.loadAllGridData().forEach { gridData ->
                                if (
                                    mWeatherRepository.findYesterdayWeather(
                                        gridData.x,
                                        gridData.y
                                    ) == null
                                ) {
                                    ApiRequestRepository.requestVillage(
                                        context, type, gridData, mListener
                                    )
                                }
                            }
                        }
                        OfferType.MIN_MAX -> {
                            mWeatherRepository.loadAllGridData().forEach { gridData ->
                                val minMaxTpr =
                                    mWeatherRepository.findMinMaxTpr(gridData.x, gridData.y)
                                if (minMaxTpr.size != 2) {
                                    ApiRequestRepository.requestVillage(
                                        context,
                                        type,
                                        gridData,
                                        mListener
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            mListener.onComplete(ReturnCode.NOT_UPDATE_TIME)
        }
    }

    private fun registerRetryReceiver(type: OfferType) {
        (getSystemService(Context.ALARM_SERVICE) as? AlarmManager)?.run {
            this.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 60 * 1000L,
                60 * 1000L,
                PendingIntent.getBroadcast(
                    applicationContext, 10101, Intent(ACTION_RETRY).apply {
                        putExtra("offer_type", type)
                    }, 0
                )
            )
            registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    intent?.extras?.get("offer_type").let { type ->
                        if (type is OfferType) {
                            requestWeather(true, type)
                        }
                    }
                }
            }, IntentFilter(ACTION_RETRY))
        }
    }

    private fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DLog.d(tag = TAG, message = "start")
            val notificator = Notificator.getInstance()
            val type = Notificator.ChannelType.POLARIS
            val notification = notificator.getNotification(applicationContext, type)
            startForeground(notificator.getNotificationId(type), notification)
        }
    }

    private fun stopService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DLog.d(tag = TAG, message = "stop")
            stopForeground(true)
        }
        stopSelf()
    }
}