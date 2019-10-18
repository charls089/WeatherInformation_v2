package com.kobbi.weather.info.presenter.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
import com.kobbi.weather.info.presenter.repository.WeatherRepository
import com.kobbi.weather.info.presenter.service.ServiceManager

class AreaViewModel(application: Application) : AndroidViewModel(application) {
    private val _weatherRepository = WeatherRepository.getInstance(application)
    val area = _weatherRepository.loadActiveAreaLive()

    val position: LiveData<Int> get() = _position
    private val _position: MutableLiveData<Int> = MutableLiveData()

    val listener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
            //Nothing
        }

        override fun onPageScrolled(
            position: Int, positionOffset: Float, positionOffsetPixels: Int
        ) {
            //Nothing
        }

        override fun onPageSelected(position: Int) {
            _position.postValue(position)
        }
    }

    init {
        _position.postValue(0)
    }

    fun refreshWeatherInfo(init: Boolean = false) {
        ServiceManager.getWeatherInfo(init)
    }


}