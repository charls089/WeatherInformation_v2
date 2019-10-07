package com.kobbi.weather.info.data.network.domain.news

import com.google.gson.annotations.SerializedName

data class NewsItem(
    @SerializedName("tmFc")
    val tmFc: Long,
    @SerializedName("tmSeq")
    val tmSeq: Int,
    @SerializedName("tmEf")
    val tmEf: Long,
    @SerializedName("t6")
    val t6: String,
    @SerializedName("t7")
    val t7: String,
    @SerializedName("other")
    val other: String
)