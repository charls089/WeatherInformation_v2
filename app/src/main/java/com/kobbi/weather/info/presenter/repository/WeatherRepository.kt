package com.kobbi.weather.info.presenter.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.kobbi.weather.info.data.database.AreaCodeDatabase
import com.kobbi.weather.info.data.database.WeatherDatabase
import com.kobbi.weather.info.data.database.entity.*
import com.kobbi.weather.info.presenter.model.data.*
import com.kobbi.weather.info.presenter.model.type.LifeHealthCode
import com.kobbi.weather.info.presenter.model.type.OfferType
import com.kobbi.weather.info.presenter.model.type.SearchTime
import com.kobbi.weather.info.util.Constants
import com.kobbi.weather.info.util.DLog
import com.kobbi.weather.info.util.LocationUtils
import com.kobbi.weather.info.util.Utils
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap
import kotlin.concurrent.thread
import kotlin.math.pow
import kotlin.math.roundToInt

class WeatherRepository private constructor(context: Context) {
    companion object {
        private const val TAG = "WeatherRepository"
        private var INSTANCE: WeatherRepository? = null

        @JvmStatic
        fun getInstance(context: Context): WeatherRepository {
            return INSTANCE ?: synchronized(this) {
                WeatherRepository(context).also {
                    INSTANCE = it
                }
            }
        }

    }

    private val mWeatherDB: WeatherDatabase = WeatherDatabase.getDatabase(context)
    private val mAreaCodeDB: AreaCodeDatabase = AreaCodeDatabase.getDatabase(context)

    fun insertArea(context: Context, addressList: List<String>, stateCode: Int) {
        thread {
            if (addressList.isNotEmpty()) {
                val endIdx = if (addressList.size >= 4) 4 else addressList.size
                val address = StringBuilder().run {
                    for (i in 0 until endIdx) {
                        if (addressList[i].isNotBlank()) {
                            append(addressList[i])
                            append(" ")
                        }
                    }
                    toString()
                }.dropLast(1)

                val areaName = LocationUtils.getCityCode(address)
                val areaCode = mAreaCodeDB.fcstAreaCodeDao().findCode(areaName)
                val list = LocationUtils.splitAddressLine(address).toMutableList()
                if (list.isNotEmpty()) {
                    if (list[0].endsWith("시"))
                        list.add(0, "")
                    val dao = mAreaCodeDB.lifeAreaCodeDao()

                    fun findAreaCode(list: List<String>): BranchCode? {
                        val size = list.size
                        return when (size) {
                            0 -> null
                            1 -> dao.findCodeFromAddress(list[0])
                            2 -> dao.findCodeFromAddress(list[0], list[1])
                            3 -> dao.findCodeFromAddress(list[0], list[1], list[2])
                            else -> dao.findCodeFromAddress(list[0], list[1], list[2], list[3])
                        } ?: if (size - 1 > 0) findAreaCode(list.subList(0, size - 1)) else null
                    }

                    val pointCode = findAreaCode(list)
                    DLog.d(
                        tag = TAG,
                        message = "insertArea() --> pointCode : ${pointCode.toString()}"
                    )
                    val grid = if (pointCode?.gridX == null)
                        LocationUtils.convertGrid(LocationUtils.convertAddress(context, address))
                    else
                        GridData(pointCode.gridX, pointCode.gridY)
                    val areaNo = pointCode?.areaNo
                    DLog.d(
                        tag = TAG,
                        message = "insertArea() --> area = $address, gridX = ${grid?.x}, gridY = ${grid?.y}, prvnCode = ${areaCode?.prvnCode}, cityCode = ${areaCode?.cityCode}, areaCode = $areaNo"
                    )
                    if (grid != null && areaCode != null && areaNo != null) {
                        val area =
                            Area(
                                address,
                                areaCode.prvnCode,
                                areaCode.cityCode,
                                areaNo,
                                grid.x,
                                grid.y,
                                stateCode
                            )
                        //check Area is active
                        val isActiveArea = loadActiveArea().contains(area)
                        DLog.d(
                            context,
                            "AreaWeather",
                            "insertArea() --> isActiveArea : $isActiveArea"
                        )
                        if (!isActiveArea)
                            ApiRequestRepository.initBaseAreaData(context, area)

                        if (stateCode == Constants.STATE_CODE_LOCATED)
                            changeStateBeforeArea()
                        mWeatherDB.areaDao().insert(area)
                    }
                }
            }
        }
    }

