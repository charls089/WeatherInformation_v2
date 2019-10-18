package com.kobbi.weather.info.presenter.listener

import android.location.Location

interface LocationListener {
    fun onComplete(responseCode: Int, location: Location?)
}