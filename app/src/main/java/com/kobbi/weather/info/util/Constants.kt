package com.kobbi.weather.info.util

class Constants private constructor() {
    companion object {
        const val POP = "POP"        //강수확률
        const val PTY = "PTY"        //강수형태
        const val R06 = "R06"        //6시간 강수량
        const val RN1 = "RN1"        //1시간 강수량
        const val REH = "REH"        //습도
        const val S06 = "S06"        //6시간 신적설
        const val SKY = "SKY"        // 하늘상태
        const val T3H = "T3H"        //3시간 기온
        const val T1H = "T1H"        //1시간 기온
        const val TMN = "TMN"        //아침 최저기온
        const val TMX = "TMX"        //낮 최고기온
        const val UUU = "UUU"        //풍속(동서성분)
        const val VVV = "VVV"        //풍속(남북성분)
        const val WAV = "WAV"        //파고
        const val VEC = "VEC"        //풍향
        const val WSD = "WSD"        //풍속
        const val LGT = "LGT"        //낙뢰

        //주기 타입
        const val TYPE_DAY = "DAY"
        const val TYPE_3HOUR = "3HOUR"

        //생활지수 범위
        private val VALUE_FSN_NORMAL = Pair(0 until 35, "관심")
        private val VALUE_FSN_CAUTION = Pair(35 until 70, "주의")
        private val VALUE_FSN_WARNING = Pair(70 until 95, "경고")
        private val VALUE_FSN_DANGER = Pair(95 until Int.MAX_VALUE, "위험")
        val VALUE_RANGE_FSN =
            arrayOf(VALUE_FSN_NORMAL, VALUE_FSN_CAUTION, VALUE_FSN_WARNING, VALUE_FSN_DANGER)

        private val VALUE_WC_TEM_NORMAL = Pair(-10 until Int.MAX_VALUE, "관심")
        private val VALUE_WC_TEM_CAUTION = Pair(-25 until -10, "주의")
        private val VALUE_WC_TEM_WARNING = Pair(-45 until -25, "경고")
        private val VALUE_WC_TEM_DANGER = Pair(Int.MIN_VALUE until -45, "위험")
        val VALUE_RANGE_WC_TEM =
            arrayOf(
                VALUE_WC_TEM_NORMAL,
                VALUE_WC_TEM_CAUTION,
                VALUE_WC_TEM_WARNING,
                VALUE_WC_TEM_DANGER
            )

        private val VALUE_HEAT_LOW = Pair(Int.MIN_VALUE until 32, "낮음")
        private val VALUE_HEAT_NORMAL = Pair(32 until 41, "보통")
        private val VALUE_HEAT_HIGH = Pair(41 until 54, "높음")
        private val VALUE_HEAT_VERY_HIGH = Pair(54 until 66, "매우높음")
        private val VALUE_HEAT_DANGER = Pair(66 until Int.MAX_VALUE, "위험")
        val VALUE_RANGE_HEAT =
            arrayOf(
                VALUE_HEAT_LOW,
                VALUE_HEAT_NORMAL,
                VALUE_HEAT_HIGH,
                VALUE_HEAT_VERY_HIGH,
                VALUE_HEAT_DANGER
            )

        private val VALUE_DSP_LS_LOW =
            Pair(Int.MIN_VALUE until 68, "낮음")                //전원 쾌적함을 느낌
        private val VALUE_DSP_LS_NORMAL = Pair(68 until 75, "보통")            // 불쾌감을 나타내기 시작함
        private val VALUE_DSP_LS_HIGH = Pair(75 until 80, "높음")              //50% 정도 불쾌감을 나타냄
        private val VALUE_DSP_LS_VERY_HIGH = Pair(80 until Int.MAX_VALUE, "매우높음")    //전원 불쾌감을 느낌
        val VALUE_RANGE_DSP_LS =
            arrayOf(
                VALUE_DSP_LS_LOW,
                VALUE_DSP_LS_NORMAL,
                VALUE_DSP_LS_HIGH,
                VALUE_DSP_LS_VERY_HIGH
            )

        private val VALUE_WINTER_LOW = Pair(25 until 50, "낮음")
        private val VALUE_WINTER_NORMAL = Pair(50 until 75, "보통")
        private val VALUE_WINTER_HIGH = Pair(75 until 100, "높음")
        private val VALUE_WINTER_VERY_HIGH = Pair(100 until Int.MAX_VALUE, "매우높음")
        val VALUE_RANGE_WINTER =
            arrayOf(
                VALUE_WINTER_LOW,
                VALUE_WINTER_NORMAL,
                VALUE_WINTER_HIGH,
                VALUE_WINTER_VERY_HIGH
            )

        private val VALUE_ULTRA_V_LOW = Pair(0 until 3, "낮음")
        private val VALUE_ULTRA_V_NORMAL = Pair(3 until 6, "보통")
        private val VALUE_ULTRA_V_HIGH = Pair(6 until 8, "높음")
        private val VALUE_ULTRA_V_VERY_HIGH = Pair(8 until 11, "매우높음")
        private val VALUE_ULTRA_V_DANGER = Pair(11 until Int.MAX_VALUE, "위험")
        val VALUE_RANGE_ULTRA_V = arrayOf(
            VALUE_ULTRA_V_LOW,
            VALUE_ULTRA_V_NORMAL,
            VALUE_ULTRA_V_HIGH,
            VALUE_ULTRA_V_VERY_HIGH,
            VALUE_ULTRA_V_DANGER
        )

        private val VALUE_AIR_LOW = Pair(25 until 50, "낮음")
        private val VALUE_AIR_NORMAL = Pair(50 until 75, "보통")
        private val VALUE_AIR_HIGH = Pair(75 until 100, "높음")
        private val VALUE_AIR_VERY_HIGH = Pair(100 until Int.MAX_VALUE, "매우높음")
        val VALUE_RANGE_AIR =
            arrayOf(VALUE_AIR_LOW, VALUE_AIR_NORMAL, VALUE_AIR_HIGH, VALUE_AIR_VERY_HIGH)

        private val VALUE_SENSORY_HEAT_NORMAL = Pair(0 until 21, "관심")
        private val VALUE_SENSORY_HEAT_CAUTION = Pair(21 until 25, "주의")
        private val VALUE_SENSORY_HEAT_WARNING = Pair(25 until 28, "경고")
        private val VALUE_SENSORY_HEAT_DANGER = Pair(28 until 31, "위험")
        private val VALUE_SENSORY_HEAT_VERY_DANGER = Pair(31 until Int.MAX_VALUE, "매우위험")
        val VALUE_RANGE_SENSORY_HEAT = arrayOf(
            VALUE_SENSORY_HEAT_NORMAL,
            VALUE_SENSORY_HEAT_CAUTION,
            VALUE_SENSORY_HEAT_WARNING,
            VALUE_SENSORY_HEAT_DANGER,
            VALUE_SENSORY_HEAT_VERY_DANGER
        )

        private val VALUE_PM25_LEVEL_GOOD = Pair(0 until 16, "좋음")
        private val VALUE_PM25_LEVEL_NORMAL = Pair(16 until 36, "보통")
        private val VALUE_PM25_LEVEL_BAD = Pair(36 until 76, "나쁨")
        private val VALUE_PM25_LEVEL_VERY_BAD = Pair(76 until Int.MAX_VALUE, "매우나쁨")
        val VALUE_RANGE_PM25_LEVEL = arrayOf(
            VALUE_PM25_LEVEL_GOOD,
            VALUE_PM25_LEVEL_NORMAL,
            VALUE_PM25_LEVEL_BAD,
            VALUE_PM25_LEVEL_VERY_BAD
        )

        private val VALUE_PM10_LEVEL_GOOD = Pair(0 until 31, "좋음")
        private val VALUE_PM10_LEVEL_NORMAL = Pair(31 until 81, "보통")
        private val VALUE_PM10_LEVEL_BAD = Pair(81 until 151, "나쁨")
        private val VALUE_PM10_LEVEL_VERY_BAD = Pair(151 until Int.MAX_VALUE, "매우나쁨")
        val VALUE_RANGE_PM10_LEVEL = arrayOf(
            VALUE_PM10_LEVEL_GOOD,
            VALUE_PM10_LEVEL_NORMAL,
            VALUE_PM10_LEVEL_BAD,
            VALUE_PM10_LEVEL_VERY_BAD
        )

        private val VALUE_HEALTH_LEVEL_LOW = Pair(0 until 1, "낮음")
        private val VALUE_HEALTH_LEVEL_NORMAL = Pair(1 until 2, "보통")
        private val VALUE_HEALTH_LEVEL_HIGH = Pair(2 until 3, "높음")
        private val VALUE_HEALTH_LEVEL_VERY_HIGH = Pair(3 until Int.MAX_VALUE, "매우높음")
        val VALUE_RANGE_HEALTH_LEVEL = arrayOf(
            VALUE_HEALTH_LEVEL_LOW,
            VALUE_HEALTH_LEVEL_NORMAL,
            VALUE_HEALTH_LEVEL_HIGH,
            VALUE_HEALTH_LEVEL_VERY_HIGH
        )



        const val STATE_CODE_ACTIVE = 0
        const val STATE_CODE_INACTIVE = 1
        const val STATE_CODE_LOCATED = 2

        //기상특보 맵핑
        /*
        108     서울  전국
        109     서울  서울, 인천, 경기도
        159     부산  부산. 울산. 경상남도
        143     대구  대구. 경상북도
        156     광주  광주. 전라남도
        146     전주  전라북도
        133     대전  대전. 세종. 충청남도
        131     청주  충청북도
        105     강릉  강원도
        184     제주  제주도
         */
    }
}