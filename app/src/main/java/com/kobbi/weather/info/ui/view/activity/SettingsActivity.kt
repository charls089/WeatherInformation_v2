package com.kobbi.weather.info.ui.view.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kobbi.weather.info.R
import com.kobbi.weather.info.databinding.ActivitySettingsBinding
import com.kobbi.weather.info.presenter.viewmodel.SettingViewModel
import com.kobbi.weather.info.util.GoogleClient
import com.kobbi.weather.info.util.SharedPrefHelper

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        init()
        DataBindingUtil.setContentView<ActivitySettingsBinding>(this, R.layout.activity_settings)
            .run {
                settingVm =
                    ViewModelProviders.of(this@SettingsActivity)[SettingViewModel::class.java].apply {
                        useLocation.observe(this@SettingsActivity, Observer {
                            SharedPrefHelper.setBool(
                                applicationContext, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION, it
                            )
                            if (it) {
                                checkPermission()
                            }
                            onAgreeChangedResults(it)
                        })
                    }
                lifecycleOwner = this@SettingsActivity
            }
    }

    override fun doSomething() {
        applicationContext?.let { context ->
            if (SharedPrefHelper.getBool(context, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION)) {
                GoogleClient.checkLocationEnabled(this)
            }
        }
    }
}