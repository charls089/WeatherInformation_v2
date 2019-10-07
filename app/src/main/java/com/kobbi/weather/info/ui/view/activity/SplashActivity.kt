package com.kobbi.weather.info.ui.view.activity

import android.content.Intent
import android.os.Bundle
import com.kobbi.weather.info.R
import com.kobbi.weather.info.presenter.WeatherApplication
import com.kobbi.weather.info.util.GoogleClient
import com.kobbi.weather.info.util.SharedPrefHelper
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCenter.start(
                application, "b01400e0-9d30-4183-99d6-43b37dc82a42",
                Analytics::class.java, Crashes::class.java
        )
        init()
    }

    override fun doSomething() {
        applicationContext?.let { context ->
            val isAgree =
                    SharedPrefHelper.getBool(context, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION)
            if (!isAgree || GoogleClient.checkLocationEnabled(this)) {
                WeatherApplication.startProcess(applicationContext)
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        }
    }
}
