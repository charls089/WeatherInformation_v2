package com.kobbi.weather.info.data.network.domain.news

import com.google.gson.annotations.SerializedName

data class NewsBody(
    @SerializedName("items")
    val items: NewsItems,
    @SerializedName("numOfRows")
    val numOfRows: Int,
    @SerializedName("pageNo")
    val pageNo: Int,
    @SerializedName("totalCount")
    val totalCount: Int
)