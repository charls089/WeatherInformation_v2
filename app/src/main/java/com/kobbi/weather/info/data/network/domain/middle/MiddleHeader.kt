package com.kobbi.weather.info.data.network.domain.middle

import com.google.gson.annotations.SerializedName

data class MiddleHeader(
    @SerializedName("resultCode")
    val resultCode:String,
    @SerializedName("resultMsg")
    val resultMsg:String
)