    fun insertWeather(gridData: GridData, item: List<Map<String, String>>?) {
        thread {
            val forecastDataList = getWeatherList(item)
            DLog.d(tag = TAG, message = "insertWeather() --> forecastDataList : $forecastDataList")
            val currentList = mutableListOf<CurrentWeather>()
            val dailyList = mutableListOf<DailyWeather>()
            val gridX = gridData.x
            val gridY = gridData.y
            forecastDataList.forEach { forecastData ->
                val dateTime = forecastData.dateTime.toLong()
                val mapData = forecastData.value

                val t1h = getMapData(mapData, Constants.T1H)
                val rn1 = getMapData(mapData, Constants.RN1)

                val t3h = getMapData(mapData, Constants.T3H)
                val pop = getMapData(mapData, Constants.POP)
                val tmn = getMapData(mapData, Constants.TMN)
                val tmx = getMapData(mapData, Constants.TMX)
                val r06 = getMapData(mapData, Constants.R06)
                val s06 = getMapData(mapData, Constants.S06)
                val wav = getMapData(mapData, Constants.WAV)

                val sky = getMapData(mapData, Constants.SKY)
                val uuu = getMapData(mapData, Constants.UUU)
                val vvv = getMapData(mapData, Constants.VVV)
                val reh = getMapData(mapData, Constants.REH)
                val pty = getMapData(mapData, Constants.PTY)
                val lgt = getMapData(mapData, Constants.LGT)
                val vec = getMapData(mapData, Constants.VEC)
                val wsd = getMapData(mapData, Constants.WSD)
                val wct = getWindChillTemperature(if (t1h.isEmpty()) t3h else t1h, wsd)
                if (sky.isNotEmpty() && wsd.isNotEmpty() && pty.isNotEmpty()) {
                    if (t3h.isNotEmpty()) {
                        dailyList.add(
                            DailyWeather(
                                dateTime, gridX, gridY,
                                pop, t3h, tmn, tmx, r06, s06, sky, uuu, vvv, reh,
                                pty, lgt, wav, vec, wsd, wct
                            )
                        )
                    }
                    if (t1h.isNotEmpty()) {
                        currentList.add(
                            CurrentWeather(
                                dateTime, gridX, gridY,
                                t1h, rn1, sky, uuu, vvv, reh, pty, lgt, vec, wsd, wct
                            )
                        )
                    }
                }
            }
            if (currentList.isNotEmpty())
                mWeatherDB.currentWeatherDao().insertAll(currentList)

            if (dailyList.isNotEmpty())
                mWeatherDB.dailyWeatherDao().insertAll(dailyList)
        }
    }

    fun insertMiddle(areaCode: AreaCode, items: List<Map<String, String>>?) {
        items?.forEach { item ->
            thread {
                val prvnCode = areaCode.prvnCode
                val cityCode = areaCode.cityCode

                val tprList = mutableListOf<WeeklyTpr>()
                val landList = mutableListOf<WeeklyLand>()
                val baseDate = OfferType.getBaseDateTime(OfferType.WEEKLY).first
                Utils.convertStringToDate(date = baseDate)?.let { date ->
                    for (i in 3..7) {
                        val cal = GregorianCalendar().apply {
                            time = date
                            add(Calendar.DATE, i)
                        }
                        val dateTime = Utils.getCurrentTime(time = cal.timeInMillis).toLong()

                        //중기육상예보 데이터
                        val wfAm = getMapData(item, "wf${i}Am")
                        val wfPm = getMapData(item, "wf${i}Pm")
                        val rnStAm = getMapData(item, "rnSt${i}Am")
                        val rnStPm = getMapData(item, "rnSt${i}Pm")

                        //중기기온조회 데이터
                        val taMax = getMapData(item, "taMax$i")
                        val taMin = getMapData(item, "taMin$i")

                        if (wfAm.isEmpty()) {
                            tprList.add(WeeklyTpr(dateTime, prvnCode, cityCode, taMin, taMax))
                        } else {
                            landList.add(
                                WeeklyLand(dateTime, prvnCode, wfAm, wfPm, rnStAm, rnStPm)
                            )
                        }
                    }
                    val weeklyDao = mWeatherDB.weeklyWeatherDao()
                    if (tprList.isEmpty())
                        weeklyDao.insertLand(landList)
                    else
                        weeklyDao.insertTpr(tprList)
                }
            }
        }
    }

