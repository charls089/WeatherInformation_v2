package com.kobbi.weather.info.presenter.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
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
import kotlin.concurrent.thread

class WeatherService : Service() {
    companion object {
        private const val TAG = "WeatherService"
    }

    private val mListener = object : CompleteListener {
        override fun onComplete(code: ReturnCode, data: Any) {
            when (code) {
                ReturnCode.SOCKET_TIMEOUT -> {
                    val message = getString(R.string.info_network_timeout)
                    val title = getString(R.string.title_notify_network_timeout)
                    Notificator.getInstance().showNotification(
                        applicationContext, Notificator.ChannelType.DEFAULT, title, message
                    )
                    if (data is OfferType)
                        requestWeather(true, data)
                }
                else -> {
                    //Nothing
                }
            }
        }
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
            requestAllWeather(init)
        }
    }

    fun notifyMyLocation() {
        if (SharedPrefHelper.getBool(
                applicationContext,
                SharedPrefHelper.KEY_AGREE_TO_USE_NOTIFICATION
            )
        )
            thread {
                val locatedArea = weatherRepository.loadLocatedArea()
                weatherRepository.getWeatherInfo(locatedArea)?.run {
                    Notificator.getInstance().showNotification(
                        applicationContext,
                        Notificator.ChannelType.WEATHER,
                        String.format(
                            getString(R.string.holder_weather_notify), tpr, wct, tmn, tmx
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
        }
    }

    private fun requestAllWeather(init: Boolean) {
        OfferType.values().forEach { type ->
            requestWeather(init, type)
        }
    }

    private fun requestWeather(init: Boolean, type: OfferType) {
        if (init || OfferType.isNeedToUpdate(type) || type == OfferType.YESTERDAY) {
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
                                    context, ApiConstants.API_MIDDLE_LAND_WEATHER, areaCode, mListener
                                )
                                ApiRequestRepository.requestMiddle(
                                    context, ApiConstants.API_MIDDLE_TEMPERATURE, areaCode, mListener
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
                            weatherRepository.loadAllCityName().forEach { cityName ->
                                ApiRequestRepository.requestAirMeasure(
                                    context, cityName, mListener
                                )
                            }
                        }
                        OfferType.BASE -> {
                            ApiRequestRepository.requestNews(context, mListener)
                        }
                        OfferType.YESTERDAY -> {
                            weatherRepository.loadAllGridData().forEach { gridData ->
                                if (
                                    weatherRepository.findYesterdayWeather(gridData.x, gridData.y) == null
                                ) {
                                    ApiRequestRepository.requestWeather(
                                        context, type, gridData, mListener
                                    )
                                }
                            }
                        }
                        OfferType.MIN_MAX -> {
                            //Nothing
                        }
                    }
                }
            }
        }
    }
}
