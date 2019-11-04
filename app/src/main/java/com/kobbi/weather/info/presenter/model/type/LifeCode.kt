package com.kobbi.weather.info.presenter.model.type

import com.kobbi.weather.info.util.Constants

enum class LifeCode(
    val codeName: String,
    val codeNo: String,
    val type: String = Constants.TYPE_3HOUR,
    val range: Array<Pair<IntRange, String>>
) {
    FSN("식중독지수", "A01_2", Constants.TYPE_DAY, Constants.VALUE_RANGE_FSN),
    ULTRA_V("자외선지수", "A07_1", Constants.TYPE_DAY, Constants.VALUE_RANGE_ULTRA_V),
    SENSORY_TEM("체감온도", "A03", range = Constants.VALUE_RANGE_WC_TEM),
    HEAT("열지수", "A05", range = Constants.VALUE_RANGE_HEAT),
    DSPLS("불쾌지수", "A06", range = Constants.VALUE_RANGE_DSP_LS),
    WINTER("동파가능지수", "A08", range = Constants.VALUE_RANGE_WINTER),
    AIR_POLLUTION("대기확산지수", "A09", range = Constants.VALUE_RANGE_AIR),
    SENSORY_HEAT("더위체감지수", "A20", range = Constants.VALUE_RANGE_SENSORY_HEAT);

    companion object {
        fun findLifeCode(codeNo: String): LifeCode? {
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