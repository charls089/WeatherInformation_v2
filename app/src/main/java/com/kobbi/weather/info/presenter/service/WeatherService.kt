package com.kobbi.weather.info.presenter.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock
import com.kobbi.weather.info.R
import com.kobbi.weather.info.presenter.WeatherApplication
import com.kobbi.weather.info.presenter.listener.CompleteListener
import com.kobbi.weather.info.presenter.listener.LocationListener
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

class WeatherService : Service() {
    companion object {
        private const val TAG = "WeatherService"
        private const val ACTION_RETRY = "com.kobbi.weather.info.action.retry"
    }

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
            Timer().schedule(1000) {
                mIsRunning = false
            }
        }
    }

    private val weatherRepository by lazy { WeatherRepository.getInstance(applicationContext) }
    private val mBinder = LocalBinder()
    private var mIsRunning = false

    inner class LocalBinder : Binder() {
        internal val service: WeatherService get() = this@WeatherService
    }

    override fun onBind(intent: Intent): IBinder = mBinder

    fun runService(init: Boolean) {
        if (mIsRunning)
            return
        mIsRunning = true
        DLog.d(tag = TAG, message = "runService() - init : $init")
        applicationContext?.let { context ->
            WeatherApplication.setUpdateCheckTime(context)
            if (SharedPrefHelper.getBool(context, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION))
                requestLocation()
            requestAllWeather(init)
        }
    }

    fun notifyMyLocation() {
        applicationContext?.let { context ->
            val isUse =
                SharedPrefHelper.getBool(context, SharedPrefHelper.KEY_AGREE_TO_USE_NOTIFICATION)
            DLog.d(tag = TAG, message = "notifyWeather() - isUse : $isUse")
            if (isUse)
                thread {
                    val locatedArea = weatherRepository.loadLocatedArea()
                    weatherRepository.loadWeatherInfo(locatedArea)?.run {
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
                }
        }
    }

    private fun requestLocation() {
        applicationContext?.let { context ->
            LocationManager.getLocation(context, object : LocationListener {
                override fun onComplete(responseCode: Int, location: Location?) {
                    var message = ""
                    when (responseCode) {
                        LocationManager.RESPONSE_NO_ERROR -> {
                            location?.let {
                                val time =
                                    Utils.getCurrentTime("yyyy-MM-dd, HH:mm:ss", it.time)
                                val address =
                                    LocationUtils.getAddressLine(context, location)
                                val addrList = LocationUtils.splitAddressLine(address)
                                weatherRepository.insertArea(
                                    context,
                                    addrList,
                                    Constants.STATE_CODE_LOCATED
                                )
                                message =
                                    "time : $time, provider : ${it.provider}, LatLng:(${it.latitude},${it.longitude}), area : $address"
                            }
                        }
                        LocationManager.RESPONSE_LOCATION_TIMEOUT -> {
                            //위치 획득 시간초과
                            message = "Location Timeout"
                        }
                        LocationManager.RESPONSE_MISSING_PERMISSION -> {
                            //권한 요청 로직
                            WeatherApplication.setUpdateCheckTime(context, 0)
                            message = "Location needs runtime permission"
                        }
                    }
                    DLog.i(
                        context, TAG,
                        "getLocation.onComplete() --> responseCode : $responseCode, message : $message"
                    )
                }
            })
        }
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
                            weatherRepository.loadAllGridData().forEach { gridData ->
                                ApiRequestRepository.requestWeather(
                                    context, type, gridData, mListener
                                )
                            }
                        }
                        OfferType.WEEKLY -> {
                            weatherRepository.loadAllAreaCode().forEach { areaCode ->
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
                            weatherRepository.loadAllAreaNo().forEach { areaNo ->
                                ApiRequestRepository.requestLife(
                                    context, type, areaNo, mListener
                                )
                            }
                        }
                        OfferType.AIR -> {
                            weatherRepository.loadAllSidoName().forEach { sidoName ->
                                ApiRequestRepository.requestAirMeasure(
                                    context, sidoName, mListener
                                )
                            }
                        }
                        OfferType.BASE -> {
                            ApiRequestRepository.requestNews(context, mListener)
                        }
                        OfferType.YESTERDAY -> {
                            weatherRepository.loadAllGridData().forEach { gridData ->
                                if (
                                    weatherRepository.findYesterdayWeather(
                                        gridData.x,
                                        gridData.y
                                    ) == null
                                ) {
                                    ApiRequestRepository.requestWeather(
                                        context, type, gridData, mListener
                                    )
                                }
                            }
                        }
                        OfferType.MIN_MAX -> {
                            weatherRepository.loadAllGridData().forEach { gridData ->
                                val minMaxTpr =
                                    weatherRepository.findMinMaxTpr(gridData.x, gridData.y)
                                if (minMaxTpr.size != 2) {
                                    ApiRequestRepository.requestWeather(
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
}
