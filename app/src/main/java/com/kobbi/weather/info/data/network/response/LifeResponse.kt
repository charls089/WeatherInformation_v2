package com.kobbi.weather.info.data.network.response

import com.google.gson.annotations.SerializedName
import com.kobbi.weather.info.data.network.domain.life.LifeObject

data class LifeResponse(
    @SerializedName("Response")
    val response: LifeObject
)