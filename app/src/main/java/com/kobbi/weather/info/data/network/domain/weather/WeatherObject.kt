package com.kobbi.weather.info.data.network.domain.weather

import com.google.gson.annotations.SerializedName

data class WeatherObject(
    @SerializedName("header")
    val header: WeatherHeader,
    @SerializedName("body")
    val body: WeatherBody
)