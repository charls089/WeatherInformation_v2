package com.kobbi.weather.info.data.network.response

import com.google.gson.annotations.SerializedName
import com.kobbi.weather.info.data.network.domain.news.NewsObject

data class NewsResponse(
    @SerializedName("response")
    val response: NewsObject
)