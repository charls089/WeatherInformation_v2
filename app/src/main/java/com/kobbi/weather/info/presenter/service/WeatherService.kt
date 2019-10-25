package com.kobbi.weather.info.presenter.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.kobbi.weather.info.R
import com.kobbi.weather.info.presenter.WeatherApplication
import com.kobbi.weather.info.presenter.listener.LocationListener
import com.kobbi.weather.info.presenter.location.LocationManager
import com.kobbi.weather.info.presenter.model.type.OfferType
import com.kobbi.weather.info.presenter.repository.ApiRequestRepository
import com.kobbi.weather.info.presenter.repository.WeatherRepository
import com.kobbi.weather.info.ui.view.activity.SplashActivity
import com.kobbi.weather.info.util.*
import kotlin.concurrent.thread

class WeatherService : Service() {
    companion object {
        private const val TAG = "WeatherService"
    }

    private val weatherRepository by lazy { WeatherRepository.getInstance(applicationContext) }
    private val mBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        internal val service: WeatherService get() = this@WeatherService
    }

    override fun onBind(intent: Intent): IBinder = mBinder

    @Synchronized
    fun runService(init: Boolean) {
        DLog.d(TAG, "runService() - init : $init")
        applicationContext?.let { context ->
            WeatherApplication.setUpdateCheckTime(context)
            if (SharedPrefHelper.getBool(context, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION))
                requestLocation()
            requestWeather(init)
        }
    }

    fun echoService() {
        DLog.writeLogFile(applicationContext, TAG, "echoService")
        startF()
        stopF()
    }

    fun notifyMyLocation() {
        thread {
            weatherRepository.getWeatherInfo()?.run {
                Notificator.getInstance().showNotification(
                    applicationContext,
                    Notificator.ChannelType.TYPE_,
                    String.format(
                        getString(R.string.holder_weather_notify),
                        tpr,
                        wct,
                        yesterdayWct,
                        tmn,
                        tmx,
                        address
                    ),
                    getString(R.string.info_more_weather_info_message),
                    WeatherUtils.getSkyIcon(dateTime, pty, sky),
                    PendingIntent.getActivity(
                        applicationContext,
                        0,
                        Intent(applicationContext, SplashActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        },
                        0
                    )
                )
            }
        }
    }

    private fun requestLocation() {
        applicationContext?.let { context ->
            startF()
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
                    DLog.writeLogFile(
                        context, TAG,
                        "getLocation.onComplete() --> responseCode : $responseCode, message : $message"
                    )
                }
            })
            stopF()
        }
    }

    private fun requestWeather(init: Boolean) {
        OfferType.values().forEach { type ->
            if (init || OfferType.isNeedToUpdate(type) || type == OfferType.YESTERDAY) {
                applicationContext?.let { context ->
                    thread {
                        when (type) {
                            OfferType.CURRENT, OfferType.DAILY -> {
                                weatherRepository.loadAllGridData().forEach { gridData ->
                                    ApiRequestRepository.requestWeather(
                                        context,
                                        type,
                                        gridData
                                    )
                                }
                            }
                            OfferType.WEEKLY -> {
                                weatherRepository.loadAllAreaCode().forEach { areaCode ->
                                    ApiRequestRepository.requestMiddle(
                                        context,
                                        ApiConstants.API_MIDDLE_LAND_WEATHER,
                                        areaCode
                                    )
                                    ApiRequestRepository.requestMiddle(
                                        context,
                                        ApiConstants.API_MIDDLE_TEMPERATURE,
                                        areaCode
                                    )
                                }
                            }
                            OfferType.LIFE -> {
                                weatherRepository.loadAllAreaNo().forEach { areaNo ->
                                    ApiRequestRepository.requestLife(context, areaNo)
                                }
                            }
                            OfferType.AIR -> {
                                weatherRepository.loadAllCityName().forEach { cityName ->
                                    ApiRequestRepository.requestAirMeasure(
                                        context,
                                        cityName
                                    )
                                }
                            }
                            OfferType.BASE -> {
                                ApiRequestRepository.requestNews(context)
                            }
                            OfferType.YESTERDAY -> {
                                weatherRepository.loadAllGridData().forEach { gridData ->
                                    if (weatherRepository.findYesterdayWeather(
                                            gridData.x,
                                            gridData.y
                                        ) == null
                                    ) {
                                        ApiRequestRepository.requestWeather(
                                            context,
                                            type,
                                            gridData
                                        )
                                    }
                                }
                            }
                            OfferType.MINMAX -> {
                                //Nothing
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DLog.d(TAG, "startForeground")
            val notificator = Notificator.getInstance()
            val type = Notificator.ChannelType.TYPE_POLARIS
            val notification = notificator.getNotification(applicationContext, type)
            startForeground(notificator.getNotificationId(type), notification)
        }
    }

    private fun stopF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DLog.d(TAG, "stopForeground")
            stopForeground(true)
        }
    }
}
