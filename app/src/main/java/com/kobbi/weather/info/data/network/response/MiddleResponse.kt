package com.kobbi.weather.info.data.network.response

import com.google.gson.annotations.SerializedName
import com.kobbi.weather.info.data.network.domain.middle.MiddleObject

data class MiddleResponse(
    @SerializedName("response")
    val response: MiddleObject
)