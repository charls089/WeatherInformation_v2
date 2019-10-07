package com.kobbi.weather.info.data.network.domain.news

import com.google.gson.annotations.SerializedName

data class NewsHeader(
    @SerializedName("resultCode")
    val resultCode:String,
    @SerializedName("resultMsg")
    val resultMsg:String
)