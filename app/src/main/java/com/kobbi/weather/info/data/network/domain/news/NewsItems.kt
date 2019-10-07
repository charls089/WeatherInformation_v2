package com.kobbi.weather.info.data.network.domain.news

import com.google.gson.annotations.SerializedName

class NewsItems(
    @SerializedName("item")
    val item: NewsItem
)