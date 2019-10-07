package com.kobbi.weather.info.data.network.domain.middle

import com.google.gson.annotations.SerializedName

data class MiddleObject(
    @SerializedName("header")
    val header: MiddleHeader,
    @SerializedName("body")
    val body: MiddleBody
)