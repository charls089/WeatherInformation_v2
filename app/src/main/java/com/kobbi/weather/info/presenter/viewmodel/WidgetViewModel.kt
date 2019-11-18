package com.kobbi.weather.info.presenter.viewmodel

import android.content.Context
import com.kobbi.weather.info.data.database.entity.Area
import com.kobbi.weather.info.presenter.listener.CompleteListener
import com.kobbi.weather.info.presenter.model.data.WeatherInfo
import com.kobbi.weather.info.presenter.model.type.ReturnCode
import com.kobbi.weather.info.presenter.repository.ApiRequestRepository
import com.kobbi.weather.info.presenter.repository.WeatherRepository

class WidgetViewModel(private val context: Context) {

    private val weatherRepository = WeatherRepository.getInstance(context.applicationContext)

    private val locatedArea: Area? get() = weatherRepository.loadLocatedArea()
    private val weatherInfo: WeatherInfo? get() = weatherRepository.loadWeatherInfo(locatedArea)

    fun getWeatherInfo(listener: CompleteListener) {
        locatedArea?.run {
            weatherInfo?.run {
                listener.onComplete(ReturnCode.NO_ERROR, weatherInfo!!)
            } ?: ApiRequestRepository.initBaseAreaData(context, this, listener)
        } ?: listener.onComplete(ReturnCode.DATA_IS_NULL, "We can't find your area right now.")
    }
}