    fun insertLife(areaCode: String, items: List<Map<String, String>>?) {
        items?.forEach { item ->
            thread {
                val code = getMapData(item, "code")
                val date = getMapData(item, "date")
                val lifeCode = LifeHealthCode.findLifeCode(code)
                fun insertData(key: String, field: Int, amount: Int) {
                    Utils.convertStringToDate(Utils.VALUE_DATETIME_FORMAT, date)?.let { date ->
                        val data = getMapData(item, key)
                        if (data.isNotEmpty())
                            Calendar.getInstance().run {
                                time = date
                                add(field, amount)
                                val dateTime =
                                    Utils.getCurrentTime(time = this.timeInMillis).toLong()
                                val baseTime =
                                    if (lifeCode!!.type == Constants.TYPE_3HOUR)
                                        Utils.getCurrentTime(
                                            "HH", this.timeInMillis
                                        ).toInt()
                                    else
                                        24
                                DLog.d(
                                    tag = TAG,
                                    message = "insertLife() --> key : $key, dateTime : $dateTime, baseTime : $baseTime"
                                )
                                mWeatherDB.lifeIndexDao().insert(
                                    LifeIndex(
                                        dateTime,
                                        baseTime,
                                        areaCode,
                                        code,
                                        data.toInt()
                                    )
                                )
                            }
                    }
                }
                when (lifeCode?.type) {
                    Constants.TYPE_DAY -> {
                        insertData("today", Calendar.DATE, 0)
                        insertData("tomorrow", Calendar.DATE, 1)
                        insertData("theDayAfterTomorrow", Calendar.DATE, 2)
                    }
                    Constants.TYPE_3HOUR -> {
                        for (i in 1..22) {
                            insertData("h${3 * i}", Calendar.HOUR, 3 * i)
                        }
                    }
                }
            }
        }
    }

    fun insertAirMeasure(sidoName: String, list: List<Map<String, String>>?) {
        thread {
            val results = mutableListOf<AirMeasure>()
            list?.forEach { data ->
                data.let {
                    val cityName = getMapData(it, "cityName")
                    val dataTime = getMapData(it, "dataTime")
                    val so2Value = getMapData(it, "so2Value")
                    val coValue = getMapData(it, "coValue")
                    val no2Value = getMapData(it, "no2Value")
                    val o3Value = getMapData(it, "o3Value")
                    val pm10Value = getMapData(it, "pm10Value")
                    val pm25Value = getMapData(it, "pm25Value")
                    val time = Regex("\\D").replace(dataTime, "")
                    results.add(
                        AirMeasure(
                            time.toLong(), sidoName, cityName,
                            so2Value, coValue, o3Value, no2Value, pm10Value, pm25Value
                        )
                    )
                }
            }
            if (results.isNotEmpty())
                mWeatherDB.airMeasureDao().insert(results)
        }
    }

    fun insertSpecialNews(items: List<Map<String, String>>?) {
        items?.forEach { item ->
            thread {
                val tmFc = getMapData(item, "tmFc")
                val tmSeq = getMapData(item, "tmSeq")
                val tmEf = getMapData(item, "tmEf")
                val t6 = getMapData(item, "t6")
                val t7 = getMapData(item, "t7")
                val other = getMapData(item, "other")

                val key = "${tmFc.substring(0..5)}_${tmSeq}"
                mWeatherDB.specialNewsDao().insert(
                    SpecialNews(
                        key, tmFc.toLong(), tmSeq.toInt(), tmEf.toLong(), t6, t7, other
                    )
                )
            }
        }
    }

    fun insertPlace(address: String) {
        thread {
            mWeatherDB.favoritePlaceDao().insert(FavoritePlace(address))
        }
    }

    fun updateAreaCode(address: String, stateCode: Int): Int {
        return mWeatherDB.areaDao().updateCodeArea(address, stateCode)
    }

    fun findArea(address: String) = mWeatherDB.areaDao().findArea(address)

    fun loadArea() = mWeatherDB.areaDao().loadAll()

    fun loadActiveArea() = mWeatherDB.areaDao().loadActiveArea()

    fun loadActiveAreaLive() = mWeatherDB.areaDao().loadActiveAreaLive()

    fun loadLocatedArea() = mWeatherDB.areaDao().loadLocatedArea()

    fun loadAreaFromAddress(address: String) = mWeatherDB.areaDao().findArea(address)

    fun loadAllAddress() = mWeatherDB.areaDao().loadAllAddress()

