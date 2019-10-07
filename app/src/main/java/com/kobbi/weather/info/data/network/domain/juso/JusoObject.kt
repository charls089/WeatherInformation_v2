package com.kobbi.weather.info.data.network.domain.juso

import com.google.gson.annotations.SerializedName

data class JusoObject(
    @SerializedName("cmmMsgHeader")
    val header: JusoHeader,
    @SerializedName("eupMyunDongList", alternate = ["siGunGuList", "borodCity"])
    val body: List<JusoItem>
)