package com.kobbi.weather.info.presenter.viewmodel

import android.content.Context
import com.kobbi.weather.info.data.database.entity.Area
import com.kobbi.weather.info.presenter.listener.CompleteListener
import com.kobbi.weather.info.presenter.model.data.WeatherInfo
import com.kobbi.weather.info.presenter.model.type.OfferType
import com.kobbi.weather.info.presenter.model.type.ReturnCode
import com.kobbi.weather.info.presenter.repository.ApiRequestRepository
import com.kobbi.weather.info.presenter.repository.WeatherRepository
import com.kobbi.weather.info.util.DLog
import kotlin.concurrent.thread

class WidgetViewModel(private val context: Context) {

    private val weatherRepository = WeatherRepository.getInstance(context.applicationContext)

    private val locatedArea: Area? get() = weatherRepository.loadLocatedArea()
    private val weatherInfo: WeatherInfo? get() = weatherRepository.loadWeatherInfo(locatedArea)

    fun getWeatherInfo(listener: CompleteListener) {
        thread {
            locatedArea?.run {
                weatherInfo?.run {
                    listener.onComplete(ReturnCode.NO_ERROR, this)
                } ?: ApiRequestRepository.initBaseAreaData(context.applicationContext, this, object : CompleteListener {
                    override fun onComplete(code: ReturnCode, data: Any) {
                        DLog.d(
                            tag = "WidgetViewModel",
                            message = "WidgetViewModel.onComplete() --> code : $code, data : $data"
                        )
                        when (code) {
                            ReturnCode.NO_ERROR -> {
                                if (data is OfferType)
                                    when (data) {
                                        OfferType.CURRENT, OfferType.MIN_MAX -> {
                                            thread {
                                                weatherInfo?.run {
                                                    listener.onComplete(ReturnCode.NO_ERROR, this)
                                                }
                                            }
                                        }
                                        else -> {
                                            //Nothing
                                        }
                                    }
                            }
                            else -> {
                                listener.onComplete(code, data)
                            }
                        }
                    }
                })
            } ?: listener.onComplete(ReturnCode.AREA_IS_NOT_FOUND, "We can't find your area right now.")
        }
    }
}