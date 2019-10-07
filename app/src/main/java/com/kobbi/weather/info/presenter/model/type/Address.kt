package com.kobbi.weather.info.presenter.model.type

enum class Address(
    val engName: String,
    val fullName: String,
    val shortName: String
) {
    SEOUL("seoul", "서울특별시", "서울"),
    BUSAN("busan", "부산광역시", "부산"),
    INCHEON("incheon", "인천광역시", "인천"),
    DAEGU("daegu", "대구광역시", "대구"),
    GWANGJU("gwangju", "광주광역시", "광주"),
    DAEJEON("daejeon", "대전광역시", "대전"),
    ULSAN("ulsan", "울산광역시", "울산"),
    GG("kyeonggi", "경기도", "경기"),
    GW("kangwon", "강원도", "강원"),
    CB("chungbuk", "충청북도", "충북"),
    CN("chungnam", "충청남도", "충남"),
    JB("jeonbuk", "전라북도", "전북"),
    JN("jeonnam", "전라남도", "전남"),
    GSB("kyeongbuk", "경상북도", "경북"),
    GSN("kyeongnam", "경상남도", "경남"),
    JEJU("jeju", "제주특별자치도", "제주"),
    SEJONG("sejong", "세종특별자치시", "세종");

    companion object {
        fun getSidoCode(fullName: String): Address? {
            return try {
                values().first {
                    it.fullName == fullName
                }
            } catch (e: NoSuchElementException) {
                null
            }
        }

        fun getFullName(shortName: String): Address? {
            return try {
                values().first {
                    it.shortName == shortName
                }
            } catch (e: NoSuchElementException) {
                null
            }
        }
    }
}