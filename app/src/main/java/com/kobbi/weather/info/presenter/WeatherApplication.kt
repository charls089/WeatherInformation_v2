package com.kobbi.weather.info.presenter

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.kobbi.weather.info.presenter.viewmodel.AreaViewModel
import com.kobbi.weather.info.presenter.viewmodel.WeatherViewModel

class WeatherApplication : Application() {

    val areaViewModel: AreaViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(this)
            .create(AreaViewModel::class.java)
    }
    val weatherViewModel: WeatherViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(this)
            .create(WeatherViewModel::class.java)
    }
}