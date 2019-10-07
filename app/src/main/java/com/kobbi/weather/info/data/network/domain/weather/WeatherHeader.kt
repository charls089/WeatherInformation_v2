package com.kobbi.weather.info.data.network.domain.weather

import com.google.gson.annotations.SerializedName

data class WeatherHeader(
    @SerializedName("resultCode")
    val resultCode:String,
    @SerializedName("resultMsg")
    val resultMsg:String
)