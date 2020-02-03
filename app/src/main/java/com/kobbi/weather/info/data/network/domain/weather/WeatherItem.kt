package com.kobbi.weather.info.data.network.domain.weather

import com.google.gson.annotations.SerializedName

data class WeatherItem (
    @SerializedName("item")
    val item: List<Map<String, String>>
)