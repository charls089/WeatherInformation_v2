package com.kobbi.weather.info.presenter.model.type

import com.kobbi.weather.info.util.DLog
import com.kobbi.weather.info.util.Utils
import java.util.*

enum class OfferType(
    val baseTimeList: Array<String> = arrayOf(),
    val offerTime: Int = 0
) {
    BASE(Array(24) { i -> "${if (i < 10) "0" else ""}${i}00" }, 10),
    CURRENT(Array(24) { i -> "${if (i < 10) "0" else ""}${i}30" }, 15),
    DAILY(arrayOf("0200", "0500", "0800", "1100", "1400", "1700", "2000", "2300"), 10),
    WEEKLY(arrayOf("0600", "1800"), 50),
    LIFE_DAY(arrayOf("0600"), 10),
    LIFE_TIME(Array(8) { i -> "${if (i < 4) "0" else ""}${i * 3}00" }, 10),
    AIR(Array(24) { i -> "${if (i < 10) "0" else ""}${i}00" }, 15),
    MIN_MAX,
    YESTERDAY;

    companion object {
        private const val TAG = "OfferType"
        private val DEFAULT_OFFER_TIME_RANGE = 0..10

        fun isNeedToUpdate(type: OfferType): Boolean {
            if (type == YESTERDAY || type == MIN_MAX)
                return true
            val currentTime = Utils.getCurrentTime("HHmm", System.currentTimeMillis()).toInt()
            return type.baseTimeList.any {
                val offerTime = it.toInt() + type.offerTime
                currentTime - offerTime in DEFAULT_OFFER_TIME_RANGE
            }
        }

        fun getBaseDateTime(type: OfferType): Pair<String, String> {
            var baseTime = "0000"
            val calendar = GregorianCalendar().apply {
                when (type) {
                    YESTERDAY -> {
                        val min = Utils.getCurrentTime("mm", this.timeInMillis)
                        this.add(Calendar.DATE, -1)
                        if (min.toInt() > 30) {
                            this.add(Calendar.HOUR, 1)
                        }
                        baseTime = Utils.getCurrentTime("HH", this.timeInMillis) + 30
                    }
                    MIN_MAX -> {
                        baseTime = "0200"
                    }
                    else -> {
                        val time = Utils.getCurrentTime("HHmm", this.timeInMillis)
                        val baseTimeList = type.baseTimeList
                        val listIndices = baseTimeList.indices
                        val offerTime = type.offerTime
                        for (i in listIndices) {
                            if (time.toInt() < (baseTimeList[i].toInt() + offerTime)) {
                                baseTime = when (type) {
                                    LIFE_TIME -> {
                                        if (i <= 1) {
                                            this.add(Calendar.DATE, -1)
                                            baseTimeList[baseTimeList.size - (2 - i)]
                                        } else {
                                            baseTimeList[i - 2]
                                        }
                                    }
                                    else -> {
                                        if (i == 0) {
                                            this.add(Calendar.DATE, -1)
                                            baseTimeList[baseTimeList.size - 1]
                                        } else {
                                            baseTimeList[i - 1]
                                        }
                                    }
                                }
                                break
                            }
                            if (i == listIndices.last) {
                                val index = baseTimeList.size - if (type == LIFE_TIME) 2 else 1
                                baseTime = baseTimeList[index]
                                break
                            }
                        }
                    }
                }
            }
            val baseDate = Utils.getCurrentTime(time = calendar.timeInMillis)
            DLog.d(
                tag = TAG,
                message = "getBaseDateTime() -->type : $type, baseDate : $baseDate / baseTime : $baseTime"
            )
            return Pair(baseDate, baseTime)
        }
    }
}