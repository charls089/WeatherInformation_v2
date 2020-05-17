package com.kobbi.weather.info.presenter.location

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import com.kobbi.weather.info.presenter.listener.LocationCompleteListener
import com.kobbi.weather.info.presenter.service.ServiceManager
import com.kobbi.weather.info.util.DLog
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.pow


object LocationManager {
    enum class ResponseCode {
        RESPONSE_NO_ERROR,
        RESPONSE_LOCATION_TIMEOUT,
        RESPONSE_MISSING_PERMISSION;

        companion object {
            fun isComplete(code: ResponseCode): Boolean {
                return values().any {
                    code == it
                }
            }
        }
    }

    private const val LOCATION_OFFER_TIME = 10 * 60 * 1000L
    private const val DEFAULT_WAIT_TIME = 4 * 1000L

    private val mExecutionWaits = HashSet<LocationCompleteListener>()
    private var mIsRunning = false

    @Synchronized
    fun getLocation(context: Context, listener: LocationCompleteListener) {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager

        mExecutionWaits.add(listener)
        if (mIsRunning)
            return

        mIsRunning = true

        getLocationFromCache(locationManager)?.let {
            sendCallbackComplete(ResponseCode.RESPONSE_NO_ERROR, it)
            return
        }

        val timer = Timer()

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                timer.cancel()
                locationManager.removeUpdates(this)
                location?.let {
                    sendCallbackComplete(ResponseCode.RESPONSE_NO_ERROR, location)
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                //Nothing
            }

            override fun onProviderEnabled(provider: String?) {
                //Nothing
            }

            override fun onProviderDisabled(provider: String?) {
                //Nothing
            }
        }
        try {
            locationManager.requestLocationUpdates(
                NETWORK_PROVIDER, 0L, 0F, locationListener, Looper.getMainLooper()
            )
            timer.schedule(DEFAULT_WAIT_TIME) {
                locationManager.removeUpdates(locationListener)
                sendCallbackComplete(ResponseCode.RESPONSE_LOCATION_TIMEOUT, null)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            sendCallbackComplete(ResponseCode.RESPONSE_MISSING_PERMISSION, null)
        }
    }

    @Synchronized
    private fun sendCallbackComplete(responseCode: ResponseCode, location: Location?) {
        for (listener in mExecutionWaits) {
            listener.onComplete(responseCode, location)
        }
        mExecutionWaits.clear()
        mIsRunning = false
    }


    private fun getLocationFromCache(manager: android.location.LocationManager): Location? {
        try {
            fun getLastKnownLocation(provider: String): Location? {
                fun convertNanoToMillis(nanoTime: Long) = nanoTime.div(10.0.pow(6)).toLong()
                manager.getLastKnownLocation(provider)?.let {
                    val currentTime = convertNanoToMillis(SystemClock.elapsedRealtimeNanos())
                    val locationTime = convertNanoToMillis(it.elapsedRealtimeNanos)
                    DLog.d(
                        tag = "LocationManager",
                        message = "$currentTime - $locationTime(${currentTime - locationTime}) <= $LOCATION_OFFER_TIME"
                    )
                    if (currentTime - locationTime <= LOCATION_OFFER_TIME)
                        return it
                }
                return null
            }
            return getLastKnownLocation(GPS_PROVIDER) ?: getLastKnownLocation(NETWORK_PROVIDER)
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}