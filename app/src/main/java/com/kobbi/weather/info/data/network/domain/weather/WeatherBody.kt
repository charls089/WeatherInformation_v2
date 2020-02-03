package com.kobbi.weather.info.data.network.domain.weather

import com.google.gson.annotations.SerializedName


data class WeatherBody(
    @SerializedName("dataType")
    val dataType: String,
    @SerializedName("items")
    val items: WeatherItem,
    @SerializedName("numOfRows")
    val numOfRows: Int,
    @SerializedName("pageNo")
    val pageNo: Int,
    @SerializedName("totalCount")
    val totalCount: Int
)