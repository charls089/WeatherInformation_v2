package com.kobbi.weather.info.util

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

object GoogleClient {
    const val REQUEST_CODE_LOCATION_STATUS = 10001

    private const val UPDATE_INTERVAL_MS = 1000L  // 1초
    private const val FASTEST_UPDATE_INTERVAL_MS = 500L // 0.5초

    private var GOOGLE_API_INSTANCE: GoogleApiClient? = null

    fun checkLocationEnabled(activity: Activity): Boolean {
        activity.applicationContext?.let { context ->
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                return true
            }
            reconnect(activity)
        }
        return false
    }

    fun disconnect(activity: Activity) {
        getInstance(activity).run {
            if (isConnected) {
                this.disconnect()
            }
        }
    }

    private fun reconnect(activity: Activity) {
        getInstance(activity).run {
            if (!isConnected)
                this.reconnect()
        }
    }

    private fun getInstance(activity: Activity): GoogleApiClient {
        if (GOOGLE_API_INSTANCE == null)
            GOOGLE_API_INSTANCE = GoogleApiClient.Builder(activity.application).run {
                addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                    override fun onConnected(p0: Bundle?) {
                        turnGpsOnByGooglePlayService(activity)
                    }

                    override fun onConnectionSuspended(p0: Int) {
                        //Nothing
                    }
                })
                addOnConnectionFailedListener {
                    //Nothing
                        connectionResult ->
                    connectionResult.resolution
                }
                addApi(LocationServices.API)
                build()
            }
        return GOOGLE_API_INSTANCE!!
    }

    private fun turnGpsOnByGooglePlayService(activity: Activity) {
        val builder = LocationSettingsRequest.Builder().apply {
            addLocationRequest(LocationRequest().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = UPDATE_INTERVAL_MS
                fastestInterval = FASTEST_UPDATE_INTERVAL_MS
            })
            setAlwaysShow(true)
        }
        LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build()).run {
            addOnCompleteListener {
                try {
                    it.getResult(ApiException::class.java)
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            try {
                                (exception as ResolvableApiException).startResolutionForResult(
                                    activity,
                                    REQUEST_CODE_LOCATION_STATUS
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        else -> DLog.d(message = "Undefined status code : ${e.statusCode}")
                    }
                }
            }
        }
    }
}