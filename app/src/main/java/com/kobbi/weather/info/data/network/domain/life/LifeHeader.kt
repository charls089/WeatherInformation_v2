package com.kobbi.weather.info.data.network.domain.life

import com.google.gson.annotations.SerializedName

data class LifeHeader(
    @SerializedName("successYN")
    val successYN:String,
    @SerializedName("returnCode")
    val returnCode:String,
    @SerializedName("errMsg")
    val errMsg:String
)