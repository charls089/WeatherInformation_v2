package com.kobbi.weather.info.presenter.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.kobbi.weather.info.presenter.repository.WeatherRepository
import com.kobbi.weather.info.presenter.service.ServiceManager
import com.kobbi.weather.info.util.SharedPrefHelper
import kotlin.concurrent.thread

class SettingViewModel(application: Application) : AndroidViewModel(application) {
    val useLocation: MutableLiveData<Boolean> = MutableLiveData()

    private val _weatherRepository = WeatherRepository.getInstance(application)

    init {
        val isAgree =
            SharedPrefHelper.getBool(application, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION)
        useLocation.postValue(isAgree)
    }

    fun onAgreeChangedResults(isAgree: Boolean) {
        if (isAgree) {
            ServiceManager.getWeatherInfo()
        } else {
            thread {
                _weatherRepository.run {
                    loadLocatedArea()?.address?.let { lastAddress ->
                        updateAreaCode(lastAddress, 1)
                    }
                }
            }
        }
    }
}