    fun loadAllSidoName(): List<String> {
        val sidoList = mutableSetOf<String>()
        mWeatherDB.areaDao().loadAllAddress().forEach { address ->
            val splitAddr = LocationUtils.splitAddressLine(address)
            if (splitAddr.isNotEmpty())
                sidoList.add(splitAddr[0])
        }
        DLog.d(tag = TAG, message = "loadAllSidoName() --> list : $sidoList")
        return sidoList.toList()
    }

    fun loadAllGridData() = mWeatherDB.areaDao().loadAllGridData()

    fun loadAllAreaCode() = mWeatherDB.areaDao().loadAllAreaCode()

    fun loadAllAreaNo() = mWeatherDB.areaDao().loadAllAreaNo()

    fun loadPlaceAddress() = mWeatherDB.favoritePlaceDao().loadAddress()

    fun loadPlaceAddressLive() = mWeatherDB.favoritePlaceDao().loadAddressLive()

    fun loadWeatherInfo(area: Area?): WeatherInfo? {
        DLog.d(tag = TAG, message = "loadWeatherInfo() --> area : $area")
        area?.run {
            val today = SearchTime.getDate(SearchTime.DEFAULT)
            val time = SearchTime.getDate(SearchTime.CURRENT)
            DLog.d(
                tag = TAG,
                message = "loadWeatherInfo() --> today : $today, time : $time, gridX : $gridX, gridY : $gridY"
            )
            val weatherInfo =
                mWeatherDB.weatherInfoDao()
                    .loadWeatherInfo(address, today, time, gridX, gridY)
            LocationUtils.splitAddressLine(address).let {
                if (it.size >= 2) {
                    val sidoName = it[0]
                    val cityName = it[1]
                    val yesterdayWeather = findYesterdayWeather(gridX, gridY)
                    val airMeasure = findAirMeasureData(sidoName, cityName)
                    DLog.d(tag = TAG, message = "sidoName : $sidoName, cityName : $cityName")
                    weatherInfo?.run {
                        yesterdayTpr = yesterdayWeather?.t1h
                        yesterdayWct = yesterdayWeather?.wct
                        pm10 = airMeasure?.pm10
                        pm25 = airMeasure?.pm25
                    }
                }
            }
            DLog.d(tag = TAG, message = "loadWeatherInfo() --> weatherInfo : $weatherInfo")
            return weatherInfo
        }
        return null
    }

    fun loadCurrentWeather() =
        mWeatherDB.currentWeatherDao().load(SearchTime.getDate(SearchTime.CURRENT))

    fun loadCurrentWeatherLive() =
        mWeatherDB.currentWeatherDao().loadLive(SearchTime.getDate(SearchTime.CURRENT))

    fun findYesterdayWeather(x: Int, y: Int) =
        mWeatherDB.currentWeatherDao().findData(SearchTime.getDate(SearchTime.YESTERDAY), x, y)

    fun loadYesterdayWeatherLive() =
        mWeatherDB.currentWeatherDao().loadLive(SearchTime.getDate(SearchTime.YESTERDAY))

    fun loadDailyWeatherLive(): LiveData<List<DailyWeather>> {
        val dailyTerm = SearchTime.getTerm(SearchTime.DAILY_START, SearchTime.DAILY_END)
        return mWeatherDB.dailyWeatherDao().loadLive(dailyTerm.first, dailyTerm.second)
    }

    fun findDailyWeather(x: Int, y: Int): List<DailyWeather> {
        val dailyTerm = SearchTime.getTerm(SearchTime.DAILY_START, SearchTime.DAILY_END)
        return mWeatherDB.dailyWeatherDao().findData(dailyTerm.first, dailyTerm.second, x, y)
    }

    fun findMinMaxTpr(x: Int, y: Int) =
        mWeatherDB.dailyWeatherDao().findMinMaxTpr(SearchTime.getDate(SearchTime.DEFAULT), x, y)

    fun loadMinMaxTprLive() =
        mWeatherDB.dailyWeatherDao().findMinMaxTprLive(SearchTime.getDate(SearchTime.DEFAULT))

    fun loadWeekWeatherLive(): LiveData<List<DailyWeather>> {
        val weekTerm = SearchTime.getTerm(SearchTime.WEEK_START, SearchTime.WEEK_CHECK)
        return mWeatherDB.dailyWeatherDao().findWeekWeatherLive(weekTerm.first, weekTerm.second)
    }

