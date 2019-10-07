package com.kobbi.weather.info.data.network.domain.life

import com.google.gson.annotations.SerializedName

data class LifeObject(
    @SerializedName("header")
    val header:LifeHeader,
    @SerializedName("body")
    val body:LifeBody
)