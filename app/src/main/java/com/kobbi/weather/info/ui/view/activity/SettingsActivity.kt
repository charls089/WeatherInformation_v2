package com.kobbi.weather.info.ui.view.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kobbi.weather.info.R
import com.kobbi.weather.info.databinding.ActivitySettingsBinding
import com.kobbi.weather.info.presenter.viewmodel.SettingViewModel
import com.kobbi.weather.info.util.GoogleClient
import com.kobbi.weather.info.util.SharedPrefHelper

class SettingsActivity : BaseActivity() {
    private val mSettingVm by lazy {
        ViewModelProvider.AndroidViewModelFactory(application).create(SettingViewModel::class.java)
            .apply {
                useLocation.observe(this@SettingsActivity, Observer {
                    SharedPrefHelper.setBool(
                        applicationContext, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION, it
                    )
                })
                useNotify.observe(this@SettingsActivity, Observer {
                    SharedPrefHelper.setBool(
                        applicationContext, SharedPrefHelper.KEY_AGREE_TO_USE_NOTIFICATION, it
                    )
                })
            }
    }
    private val mListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION -> {
                    val isAgree = sharedPreferences.getBoolean(key, false)
                    if (isAgree) {
                        checkPermission()
                    }
                    mSettingVm.onAgreeChangedResults(applicationContext, isAgree)
                    val messageId = if (isAgree) {
                        R.string.info_use_location_service
                    } else {
                        R.string.info_stop_use_location_service
                    }
                    Toast.makeText(applicationContext, messageId, Toast.LENGTH_SHORT).show()
                }
                SharedPrefHelper.KEY_AGREE_TO_USE_NOTIFICATION -> {
                    val isAgree = sharedPreferences.getBoolean(key, false)
                    val messageId = if (isAgree) {
                        R.string.info_use_notification_service
                    } else {
                        R.string.info_stop_notification_service
                    }
                    Toast.makeText(applicationContext, messageId, Toast.LENGTH_SHORT).show()
                }
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