package com.kobbi.weather.info.util

import java.util.*

class ApiConstants private constructor() {
    companion object {

        const val API_KMA_BASE_URL = "http://newsky2.kma.go.kr/"

        //동네 예보
        const val API_WEATHER_SERVICE = "service/SecndSrtpdFrcstInfoService2/"
        const val API_FORECAST_GRIB = "ForecastGrib"
        const val API_FORECAST_TIME_DATA = "ForecastTimeData"
        const val API_FORECAST_SPACE_DATA = "ForecastSpaceData"
        const val API_VERSION_CHECK = "ForecastVersionCheck"

        //중기 예보
        const val API_MIDDLE_SERVICE = "service/MiddleFrcstInfoService/"
        const val API_MIDDLE_FORECAST = "getMiddleForecast"
        const val API_MIDDLE_LAND_WEATHER = "getMiddleLandWeather"
        const val API_MIDDLE_SEA_WEATHER = "getMiddleSeaWeather"
        const val API_MIDDLE_TEMPERATURE = "getMiddleTemperature"
        const val API_MIDDLE_LAND_WEATHER_CONF = "getMiddleLandWeatherConf"

        //기상특보
        const val API_SPECIAL_REPORT_SERVICE = "service/WetherSpcnwsInfoService/"
        const val API_SPECIAL_WARNING_LIST = "WeatherWarningList"                      //기상특보목록조회
        const val API_SPECIAL_WARNING_ITEM = "WeatherWarningItem"                      //기상특보통보문조회
        const val API_SPECIAL_WEATHER_INFO_LIST = "WeatherInfomationList"              //기상정보목록조회
        const val API_SPECIAL_WEATHER_INFO = "WeatherInformation"                      //기상정보문조회
        const val API_SPECIAL_BREAKING_LIST = "WeatherBreakingNewsList"                //기상속보목록조회
        const val API_SPECIAL_BREAKING = "WeatherAnnouncement"                         //기상속보조회
        const val API_SPECIAL_PREP_LIST = "PreparationSprcialNewsList"                 //기상예비특보목록조회
        const val API_SPECIAL_PREP = "WeatherPrepareWarning"                           //기상예비특보조회
        const val API_SPECIAL_CODE = "SpecialNewsCode"                                 //특보코드조회
        const val API_SPECIAL_STATUS = "SpecialNewsStatus"                             //특보현황조회


        //생활기상지수
        const val API_LIFE_SERVICE = "iros/RetrieveLifeIndexService3/"

        //대기 오염정보
        const val API_AIRKOREA_BASE_URL =
            "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/"
        const val API_STATION_MEASURE_DATA = "getMsrstnAcctoRltmMesureDnsty"     //측정소별 실시간 측정정보 조회
        const val API_PLACE_MEASURE_DATA = "getCtprvnRltmMesureDnsty"    //시도별 실시간 측정정보 조회
        const val API_UNITY_AIR_IDX_BAD_LIST =
            "getUnityAirEnvrnIdexSnstiveAboveMsrstnList"    //통합대기환경지수 나쁨 이상 측정소 목록조회
        const val API_DUST_FORECAST = "getMinuDustFrcstDspth"    //대기질 예보통보 조회
        const val API_CITY_PROVINCE_AVG_LIST = "getCtprvnMesureLIst"    //시도별 실시간 평균정보 조회
        const val API_DISTRICT_AVG_LIST = "getCtprvnMesureSidoLIst"    //시군구별 실시간 평균정보 조회

        //지번주소 조회서비스
        const val API_JUSO_BASE_URL = "http://www.juso.go.kr/addrlink/addrLinkApi.do"
        const val API_EPOST_BASE_URL = "http://openapi.epost.go.kr/postal/"
        const val API_EPOST_SERVICE = "retrieveLotNumberAdressAreaCdService/"
        const val API_PRVN_URL = "getBorodCityList"        //광역시,도
        const val API_CITY_URL = "getSiGunGuList"          // 시,군,구
        const val API_DONG_URL = "getEupMyunDongList"      //읍,면,동
    }

    enum class LifeApi(name: String, val url: String, val startMonth: Int, val endMonth: Int) {
        SENSORY_TEM("체감온도", "getSensorytemLifeList", Calendar.NOVEMBER, Calendar.MARCH),
        WINTER("동파가능지수", "getWinterLifeList", Calendar.DECEMBER, Calendar.FEBRUARY),
        ULTRA_V("자외선지수", "getUltrvLifeList", Calendar.MARCH, Calendar.NOVEMBER),
        FSN("식중독지수", "getFsnLifeList", Calendar.JANUARY, Calendar.DECEMBER),
        HEAT("열지수", "getHeatLifeList", Calendar.JUNE, Calendar.SEPTEMBER),
        DSPLS("불쾌지수", "getDsplsLifeList", Calendar.JUNE, Calendar.SEPTEMBER),
        AIR_POLLUTION("대기오염확산지수", "getAirpollutionLifeList", Calendar.NOVEMBER, Calendar.MAY),
        SENSORY_HEAT("더위체감지수", "getSensoryHeatLifeList", Calendar.MAY, Calendar.SEPTEMBER);

        companion object {
            @JvmStatic
            fun checkRequestUrl(): List<LifeApi> {
                val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
                return values().filter {
                    it.run {
                        if (startMonth <= endMonth) {
                            currentMonth in startMonth..endMonth
                        } else {
                            currentMonth in startMonth..Calendar.DECEMBER || currentMonth in Calendar.JANUARY..endMonth
                        }
                    }
                }
            }
        }
    }
}