package com.kobbi.weather.info.ui.view.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kobbi.weather.info.R
import com.kobbi.weather.info.util.DLog
import com.kobbi.weather.info.util.GoogleClient
import com.kobbi.weather.info.util.SharedPrefHelper

abstract class BaseActivity : AppCompatActivity() {
    companion object {
        private val NEED_PERMISSIONS =
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        private const val REQUEST_CODE_PERMISSIONS = 100
        private const val REQUEST_PERMISSION_SETTING = 101
    }

    abstract fun doSomething()

    fun init() {
        val isAgree =
            SharedPrefHelper.getBool(
                applicationContext, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION, true
            )
        DLog.d(message = "BaseActivity.init() --> isAgree : $isAgree")
        if (isAgree)
            checkPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_PERMISSIONS -> {
                var isAgree = false
                for (i in grantResults.indices) {
                    val permission = permissions[i]
                    if (permission == android.Manifest.permission.ACCESS_FINE_LOCATION) {
                        applicationContext?.let { context ->
                            SharedPrefHelper.getBool(
                                context, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION
                            ).let {
                                if (it) {
                                    goToSettings(permission)
                                    return
                                }
                            }

                            isAgree =
                                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                    true
                                } else {
                                    Toast.makeText(
                                        context, R.string.info_deny_permission, Toast.LENGTH_SHORT
                                    ).show()
                                    false
                                }
                            SharedPrefHelper.setBool(
                                context, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION, isAgree
                            )
                        }
                    }
                }
                if (isAgree)
                    GoogleClient.checkLocationEnabled(this)
                doSomething()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GoogleClient.REQUEST_CODE_LOCATION_STATUS -> {
                var textResId = 0
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        textResId = R.string.info_use_location_service
                    }
                    Activity.RESULT_CANCELED -> {
                        SharedPrefHelper.setBool(
                            applicationContext, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION, false
                        )
                        textResId = R.string.info_stop_use_location_service
                    }
                }
                Toast.makeText(applicationContext, textResId, Toast.LENGTH_SHORT).show()
                doSomething()
            }
            REQUEST_PERMISSION_SETTING -> {
                checkPermission()
            }
        }
    }

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val needPermission: Array<String> = ArrayList<String>().run {
                NEED_PERMISSIONS.forEach {
                    val result = checkSelfPermission(it)
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        this.add(it)
                    }
                }
                this.toArray(arrayOf())
            }
            needPermission.run {
                if (isNotEmpty()) {
                    requestPermissions(
                        this,
                        REQUEST_CODE_PERMISSIONS
                    )
                } else {
                    doSomething()
                }
            }
        } else {
            doSomething()
        }
    }

    private fun goToSettings(permission: String) {
        AlertDialog.Builder(this).run {
            setTitle(R.string.title_dialog_location_permission)
            setMessage(R.string.info_location_permission_request_message)
            setCancelable(false)
            setPositiveButton(R.string.symbol_yes) { dialog, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val showRationale =
                        shouldShowRequestPermissionRationale(permission)
                    if (!showRationale)
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).run {
                            val uri = Uri.fromParts("package", packageName, null)
                            data = uri
                            startActivityForResult(this, REQUEST_PERMISSION_SETTING)
                        }
                }
                dialog.dismiss()
            }
            setNegativeButton(R.string.symbol_no) { dialog, _ ->
                SharedPrefHelper.setBool(context, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION, false)
                doSomething()
                dialog.dismiss()
            }
            create().show()
        }
    }
}