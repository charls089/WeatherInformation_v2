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
    val lifeIndexDay get() = _lifeDay
    val lifeIndexHour get() = _lifeHour
    val minMaxTpr get() = _minMaxTpr
    val airMeasure get() = _air
    val special get() = _special
    val updateTime: MutableLiveData<Long> = MutableLiveData()

    private val weatherRepository = WeatherRepository.getInstance(application)

    private var _current: LiveData<List<CurrentWeather>>? = null
    private var _yesterdayCurrent: LiveData<List<CurrentWeather>>? = null
    private var _daily: LiveData<List<DailyWeather>>? = null
    private var _weekFromDaily: LiveData<List<DailyWeather>>? = null
    private var _weekly: LiveData<List<WeeklyWeather>>? = null
    private var _lifeDay: LiveData<List<LifeIndexDay>>? = null
    private var _lifeHour: LiveData<List<LifeIndexHour>>? = null
    private var _minMaxTpr: LiveData<List<MinMaxTpr>>? = null
    private var _air: LiveData<List<AirMeasure>>? = null
    private var _special: LiveData<SpecialNews>? = null

    fun refreshData() {
        updateTime.postValue(System.currentTimeMillis())
        _current = weatherRepository.loadCurrentWeatherLive()
        _yesterdayCurrent = weatherRepository.loadYesterdayWeatherLive()
        _daily = weatherRepository.loadDailyWeatherLive()
        _weekFromDaily = weatherRepository.loadWeekWeatherLive()
        _weekly = weatherRepository.loadWeeklyWeatherLive()
        _lifeDay = weatherRepository.loadLifeIndexDayLive()
        _lifeHour = weatherRepository.loadLifeIndexHourLive()
        _minMaxTpr = weatherRepository.loadMinMaxTprLive()
        _air = weatherRepository.loadAirMeasureLive()
        _special = weatherRepository.loadSpecialNewsLive()
    }
}