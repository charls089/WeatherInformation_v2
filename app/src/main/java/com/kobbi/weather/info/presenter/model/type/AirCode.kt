package com.kobbi.weather.info.presenter.model.type

import android.text.TextUtils
import com.kobbi.weather.info.util.Constants
import java.lang.NumberFormatException

enum class AirCode(
    val codeName: String,
    val codeNo: String,
    val range: Array<Pair<Int, String>>
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

        fun getAirLevel(code: String, ss: String): String {
            try {
                var result = ""
                val value = ss.toInt()
                findAirCode(code)
                    ?.range?.let { range ->
                    for (i in 0 until range.size - 1) {
                        if (value >= range[i].first && value < range[i + 1].first) {
                            result = range[i].second
                            break
                        } else {
                            result = range[i + 1].second
                        }
                    }
                }
                return result
            } catch (e: NumberFormatException) {
                return "점검중"
            }
        }
    }
}