package com.kobbi.weather.info.data.network.response

import com.google.gson.annotations.SerializedName
import com.kobbi.weather.info.data.network.domain.weather.WeatherObject

data class WeatherResponse(
    @SerializedName("response")
    val response: WeatherObject
)