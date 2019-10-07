package com.kobbi.weather.info.data.network.domain.juso

import com.google.gson.annotations.SerializedName

data class JusoHeader(
    @SerializedName("requestMsgId")
    val requestMsgId:String,
    @SerializedName("responseMsgId")
    val responseMsgId:String,
    @SerializedName("responseTime")
    val responseTime:String,
    @SerializedName("successYN")
    val successYN:String,
    @SerializedName("returnCode")
    val returnCode:String,
    @SerializedName("errMsg")
    val errMsg:String
)