package com.kobbi.weather.info.data.network.domain.middle

import com.google.gson.annotations.SerializedName


data class MiddleBody(
    @SerializedName("items")
    val items: MiddleItems,
    @SerializedName("numOfRows")
    val numOfRows: Int,
    @SerializedName("pageNo")
    val pageNo: Int,
    @SerializedName("totalCount")
    val totalCount: Int
)