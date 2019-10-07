package com.kobbi.weather.info.data.network.domain.life

import com.google.gson.annotations.SerializedName

data class LifeBody(
    @SerializedName("indexModel")
    val indexModel: Map<String, String>
)