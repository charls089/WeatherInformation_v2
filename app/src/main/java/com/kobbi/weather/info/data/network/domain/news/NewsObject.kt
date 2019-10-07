package com.kobbi.weather.info.data.network.domain.news

import com.google.gson.annotations.SerializedName

data class NewsObject(
    @SerializedName("header")
    val header: NewsHeader,
    @SerializedName("body")
    val body: NewsBody
)