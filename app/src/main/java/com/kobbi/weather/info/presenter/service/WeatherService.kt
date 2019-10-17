package com.kobbi.weather.info.presenter.service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.kobbi.weather.info.presenter.WeatherApplication
import com.kobbi.weather.info.presenter.listener.OnLocationListener
import com.kobbi.weather.info.presenter.location.LocationManager
import com.kobbi.weather.info.presenter.model.data.AreaCode
import com.kobbi.weather.info.presenter.model.data.GridData
import com.kobbi.weather.info.presenter.model.type.OfferType
import com.kobbi.weather.info.presenter.repository.ApiRequestRepository
import com.kobbi.weather.info.presenter.repository.WeatherRepository
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

    private fun requestLocation() {
        applicationContext?.let { context ->
            startF()
            LocationManager.getLocation(context, object : OnLocationListener {
                override fun onComplete(responseCode: Int, location: Location?) {
                    var message = ""
                    when (responseCode) {
                        LocationManager.RESPONSE_NO_ERROR -> {
                            location?.let {
                                val time = Utils.getCurrentTime("yyyy-MM-dd, HH:mm:ss", it.time)
                                val address = LocationUtils.splitAddressLine(context, location)
                                weatherRepository.insertArea(context, address)
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

    private fun checkUpdateTime(init: Boolean, typeUpdateTime: Boolean): Boolean {
        return init || typeUpdateTime
    }

    private fun requestWeather(init: Boolean) {
        val isCurrentUpdateTime = OfferType.isUpdateTime(OfferType.CURRENT)
        val isDailyUpdateTime = OfferType.isUpdateTime(OfferType.DAILY)
        val isWeeklyUpdateTime = OfferType.isUpdateTime(OfferType.WEEKLY)
        val isLifeUpdateTime = OfferType.isUpdateTime(OfferType.LIFE)
        val isAirUpdateTime = OfferType.isUpdateTime(OfferType.AIR)
        val isBaseUpdateTime = OfferType.isUpdateTime(OfferType.BASE)
        applicationContext?.let { context ->
            thread {
                weatherRepository.loadArea().let { areaList ->
                    val anySet = mutableSetOf<Any>()
                    for (area in areaList) {
                        area.run {
                            if (checkUpdateTime(init, isCurrentUpdateTime))
                                anySet.add(GridData(gridX, gridY))

                            if (checkUpdateTime(init, isDailyUpdateTime))
                                anySet.add(GridData(gridX, gridY))

                            if (checkUpdateTime(init, isWeeklyUpdateTime))
                                anySet.add(AreaCode(prvnCode, cityCode))

                            if (checkUpdateTime(init, isLifeUpdateTime))
                                anySet.add(areaCode)

                            if (checkUpdateTime(init, isAirUpdateTime)) {
                                val sidoName = LocationUtils.splitAddressLine(address)
                                if (sidoName.isNotEmpty())
                                    anySet.add(sidoName[0])
                            }
                        }
                    }
                    anySet.forEach {
                        when (it) {
                            is GridData -> {
                                ApiRequestRepository.requestWeather(
                                    context, ApiConstants.API_FORECAST_TIME_DATA, it
                                )
                                ApiRequestRepository.requestWeather(
                                    context, ApiConstants.API_FORECAST_SPACE_DATA, it
                                )
                            }
                            is AreaCode -> {
                                ApiRequestRepository.requestMiddle(
                                    context, ApiConstants.API_MIDDLE_TEMPERATURE, it
                                )
                                ApiRequestRepository.requestMiddle(
                                    context, ApiConstants.API_MIDDLE_LAND_WEATHER, it
                                )
                            }
                            is String -> {
                                if (it.length == 10)
                                    ApiRequestRepository.requestLife(context, it)
                                else
                                    ApiRequestRepository.requestAirMeasure(context, it)
                            }
                        }
                    }
                }
                if (checkUpdateTime(init, isBaseUpdateTime))
                    ApiRequestRepository.requestNews(context)
            }
        }
    }

   private fun startF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DLog.d(TAG, "startForeground")
            val notificator = Notificator.getInstance()
            val notification = notificator.getNotification(
                applicationContext,
                Notificator.ChannelType.TYPE_POLARIS
            )
            startForeground(notificator.getNotificationId(), notification)
        }
    }

   private fun stopF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DLog.d(TAG, "stopForeground")
            stopForeground(true)
        }
    }
}
