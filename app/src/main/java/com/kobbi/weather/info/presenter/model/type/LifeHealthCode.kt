package com.kobbi.weather.info.presenter.model.type

import com.kobbi.weather.info.util.Constants

enum class LifeHealthCode(
    val codeName: String,
    val codeNo: String,
    val type: String = Constants.TYPE_3HOUR,
    val range: Array<Pair<IntRange, String>> = Constants.VALUE_RANGE_HEALTH_LEVEL
) {
    FSN("식중독지수", "A01_2", Constants.TYPE_DAY, Constants.VALUE_RANGE_FSN),
    ULTRA_V("자외선지수", "A07_1", Constants.TYPE_DAY, Constants.VALUE_RANGE_ULTRA_V),
    SENSORY_TEM("체감온도", "A03", range = Constants.VALUE_RANGE_WC_TEM),
    HEAT("열지수", "A05", range = Constants.VALUE_RANGE_HEAT),
    DSPLS("불쾌지수", "A06", range = Constants.VALUE_RANGE_DSP_LS),
    WINTER("동파가능지수", "A08", range = Constants.VALUE_RANGE_WINTER),
    AIR_POLLUTION("대기확산지수", "A09", range = Constants.VALUE_RANGE_AIR),
    ASTHMA("천식·폐질환가능지수", "D01", Constants.TYPE_DAY),
    STROKE("뇌졸중가능지수", "D02", Constants.TYPE_DAY),
    OAK_POLLEN_RISK("꽃가루농도위험지수(참나무)", "D06", Constants.TYPE_DAY),
    PINE_POLLEN_RISK("꽃가루농도위험지수(소나무)", "D07", Constants.TYPE_DAY),
    WEEDS_POLLEN_RISK("꽃가루농도위험지수(잡초류)", "D08", Constants.TYPE_DAY),
    COLD("감기가능지수", "D05", Constants.TYPE_DAY);

    companion object {
        fun findLifeCode(codeNo: String): LifeHealthCode? {
            return values().firstOrNull {
                it.codeNo == codeNo
            }
        }

        fun getLifeLevel(code: String, value: Int): String? {
            var result: String? = null
            findLifeCode(code)?.range?.forEach { intRange ->
                if (value in intRange.first) {
                    result = intRange.second
                }
            }
            return result
        }
    }
}