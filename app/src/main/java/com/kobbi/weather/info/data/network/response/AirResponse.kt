package com.kobbi.weather.info.data.network.response

import com.google.gson.annotations.SerializedName

data class AirResponse(
    @SerializedName("list")
    val list: List<Map<String, String>>
)