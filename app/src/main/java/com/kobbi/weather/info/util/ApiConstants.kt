package com.kobbi.weather.info.util

import com.kobbi.weather.info.presenter.model.type.OfferType
import java.util.*

class ApiConstants private constructor() {
    companion object {

        const val API_KMA_BASE_URL = "http://apis.data.go.kr/1360000/"

        //동네 예보
        const val API_VILLAGE_SERVICE = "VilageFcstInfoService"
        const val API_FORECAST_GRIB = "getUltraSrtNcst"
        const val API_FORECAST_TIME_DATA = "getUltraSrtFcst"
        const val API_FORECAST_SPACE_DATA = "getVilageFcst"
        const val API_VERSION_CHECK = "getFcstVersion"

        //중기 예보
        const val API_MIDDLE_SERVICE = "MidFcstInfoService"
        const val API_MIDDLE_FORECAST = "getMidFcst"
        const val API_MIDDLE_LAND_WEATHER = "getMidLandFcst"
        const val API_MIDDLE_SEA_WEATHER = "getMidSeaFcst"
        const val API_MIDDLE_TEMPERATURE = "getMidTa"

        //기상특보
        const val API_SPECIAL_SERVICE = "WthrWrnInfoService"
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
        const val API_LIFE_SERVICE = "LivingWthrIdxService"

        //보건기상지수
        const val API_HEALTH_SERVICE = "HealthWthrIdxService"

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
        const val API_EPOST_BASE_URL = "http://openapi.epost.go.kr/postal/"
        const val API_EPOST_SERVICE = "retrieveLotNumberAdressAreaCdService/"
        const val API_PRVN_URL = "getBorodCityList"        //광역시,도
        const val API_CITY_URL = "getSiGunGuList"          // 시,군,구
        const val API_DONG_URL = "getEupMyunDongList"      //읍,면,동
    }

    enum class LifeHealthApi(
        val apiName: String,
        val url: String,
        val startMonth: Int,
        val endMonth: Int,
        val offerType: OfferType,
        val serviceType: ServiceType
    ) {
        SENSORY_TEM("체감온도", "getWindChillIdx", Calendar.NOVEMBER, Calendar.MARCH, OfferType.LIFE_TIME, ServiceType.LIFE),
        DISCOMFORT("불쾌지수", "getDiscomfortIdx", Calendar.JUNE, Calendar.SEPTEMBER, OfferType.LIFE_TIME, ServiceType.LIFE),
        WINTER("동파가능지수", "getFreezeIdx", Calendar.NOVEMBER, Calendar.MARCH, OfferType.LIFE_TIME, ServiceType.LIFE),
        ULTRA_V("자외선지수", "getUVIdx", Calendar.JANUARY, Calendar.DECEMBER, OfferType.LIFE_DAY, ServiceType.LIFE),
        AIR_DIFFUSION("대기확산지수", "getAirDiffusionIdx", Calendar.JANUARY, Calendar.DECEMBER, OfferType.LIFE_TIME, ServiceType.LIFE),
        SENSORY_HEAT("더위체감지수", "getHeatFeelingIdx", Calendar.MAY, Calendar.SEPTEMBER, OfferType.LIFE_TIME, ServiceType.LIFE),

        ASTHMA("폐질환가능지수", "getAsthmaIdx", Calendar.JANUARY, Calendar.DECEMBER, OfferType.LIFE_DAY, ServiceType.HEALTH),
        STROKE("뇌졸중가능지수", "getStrokeIdx", Calendar.JANUARY, Calendar.DECEMBER, OfferType.LIFE_DAY, ServiceType.HEALTH),
        FOOD_POISON("식중독지수", "getFoodPoisoningIdx", Calendar.JANUARY, Calendar.DECEMBER, OfferType.LIFE_DAY, ServiceType.HEALTH),
        OAK_POLLEN_RISK("꽃가루농도위험지수(참나무)", "getOakPollenRiskIdx", Calendar.APRIL, Calendar.MAY, OfferType.LIFE_DAY, ServiceType.HEALTH),
        PINE_POLLEN_RISK("꽃가루농도위험지수(소나무)", "getPinePollenRiskIdx", Calendar.APRIL, Calendar.MAY, OfferType.LIFE_DAY, ServiceType.HEALTH),
        WEEDS_POLLEN_RISK("꽃가루농도위험지수(잡초류)", "getWeedsPollenRiskndx", Calendar.SEPTEMBER, Calendar.OCTOBER, OfferType.LIFE_DAY, ServiceType.HEALTH),
        COLD("감기가능지수", "getAsthmaIdx", Calendar.SEPTEMBER, Calendar.APRIL, OfferType.LIFE_DAY, ServiceType.HEALTH);

        companion object {
            @JvmStatic
            fun checkRequestUrl(): List<LifeHealthApi> {
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

    enum class ServiceType {
        LIFE, HEALTH
    }
}