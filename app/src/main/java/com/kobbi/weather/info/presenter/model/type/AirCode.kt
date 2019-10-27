package com.kobbi.weather.info.presenter.model.type

import com.kobbi.weather.info.util.Constants
import java.lang.NumberFormatException

enum class AirCode(
    val codeName: String,
    val codeNo: String,
    val range: Array<Pair<IntRange, String>>
) {
    PM10("미세먼지농도", "pm10", Constants.VALUE_RANGE_PM10_LEVEL),
    PM25("초미세먼지농도", "pm2.5", Constants.VALUE_RANGE_PM25_LEVEL);

    companion object {
        fun findAirCode(code: String): AirCode? {
            return try {
                values().first {
                    it.codeNo == code
                }
            } catch (e: NoSuchElementException) {
                null
            }
        }

        fun getAirLevel(code: String, valueStr: String): String {
            var result = "점검중"
            try {
                val value = valueStr.toInt()
                findAirCode(code)?.range?.let {
                    for (level in it) {
                        if (value in level.first) {
                            result = level.second
                        }
                    }
                }
            } catch (e: NumberFormatException) {
                //
            }
            return result
        }
    }
}