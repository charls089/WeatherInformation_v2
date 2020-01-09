package com.kobbi.weather.info.presenter.model.type

import com.kobbi.weather.info.util.Utils
import java.util.*

enum class SearchTime(
    val format: String,
    val field: Int,
    val amount: Int,
    val suffix: String
) {
    DEFAULT(Utils.VALUE_DATE_FORMAT, Calendar.DATE, 0, "0000"),
    CURRENT(Utils.VALUE_DATETIME_FORMAT, Calendar.DATE, 0, "00"),
    YESTERDAY(Utils.VALUE_DATETIME_FORMAT, Calendar.DATE, -1, "00"),
    DAILY_START(Utils.VALUE_DATETIME_FORMAT, Calendar.HOUR, 1, "00"),
    DAILY_END(Utils.VALUE_DATETIME_FORMAT, Calendar.DATE, 1, "00"),
    WEEK_START(Utils.VALUE_DATE_FORMAT, Calendar.DATE, 1, "0000"),
    WEEK_CHECK(Utils.VALUE_DATE_FORMAT, Calendar.DATE, 3, "0000"),
    WEEKLY_CHECK(Utils.VALUE_DATE_FORMAT, Calendar.DATE, 3, ""),
    WEEKLY_END(Utils.VALUE_DATE_FORMAT, Calendar.DATE, 6, ""),
    LIFE(Utils.VALUE_DATETIME_FORMAT, Calendar.DATE, 0, ""),
    AIR(Utils.VALUE_DATETIME_FORMAT, Calendar.DATE, 0, "00"),
    SPECIAL(Utils.VALUE_DATETIME_FORMAT, Calendar.HOUR, 0, "00");

    companion object {
        @JvmStatic
        fun getDate(type: SearchTime) = GregorianCalendar().run {
            add(type.field, type.amount)
            when (type) {
                CURRENT, YESTERDAY -> add(Calendar.HOUR, 1)
                WEEK_CHECK, WEEKLY_CHECK -> {
                    if (get(Calendar.HOUR_OF_DAY) in 0..6)
                        add(type.field, -1)
                }
                else -> {
                    //Nothing
                }
            }
            (Utils.getCurrentTime(type.format, this.timeInMillis) + type.suffix).toLong()
        }

        @JvmStatic
        fun getTerm(startType: SearchTime, endType: SearchTime): Pair<Long, Long> {
            val startDate = getDate(startType)
            val endDate = getDate(endType)
            return Pair(startDate, endDate)
        }

        @JvmStatic
        fun getTime(type: SearchTime): Pair<Long, Int> {
            val date = getDate(type)
            val time = date.toString().takeLast(2).toInt() / 3 * 3
            return Pair(date.toString().dropLast(2).toLong(), time)
        }
    }
}