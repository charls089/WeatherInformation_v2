package com.kobbi.weather.info.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Utils private constructor() {
    companion object {
        const val VALUE_TIME_FORMAT = "HH:mm:ss"
        const val VALUE_DATE_FORMAT = "yyyyMMdd"
        const val VALUE_DATETIME_FORMAT = "yyyyMMddHH"

        @JvmStatic
        fun getCurrentTime(
            format: String = VALUE_DATE_FORMAT, time: Long = System.currentTimeMillis()
        ): String =
            SimpleDateFormat(format, Locale.getDefault()).format(time)


        @JvmStatic
        fun convertDateTime(
            time: Long = System.currentTimeMillis(),
            type: Int = 0
        ): String {
            GregorianCalendar().run {
                timeInMillis = time

                val month = get(Calendar.MONTH)
                val date = get(Calendar.DATE)
                val dayOfWeek = when (get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> "월"
                    Calendar.TUESDAY -> "화"
                    Calendar.WEDNESDAY -> "수"
                    Calendar.THURSDAY -> "목"
                    Calendar.FRIDAY -> "금"
                    Calendar.SATURDAY -> "토"
                    Calendar.SUNDAY -> "일"
                    else -> "ERROR"
                }
                val amPm = when (get(Calendar.AM_PM)) {
                    Calendar.AM -> "오전"
                    else -> "오후"
                }

                val hour = get(Calendar.HOUR)
                val minute = get(Calendar.MINUTE)

                return "${month + 1}월 ${date}일 ($dayOfWeek)${if (type == 1) "" else " $amPm ${if (hour == 0) 12 else hour}시 ${minute}분"}"
            }
        }

        @JvmStatic
        fun convertStringToDate(format: String = VALUE_DATE_FORMAT, date: String): Date? {
            return try {
                SimpleDateFormat(format, Locale.getDefault()).parse(date)
            } catch (e: ParseException) {
                e.printStackTrace()
                null
            }
        }
    }
}