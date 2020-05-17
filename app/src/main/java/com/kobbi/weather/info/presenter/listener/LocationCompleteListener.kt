package com.kobbi.weather.info.presenter.listener

import android.location.Location
import com.kobbi.weather.info.presenter.location.LocationManager

interface LocationCompleteListener {
    fun onComplete(responseCode: LocationManager.ResponseCode, location: Location?)
}