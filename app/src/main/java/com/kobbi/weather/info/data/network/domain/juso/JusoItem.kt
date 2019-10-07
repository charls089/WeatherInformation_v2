package com.kobbi.weather.info.data.network.domain.juso

import com.google.gson.annotations.SerializedName

data class JusoItem(
    @SerializedName("emdCd",alternate = ["signguCd","brtcCd"])
    val cd:String,
    @SerializedName("brtcNm")
    val brtcNm:String?
)