    fun findWeekWeatherData(): List<DailyWeather> {
        val weekTerm = SearchTime.getTerm(SearchTime.WEEK_START, SearchTime.WEEK_CHECK)
        return mWeatherDB.dailyWeatherDao().findWeekWeather(weekTerm.first, weekTerm.second)
    }

    fun loadWeeklyWeatherLive(): LiveData<List<WeeklyWeather>> {
        val weeklyTerm = SearchTime.getTerm(SearchTime.WEEKLY_CHECK, SearchTime.WEEKLY_END)
        return mWeatherDB.weeklyWeatherDao().loadLive(weeklyTerm.first, weeklyTerm.second)
    }

    fun findWeeklyWeatherData(cityCode: String): List<WeeklyWeather> {
        val weeklyTerm = SearchTime.getTerm(SearchTime.WEEKLY_CHECK, SearchTime.WEEKLY_END)
        return mWeatherDB.weeklyWeatherDao().findData(weeklyTerm.first, weeklyTerm.second, cityCode)
    }

    fun loadLifeIndexLive(): LiveData<List<LifeIndex>> {
        val dateTime = SearchTime.getTime(SearchTime.LIFE)
        DLog.d(tag = TAG, message = "loadLifeIndexLive() --> dateTime : $dateTime")
        return mWeatherDB.lifeIndexDao().loadLive(dateTime.first, dateTime.second)
    }

    fun findLifeIndexData(areaCode: String): List<LifeIndex> {
        val dateTime = SearchTime.getTime(SearchTime.LIFE)
        return mWeatherDB.lifeIndexDao().findData(dateTime.first, dateTime.second, areaCode)
    }

    fun loadAirMeasureLive() = mWeatherDB.airMeasureDao().loadLive()

    fun findAirMeasureData(sidoName: String, cityName: String) =
        mWeatherDB.airMeasureDao().findData(sidoName, cityName)

    fun loadSpecialNewsLive() =
        mWeatherDB.specialNewsDao().loadLastDataLive(SearchTime.getDate(SearchTime.SPECIAL))

    fun deletePlace(list: List<String>) {
        val deleteList = mutableListOf<FavoritePlace>()
        for (address in list) {
            deleteList.add(com.kobbi.weather.info.data.database.entity.FavoritePlace(address))
        }
        mWeatherDB.favoritePlaceDao().delete(deleteList)
    }

    private fun getWeatherList(items: List<Map<String, String>>?): List<ForecastData> {
        val linkedMap = LinkedHashMap<String, HashMap<String, String>>()
        items?.forEach { item ->
            val dateTime = getMapData(item, "fcstDate") + getMapData(item, "fcstTime")
            val category = getMapData(item, "category")
            val value = getMapData(item, "fcstValue")
            if (linkedMap[dateTime] == null)
                linkedMap[dateTime] = HashMap()
            linkedMap[dateTime]?.put(category, value)
        }
        val forecastDataList = mutableListOf<ForecastData>()
        linkedMap.forEach { map ->
            forecastDataList.add(
                ForecastData(
                    map.key,
                    map.value
                )
            )
        }
        return forecastDataList
    }

    private fun changeStateBeforeArea() {
        val lastAddress = loadLocatedArea()?.address
        lastAddress?.let {
            val favoritePlaceList = loadPlaceAddress()
            val stateCode = if (favoritePlaceList.contains(lastAddress))
                Constants.STATE_CODE_ACTIVE
            else
                Constants.STATE_CODE_INACTIVE
            updateAreaCode(lastAddress, stateCode)
        }
    }

    private fun getMapData(mapData: Map<String, String>, key: String): String {
        return if (mapData.contains(key)) mapData.getValue(key) else ""
    }

    /**
     * Formula : 13.12 + 0.6215 * t - 11.37 * v^0.16 + 0.3965 * v^0.16 * t (v : km/h)
     *
     * @param t Temperature
     * @param w Wind Velocity (m/s)
     */
    private fun getWindChillTemperature(t: String, w: String): String {
        return try {
            val tpr = t.toDouble()
            val v = w.toDouble() * 3.6
            (13.12 + 0.6215 * tpr - 11.37 * v.pow(0.16) + 0.3965 * v.pow(0.16) * tpr).roundToInt()
                .toString()
        } catch (e: NumberFormatException) {
            ""
        } catch (e: IllegalArgumentException) {
            ""
        }
    }
}