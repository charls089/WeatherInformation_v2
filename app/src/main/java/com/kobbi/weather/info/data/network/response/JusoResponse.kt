package com.kobbi.weather.info.data.network.response

import com.google.gson.annotations.SerializedName
import com.kobbi.weather.info.data.network.domain.juso.JusoObject

data class JusoResponse (
    @SerializedName(
        "EupMyunDongListResponse",
        alternate = ["SiGunGuListResponse", "BorodCityResponse"]
    )
    val response:JusoObject
)