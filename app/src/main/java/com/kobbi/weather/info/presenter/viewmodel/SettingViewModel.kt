package com.kobbi.weather.info.presenter.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.kobbi.weather.info.presenter.repository.WeatherRepository
import com.kobbi.weather.info.presenter.service.ServiceManager
import com.kobbi.weather.info.util.SharedPrefHelper
import kotlin.concurrent.thread

class SettingViewModel(application: Application) : AndroidViewModel(application) {
    val useLocation: MutableLiveData<Boolean> = MutableLiveData()
    val useNotify: MutableLiveData<Boolean> = MutableLiveData()

    private val _weatherRepository = WeatherRepository.getInstance(application)

    init {
        useLocation.postValue(
            SharedPrefHelper.getBool(
                application, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION
            )
        )
        useNotify.postValue(
            SharedPrefHelper.getBool(
                application, SharedPrefHelper.KEY_AGREE_TO_USE_NOTIFICATION
            )
        )
    }

    fun onAgreeChangedResults(context: Context, isAgree: Boolean) {
        if (isAgree) {
            ServiceManager.getWeatherInfo(context)
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