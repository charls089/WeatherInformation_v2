package com.kobbi.weather.info.util

/**
 *
 */
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
        private val VALUE_FSN_NORMAL = Pair(0, "관심")
        private val VALUE_FSN_CAUTION = Pair(35, "주의")
        private val VALUE_FSN_WARNING = Pair(70, "경고")
        private val VALUE_FSN_DANGER = Pair(95, "위험")
        val VALUE_RANGE_FSN = arrayOf(VALUE_FSN_NORMAL, VALUE_FSN_CAUTION, VALUE_FSN_WARNING, VALUE_FSN_DANGER)

        private val VALUE_WC_TEM_NORMAL = Pair(10, "관심")
        private val VALUE_WC_TEM_CAUTION = Pair(-10, "주의")
        private val VALUE_WC_TEM_WARNING = Pair(-25, "경고")
        private val VALUE_WC_TEM_DANGER = Pair(-45, "위험")
        val VALUE_RANGE_TEM =
            arrayOf(VALUE_WC_TEM_NORMAL, VALUE_WC_TEM_CAUTION, VALUE_WC_TEM_WARNING, VALUE_WC_TEM_DANGER)

        private val VALUE_HEAT_LOW = Pair(0, "낮음")
        private val VALUE_HEAT_NORMAL = Pair(32, "보통")
        private val VALUE_HEAT_HIGH = Pair(41, "높음")
        private val VALUE_HEAT_VERY_HIGH = Pair(54, "매우높음")
        private val VALUE_HEAT_DANGER = Pair(66, "위험")
        val VALUE_RANGE_HEAT =
            arrayOf(VALUE_HEAT_LOW, VALUE_HEAT_NORMAL, VALUE_HEAT_HIGH, VALUE_HEAT_VERY_HIGH, VALUE_HEAT_DANGER)

        private val VALUE_DSP_LS_LOW = Pair(0, "낮음")                //전원 쾌적함을 느낌
        private val VALUE_DSP_LS_NORMAL = Pair(68, "보통")            // 불쾌감을 나타내기 시작함
        private val VALUE_DSP_LS_HIGH = Pair(75, "높음")              //50% 정도 불쾌감을 나타냄
        private val VALUE_DSP_LS_VERY_HIGH = Pair(80, "매우높음")    //전원 불쾌감을 느낌
        val VALUE_RANGE_DSP_LS =
            arrayOf(VALUE_DSP_LS_LOW, VALUE_DSP_LS_NORMAL, VALUE_DSP_LS_HIGH, VALUE_DSP_LS_VERY_HIGH)

        private val VALUE_WINTER_LOW = Pair(25, "낮음")
        private val VALUE_WINTER_NORMAL = Pair(50, "보통")
        private val VALUE_WINTER_HIGH = Pair(75, "높음")
        private val VALUE_WINTER_VERY_HIGH = Pair(100, "매우높음")
        val VALUE_RANGE_WINTER =
            arrayOf(VALUE_WINTER_LOW, VALUE_WINTER_NORMAL, VALUE_WINTER_HIGH, VALUE_WINTER_VERY_HIGH)

        private val VALUE_ULTRA_V_LOW = Pair(0, "낮음")
        private val VALUE_ULTRA_V_NORMAL = Pair(3, "보통")
        private val VALUE_ULTRA_V_HIGH = Pair(6, "높음")
        private val VALUE_ULTRA_V_VERY_HIGH = Pair(8, "매우높음")
        private val VALUE_ULTRA_V_DANGER = Pair(11, "위험")
        val VALUE_RANGE_ULTRA_V = arrayOf(
            VALUE_ULTRA_V_LOW,
            VALUE_ULTRA_V_NORMAL,
            VALUE_ULTRA_V_HIGH,
            VALUE_ULTRA_V_VERY_HIGH,
            VALUE_ULTRA_V_DANGER
        )

        private val VALUE_SENSORY_HEAT_NORMAL = Pair(0, "관심")
        private val VALUE_SENSORY_HEAT_CAUTION = Pair(21, "주의")
        private val VALUE_SENSORY_HEAT_WARNING = Pair(25, "경고")
        private val VALUE_SENSORY_HEAT_DANGER = Pair(28, "위험")
        private val VALUE_SENSORY_HEAT_VERY_DANGER = Pair(31, "매우위험")
        val VALUE_RANGE_SENSORY_HEAT = arrayOf(
            VALUE_SENSORY_HEAT_NORMAL,
            VALUE_SENSORY_HEAT_CAUTION,
            VALUE_SENSORY_HEAT_WARNING,
            VALUE_SENSORY_HEAT_DANGER,
            VALUE_SENSORY_HEAT_VERY_DANGER
        )

        private val VALUE_AIR_LOW = Pair(25, "낮음")
        private val VALUE_AIR_NORMAL = Pair(50, "보통")
        private val VALUE_AIR_HIGH = Pair(75, "높음")
        private val VALUE_AIR_VERY_HIGH = Pair(100, "매우높음")
        val VALUE_RANGE_AIR =
            arrayOf(VALUE_AIR_LOW, VALUE_AIR_NORMAL, VALUE_AIR_HIGH, VALUE_AIR_VERY_HIGH)

        private val VALUE_PM25_LEVEL_GOOD = Pair(0, "좋음")
        private val VALUE_PM25_LEVEL_NORMAL = Pair(16, "보통")
        private val VALUE_PM25_LEVEL_BAD = Pair(36, "나쁨")
        private val VALUE_PM25_LEVEL_VERY_BAD = Pair(76, "매우나쁨")
        val VALUE_RANGE_PM25_LEVEL = arrayOf(
            VALUE_PM25_LEVEL_GOOD,
            VALUE_PM25_LEVEL_NORMAL,
            VALUE_PM25_LEVEL_BAD,
            VALUE_PM25_LEVEL_VERY_BAD
        )

        private val VALUE_PM10_LEVEL_GOOD = Pair(0, "좋음")
        private val VALUE_PM10_LEVEL_NORMAL = Pair(31, "보통")
        private val VALUE_PM10_LEVEL_BAD = Pair(81, "나쁨")
        private val VALUE_PM10_LEVEL_VERY_BAD = Pair(151, "매우나쁨")
        val VALUE_RANGE_PM10_LEVEL = arrayOf(
            VALUE_PM10_LEVEL_GOOD,
            VALUE_PM10_LEVEL_NORMAL,
            VALUE_PM10_LEVEL_BAD,
            VALUE_PM10_LEVEL_VERY_BAD
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