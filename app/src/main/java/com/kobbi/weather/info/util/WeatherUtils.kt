package com.kobbi.weather.info.util

import com.kobbi.weather.info.R

class WeatherUtils private constructor() {
    enum class Type {
        DAY, NIGHT
    }

    companion object {
        @JvmStatic
        fun getRainGauge(value: String): String {
            if (value.isEmpty())
                return ""
            return when (value.toFloat()) {
                in 0.0f..0.1f -> ""
                in 0.1f..1.0f -> "1mm 미만"
                in 1.0f..5.0f -> "1~4mm"
                in 5.0f..10.0f -> "5~9mm"
                in 10.0f..20.0f -> "10~20mm"
                in 20.0f..40.0f -> "20~40mm"
                in 40.0f..70.0f -> "40~70mm"
                else -> "70mm 이상"
            }
        }

        @JvmStatic
        fun getSkyCloudCover(sky: String?): String {
            return when (sky) {
                "3" -> "구름많음"
                "1" -> "맑음"
                else -> "흐림"
            }
        }

        @JvmStatic
        fun getRainFallType(pty: String?): String {
            return when (pty) {
                "1" -> "비"
                "2" -> "비/눈"
                "3" -> "눈"
                "4" -> "소나기"
                else -> "없음"
            }
        }

        @JvmStatic
        fun getSkyResId(sky: String, dayOrNight: Type): Int {
            return when {
                sky.contains("맑음") -> if (dayOrNight == Type.DAY) R.drawable.icons8_sun_round_96 else R.drawable.icons8_moon_100
                sky.contains("구름많음") -> if (dayOrNight == Type.DAY) R.drawable.icons8_partly_cloudy_day_90 else R.drawable.icons8_partly_cloudy_night_100
                sky.contains("비/눈") || sky.contains("눈/비") -> R.drawable.icons8_sleet_96
                sky.contains("비") || sky.contains("소나기") -> R.drawable.icons8_rain_96
                sky.contains("눈") -> R.drawable.icons8_snow_96
                else -> R.drawable.icons8_cloud_96
            }
        }

        @JvmStatic
        fun getDayOrNight(dateTime: Long?): Type {
            if (dateTime == 0L) {
                return Type.DAY
            }
            val hour = dateTime?.toString()?.substring(8..9)?.toInt()
            return hour?.let {
                if (hour > 19 || hour < 7) Type.NIGHT else Type.DAY
            } ?: Type.DAY
        }

        @JvmStatic
        fun getSkyIcon(dateTime: Long?, pty: String?, sky: String?): Int {
            val dayOrNight = getDayOrNight(dateTime)
            val rainType = getRainFallType(pty)
            val skyState = getSkyCloudCover(sky)
            return if (rainType == "없음")
                getSkyResId(skyState, dayOrNight)
            else
                getSkyResId(rainType, dayOrNight)
        }
    }
}