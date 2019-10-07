package com.kobbi.weather.info.data.network.domain.weather

import com.google.gson.annotations.SerializedName


data class WeatherItem(
    @SerializedName("baseDate")
    val baseDate: Int,
    @SerializedName("baseTime")
    val baseTime: Int,
    @SerializedName("category")
    val category: String,
    @SerializedName("fcstDate")
    val fcstDate: String,
    @SerializedName("fcstTime")
    val fcstTime: String,
    @SerializedName("fcstValue")
    val fcstValue: String,
    @SerializedName("nx")
    val nx: Int,
    @SerializedName("ny")
    val ny: Int
)