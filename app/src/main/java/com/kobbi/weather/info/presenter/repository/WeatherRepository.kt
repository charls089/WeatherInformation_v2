package com.kobbi.weather.info.presenter.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.kobbi.weather.info.data.database.AreaCodeDatabase
import com.kobbi.weather.info.data.database.WeatherDatabase
import com.kobbi.weather.info.data.database.entity.*
import com.kobbi.weather.info.data.network.domain.news.NewsItem
import com.kobbi.weather.info.data.network.domain.weather.WeatherItem
import com.kobbi.weather.info.presenter.model.data.AreaCode
import com.kobbi.weather.info.presenter.model.data.ForecastData
import com.kobbi.weather.info.presenter.model.data.GridData
import com.kobbi.weather.info.presenter.model.data.BranchCode
import com.kobbi.weather.info.presenter.model.type.LifeCode
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
                    DLog.d(javaClass, "insertArea() --> pointCode : ${pointCode.toString()}")
                    val grid = if (pointCode?.gridX == null)
                        LocationUtils.convertGrid(LocationUtils.convertAddress(context, address))
                    else
                        GridData(pointCode.gridX, pointCode.gridY)
                    val areaNo = pointCode?.areaNo
                    DLog.d(
                        javaClass,
                        "insertArea() --> area = $address, gridX = ${grid?.x}, gridY = ${grid?.y}, prvnCode = ${areaCode?.prvnCode}, cityCode = ${areaCode?.cityCode}, areaCode = $areaNo"
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
                        ApiRequestRepository.initBaseAreaData(context, area)
                        if (stateCode == Constants.STATE_CODE_LOCATED)
                            changeStateBeforeArea()
                        mWeatherDB.areaDao().insert(area)
                    }
                }
            }
        }
    }

    fun insertWeather(gridData: GridData, item: List<WeatherItem>?) {
        thread {
            val forecastDataList = getWeatherList(item)
            DLog.d(javaClass, "forecastDataList : $forecastDataList")
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

    fun insertMiddle(areaCode: AreaCode, item: Map<String, String>?) {
        item?.let {
            thread {
                val prvnCode = areaCode.prvnCode
                val cityCode = areaCode.cityCode

                val tprList = mutableListOf<WeeklyTpr>()
                val landList = mutableListOf<WeeklyLand>()
                for (i in 3..7) {
                    val cal = GregorianCalendar().apply {
                        add(Calendar.DATE, i)
                    }
                    val dateTime = Utils.getCurrentTime(time = cal.timeInMillis).toLong()
                    val wfAm = getMapData(it, "wf${i}Am")
                    val wfPm = getMapData(it, "wf${i}Pm")
                    val taMax = getMapData(it, "taMax$i")
                    val taMin = getMapData(it, "taMin$i")
                    val rnStAm = getMapData(it, "rnSt${i}Am")
                    val rnStPm = getMapData(it, "rnSt${i}Pm")
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

    fun insertLife(areaCode: String, indexModel: Map<String, String>?) {
        indexModel?.let {
            thread {
                val code = getMapData(it, "code")
                val date = getMapData(it, "date")
                val dateTime = date.dropLast(2)
                val baseTime = date.takeLast(2)
                val lifeCode = LifeCode.findLifeCode(code)
                when (lifeCode?.type) {
                    Constants.TYPE_DAY -> {
                        val today = getMapData(it, "today").toInt()
                        val todayData = LifeIndexDay(dateTime.toLong(), areaCode, code, today)
                        mWeatherDB.lifeDayDao().insert(todayData)
                        Utils.convertStringToDate(date = dateTime)?.let { tomorrowDate ->
                            val tomorrow = getMapData(it, "tomorrow").toInt()
                            Calendar.getInstance().run {
                                time = tomorrowDate
                                add(Calendar.DATE, 1)
                                val tomorrowDateTime =
                                    Utils.getCurrentTime(time = this.timeInMillis).toLong()
                                val tomorrowData = LifeIndexDay(
                                    tomorrowDateTime, areaCode, code, tomorrow
                                )
                                mWeatherDB.lifeDayDao().insert(tomorrowData)
                            }
                        }
                    }
                    Constants.TYPE_3HOUR -> {
                        val valueList = mutableListOf<String>()
                        for (i in 1..8) {
                            val h = getMapData(it, "h${3 * i}")
                            valueList.add(h)
                        }
                        DLog.d(
                            javaClass, "valueList -> size : ${valueList.size}, data : $valueList"
                        )
                        if (valueList.size >= 8) {
                            val data = LifeIndexHour(
                                dateTime.toLong(),
                                areaCode,
                                code,
                                baseTime,
                                valueList[0].toInt(),
                                valueList[1].toInt(),
                                valueList[2].toInt(),
                                valueList[3].toInt(),
                                valueList[4].toInt(),
                                valueList[5].toInt(),
                                valueList[6].toInt(),
                                valueList[7].toInt()
                            )
                            mWeatherDB.lifeHourDao().insert(data)
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

    fun insertSpecialNews(item: NewsItem?) {
        item?.let {
            thread {
                val key = "${item.tmFc.toString().substring(0..5)}_${item.tmSeq}"
                mWeatherDB.specialNewsDao().insert(
                    SpecialNews(
                        key, item.tmFc, item.tmSeq, item.tmEf, item.t6, item.t7, item.other
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

    fun loadAllCityName(): List<String> {
        val cityList = mutableSetOf<String>()
        mWeatherDB.areaDao().loadAllAddress().forEach { address ->
            val splitAddr = LocationUtils.splitAddressLine(address)
            if (splitAddr.isNotEmpty())
                cityList.add(splitAddr[0])
        }
        DLog.d(javaClass, "loadAllCityName() --> list : $cityList")
        return cityList.toList()
    }

    fun loadAllGridData() = mWeatherDB.areaDao().loadAllGridData()

    fun loadAllAreaCode() = mWeatherDB.areaDao().loadAllAreaCode()

    fun loadAllAreaNo() = mWeatherDB.areaDao().loadAllAreaNo()

    fun loadPlaceAddress() = mWeatherDB.favoritePlaceDao().loadAddress()

    fun loadPlaceAddressLive() = mWeatherDB.favoritePlaceDao().loadAddressLive()

    fun getNotificateData(date: Long, yesterday: Long, time: Long, x: Int, y: Int) =
        mWeatherDB.weatherInfoDao().getWeatherInfo(date, yesterday, time, x, y)

    fun findCurrentWeather(x: Int, y: Int) =
        mWeatherDB.currentWeatherDao().findData(SearchTime.getDate(SearchTime.CURRENT), x, y)

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

    fun findMinMaxTpr(x: Int, y: Int) =
        mWeatherDB.dailyWeatherDao().findMinMaxTpr(SearchTime.getDate(SearchTime.DEFAULT), x, y)

    fun loadMinMaxTprLive() =
        mWeatherDB.dailyWeatherDao().findMinMaxTprLive(SearchTime.getDate(SearchTime.DEFAULT))

    fun loadWeekWeatherLive(): LiveData<List<DailyWeather>> {
        val weekTerm = SearchTime.getTerm(SearchTime.WEEK_START, SearchTime.WEEK_CHECK)
        return mWeatherDB.dailyWeatherDao().findWeekWeatherLive(weekTerm.first, weekTerm.second)
    }

    fun loadWeeklyWeatherLive(): LiveData<List<WeeklyWeather>> {
        val weeklyTerm = SearchTime.getTerm(SearchTime.WEEKLY_CHECK, SearchTime.WEEKLY_END)
        return mWeatherDB.weeklyWeatherDao().loadLive(weeklyTerm.first, weeklyTerm.second)
    }

    fun loadLifeIndexDayLive() =
        mWeatherDB.lifeDayDao().loadLive(SearchTime.getDate(SearchTime.LIFE))

    fun loadLifeIndexHourLive() =
        mWeatherDB.lifeHourDao().loadLive(SearchTime.getDate(SearchTime.LIFE))

    fun loadAirMeasureLive() = mWeatherDB.airMeasureDao().loadLive()

    fun loadSpecialNewsLive() = mWeatherDB.specialNewsDao().loadLastDataLive()

    fun deletePlace(list: List<String>) {
        val deleteList = mutableListOf<FavoritePlace>()
        for (address in list) {
            deleteList.add(FavoritePlace(address))
        }
        mWeatherDB.favoritePlaceDao().delete(deleteList)
    }

    private fun getWeatherList(item: List<WeatherItem>?): List<ForecastData> {
        val linkedMap = LinkedHashMap<String, HashMap<String, String>>()
        item?.forEach { weather ->
            val dateTime = weather.fcstDate + weather.fcstTime
            val category = weather.category
            val value = weather.fcstValue
            if (linkedMap[dateTime] == null)
                linkedMap[dateTime] = HashMap()
            linkedMap[dateTime]?.set(category, value)
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
     * Formula : 13.12 + 0.6215 * t - 11.37 * v^0.16 + 0.3965 * v^0.16 * t
     *
     * @param t Temperature
     * @param w Wind Velocity
     */
    private fun getWindChillTemperature(t: String, w: String): String {
        return try {
            val tpr = t.toDouble()
            val v = w.toDouble()
            (13.12 + 0.6215 * tpr - 11.37 * v.pow(0.16) + 0.3965 * v.pow(0.16) * tpr).roundToInt()
                .toString()
        } catch (e: NumberFormatException) {
            ""
        } catch (e: IllegalArgumentException) {
            ""
        }
    }
}