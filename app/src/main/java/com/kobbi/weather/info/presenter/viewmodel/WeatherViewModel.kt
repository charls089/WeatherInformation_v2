package com.kobbi.weather.info.presenter.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kobbi.weather.info.data.database.entity.*
import com.kobbi.weather.info.presenter.model.data.MinMaxTpr
import com.kobbi.weather.info.presenter.repository.WeatherRepository

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    val currentWeather get() = _current
    val yesterdayCurrent get() = _yesterdayCurrent
    val dailyWeather get() = _daily
    val weeklyWeather get() = _weekly
    val weekFromDaily get() = _weekFromDaily
    val lifeIndex get() = _lifeIndex
    val minMaxTpr get() = _minMaxTpr
    val airMeasure get() = _air
    val special get() = _special
    val updateTime: MutableLiveData<Long> = MutableLiveData()

    private val _weatherRepository = WeatherRepository.getInstance(application)

    private var _current: LiveData<List<CurrentWeather>>? = null
    private var _yesterdayCurrent: LiveData<List<CurrentWeather>>? = null
    private var _daily: LiveData<List<DailyWeather>>? = null
    private var _weekFromDaily: LiveData<List<DailyWeather>>? = null
    private var _weekly: LiveData<List<WeeklyWeather>>? = null
    private var _lifeIndex: LiveData<List<LifeIndex>>? = null
    private var _minMaxTpr: LiveData<List<MinMaxTpr>>? = null
    private var _air: LiveData<List<AirMeasure>>? = null
    private var _special: LiveData<SpecialNews>? = null

    fun refreshData() {
        updateTime.postValue(System.currentTimeMillis())
        _current = _weatherRepository.loadCurrentWeatherLive()
        _yesterdayCurrent = _weatherRepository.loadYesterdayWeatherLive()
        _daily = _weatherRepository.loadDailyWeatherLive()
        _weekFromDaily = _weatherRepository.loadWeekWeatherLive()
        _weekly = _weatherRepository.loadWeeklyWeatherLive()
        _lifeIndex = _weatherRepository.loadLifeIndexLive()
        _minMaxTpr = _weatherRepository.loadMinMaxTprLive()
        _air = _weatherRepository.loadAirMeasureLive()
        _special = _weatherRepository.loadSpecialNewsLive()
    }
}