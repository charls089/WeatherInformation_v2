package com.kobbi.weather.info.data.network.domain.middle

import com.google.gson.annotations.SerializedName

data class MiddleItems(
    @SerializedName("item")
    val item: Map<String, String>?,
    @SerializedName("numOfRows")
    val numOfRows: Int,
    @SerializedName("pageNo")
    val pageNo: Int,
    @SerializedName("totalCount")
    val totalCount: Int
)