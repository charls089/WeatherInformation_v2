package com.kobbi.weather.info.presenter

import android.app.Application
import android.content.Context
import com.kobbi.weather.info.data.database.AreaCodeDatabase
import com.kobbi.weather.info.presenter.service.ServiceManager
import com.kobbi.weather.info.util.DLog
import com.kobbi.weather.info.util.SharedPrefHelper

class WeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startProcess(applicationContext)
    }

    companion object {
        private const val TAG = "WeatherApplication"

        fun startProcess(context: Context) {
            DLog.i(context, TAG, "Start WeatherApplication")
            setUpdateCheckTime(context, 0)
            AreaCodeDatabase.initializeDB(context)
            ServiceManager.restartService(context, true)
        }

        fun setUpdateCheckTime(context: Context, time: Long = System.currentTimeMillis()) {
            SharedPrefHelper.setLong(context, SharedPrefHelper.KEY_LAST_UPDATE_CHECK_TIME, time)
        }

        fun getUpdateCheckTime(context: Context) =
            SharedPrefHelper.getLong(context, SharedPrefHelper.KEY_LAST_UPDATE_CHECK_TIME)
    }
}