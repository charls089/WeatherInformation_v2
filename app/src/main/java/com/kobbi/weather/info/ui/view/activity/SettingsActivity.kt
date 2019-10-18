package com.kobbi.weather.info.ui.view.activity

import android.content.SharedPreferences
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
    private val mSettingVm by lazy {
        ViewModelProviders.of(this)[SettingViewModel::class.java].apply {
            useLocation.observe(this@SettingsActivity, Observer {
                SharedPrefHelper.setBool(
                    applicationContext, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION, it
                )
            })
        }
    }
    private val mListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION) {
                val isAgree = sharedPreferences.getBoolean(key, false)
                if (isAgree) {
                    checkPermission()
                }
                mSettingVm.onAgreeChangedResults(isAgree)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPrefHelper.registerPrefChangeListener(applicationContext, mListener)
        DataBindingUtil.setContentView<ActivitySettingsBinding>(
            this,
            R.layout.activity_settings
        )
            .run {
                settingVm = mSettingVm
                lifecycleOwner = this@SettingsActivity
            }
    }

    override fun onDestroy() {
        SharedPrefHelper.unregisterPrefChangeListener(applicationContext, mListener)
        super.onDestroy()
    }

    override fun doSomething() {
        applicationContext?.let { context ->
            if (SharedPrefHelper.getBool(context, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION)) {
                GoogleClient.checkLocationEnabled(this)
            }
        }
    }
}