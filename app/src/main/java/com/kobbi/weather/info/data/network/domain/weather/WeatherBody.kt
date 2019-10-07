package com.kobbi.weather.info.data.network.domain.weather

import com.google.gson.annotations.SerializedName


data class WeatherBody(
    @SerializedName("items")
    val items: WeatherItems,
    @SerializedName("numOfRows")
    val numOfRows: Int,
    @SerializedName("pageNo")
    val pageNo: Int,
    @SerializedName("totalCount")
    val totalCount: Int
)