package com.kobbi.weather.info.presenter.listener

import android.location.Location

interface OnLocationListener {
    fun onComplete(responseCode: Int, location: Location?)
}