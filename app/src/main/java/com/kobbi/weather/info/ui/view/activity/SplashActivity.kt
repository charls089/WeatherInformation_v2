package com.kobbi.weather.info.ui.view.activity

import android.content.Intent
import android.os.Bundle
import com.kobbi.weather.info.R
import com.kobbi.weather.info.presenter.WeatherApplication
import com.kobbi.weather.info.util.GoogleClient
import com.kobbi.weather.info.util.SharedPrefHelper

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        init()
    }

    override fun doSomething() {
        applicationContext?.let { context ->
            val isAgree =
                    SharedPrefHelper.getBool(context, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION)
            if (!isAgree || GoogleClient.checkLocationEnabled(this)) {
                //TODO check data
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        }
    }
}
