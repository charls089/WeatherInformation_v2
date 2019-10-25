package com.kobbi.weather.info.ui.adapter

import android.graphics.Color
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.postDelayed
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.kobbi.weather.info.R
import com.kobbi.weather.info.data.database.entity.*
import com.kobbi.weather.info.presenter.listener.ClickListener
import com.kobbi.weather.info.presenter.listener.LongClickListener
import com.kobbi.weather.info.presenter.model.data.MinMaxTpr
import com.kobbi.weather.info.presenter.model.data.WeeklyData
import com.kobbi.weather.info.presenter.model.type.AirCode
import com.kobbi.weather.info.presenter.model.type.LifeCode
import com.kobbi.weather.info.presenter.viewmodel.JusoViewModel
import com.kobbi.weather.info.presenter.viewmodel.PlaceViewModel
import com.kobbi.weather.info.ui.view.LifeItemLayout
import com.kobbi.weather.info.util.*
import kotlinx.android.synthetic.main.item_life_list.view.*
import java.util.*

object BindingAdapter {
    @BindingAdapter("setJusoText")
    @JvmStatic
    fun setJusoText(textView: TextView, resId: Int?) {
        resId?.let {
            textView.setText(resId)
        }
    }

    @BindingAdapter("address", "setPosition")
    @JvmStatic
    fun setAddress(textView: TextView, areas: List<Area>?, position: Int?) {
        val area = getAreaFromViewModel(areas, position)
        textView.text = area?.address
    }

    @BindingAdapter("setViewPager", "setItems", "onPageChangedListener")
    @JvmStatic
    fun setViewPager(
        view: ViewPager,
        fragmentManager: FragmentManager?,
        areas: List<Area>?,
        listener: ViewPager.OnPageChangeListener?
    ) {
        fragmentManager?.let {
            listener?.let {
                areas?.let {
                    view.adapter?.run {
                        if (this is ViewPagerAdapter) {
                            this.setItems(areas)
                        }
                    } ?: kotlin.run {
                        view.adapter = ViewPagerAdapter(fragmentManager, areas)
                        view.addOnPageChangeListener(listener)
                    }
                }
            }
        }
    }

    @BindingAdapter("place", "selectedPosition", "getVm")
    @JvmStatic
    fun setFavoritePlace(
        recyclerView: RecyclerView,
        items: List<String>?,
        selectedPositions: List<Int>?,
        placeVm: PlaceViewModel?
    ) {
        if (items != null && selectedPositions != null) {
            recyclerView.adapter?.run {
                if (this is PlaceAdapter) {
                    this.setItems(items, selectedPositions)
                }
            } ?: kotlin.run {
                PlaceAdapter(items, selectedPositions).apply {
                    recyclerView.adapter = this
                    this.setOnClickListener(object : ClickListener {
                        override fun onItemClick(position: Int, view: View) {
                            val isMulti = placeVm?.isMultiCheck?.value
                            if (isMulti == true)
                                placeVm.addDeleteItem(position)
                            else {
                                view.isClickable = false
                                view.isEnabled = false
                                Handler().postDelayed(1000) {
                                    view.isClickable = true
                                    view.isEnabled = true
                                }
                                placeVm?.selectItem(position)
                            }
                        }
                    })
                    this.setOnLongClickListener(object : LongClickListener {
                        override fun onItemLongClick(position: Int, view: View) {
                            val isMulti = placeVm?.isMultiCheck?.value
                            if (isMulti != true)
                                placeVm?.addDeleteItem(position)
                        }
                    })
                }
            }
        }
    }

    @BindingAdapter("setJuso", "setPosition")
    @JvmStatic
    fun setJusoList(view: RecyclerView, items: List<String>?, viewModel: JusoViewModel?) {
        items?.let {
            view.adapter?.run {
                if (this is JusoAdapter) {
                    this.setItems(items)
                }
            } ?: kotlin.run {
                view.adapter = JusoAdapter(items).apply {
                    this.setOnClickListener(object : ClickListener {
                        override fun onItemClick(position: Int, view: View) {
                            viewModel?.loadJusoList(position)
                        }
                    })
                }
            }
        }
    }

    @BindingAdapter("setDaily", "setArea", "setPosition")
    @JvmStatic
    fun setDailyList(
        view: RecyclerView,
        items: List<DailyWeather>?,
        areas: List<Area>?,
        position: Int?
    ) {
        items?.let {
            getAreaFromViewModel(areas, position)?.run {
                val list = items.filter {
                    it.gridX == gridX && it.gridY == gridY
                }
                view.adapter?.run {
                    if (this is DailyAdapter) {
                        this.setItems(list)
                    }
                } ?: kotlin.run {
                    DailyAdapter(list).apply {
                        view.adapter = this
                    }
                }
            }
        }
    }

    @BindingAdapter("setWeekly", "setWeekFromDaily", "setArea", "setPosition")
    @JvmStatic
    fun setWeeklyList(
        view: RecyclerView,
        items: List<WeeklyWeather>?,
        daily: List<DailyWeather>?,
        areas: List<Area>?,
        position: Int?
    ) {
        items?.let {
            getAreaFromViewModel(areas, position)?.run {
                val frontList = sortDailyToWeekly(daily, this)
                val list = items.filter {
                    it.cityCode == cityCode
                }
                val weeklyList = mutableListOf<WeeklyWeather>()
                weeklyList.addAll(frontList)
                weeklyList.addAll(list)
                view.adapter?.run {
                    if (this is WeeklyAdapter) {
                        this.setItems(weeklyList)
                    }
                } ?: kotlin.run {
                    WeeklyAdapter(weeklyList).apply {
                        view.adapter = this
                    }
                }
            }
        }
    }

    @BindingAdapter("setSpecial", "setArea", "setPosition")
    @JvmStatic
    fun setSpecial(
        view: TextView,
        news: SpecialNews?,
        areas: List<Area>?,
        position: Int?
    ) {
        val area = getAreaFromViewModel(areas, position)
        area?.let {
            news?.let {
                val cityName = LocationUtils.getCityCode(area.address)
                val newsInfoList = news.t6.split("\r\n")
                val result = mutableListOf<String>()
                for (newsInfo in newsInfoList) {
                    if (newsInfo.contains(cityName)) {
                        val removeSpaceData = newsInfo.replace(" ", "")
                        val startIdx = removeSpaceData.indexOf("o")
                        val endIdx = removeSpaceData.indexOf(":")
                        if (endIdx > -1)
                            result.add(removeSpaceData.substring(startIdx + 1, endIdx))
                    }
                }
                DLog.d(
                    "BindingAdapter", "setSpecial() -> result : $result / cityName : $cityName"
                )
                view.run {
                    visibility = if (result.isEmpty()) View.GONE else View.VISIBLE
                    val sb = StringBuilder().apply {
                        for (i in result.indices) {
                            append(result[i])
                            if (i < result.size - 1)
                                append(", ")
                        }
                    }
                    text = String.format(
                        view.context.getString(R.string.holder_special_value), sb.toString()
                    )
                }
            }
        }
    }

    @BindingAdapter("setBackground")
    @JvmStatic
    fun setBackground(view: View, isSelected: Boolean?) {
        isSelected?.let {
            view.setBackgroundResource(
                if (isSelected)
                    R.drawable.border_selected
                else
                    R.drawable.border_not_selected
            )
        }
    }

    @BindingAdapter("setTpr", "setArea", "setPosition")
    @JvmStatic
    fun setTpr(
        view: TextView,
        items: List<CurrentWeather>?,
        areas: List<Area>?,
        position: Int?
    ) {
        val currentWeather = getCurrentWeather(areas, position, items)
        currentWeather?.run {
            view.text = t1h
        }
    }

    @BindingAdapter("setReh", "setArea", "setPosition")
    @JvmStatic
    fun setReh(
        view: TextView,
        items: List<CurrentWeather>?,
        areas: List<Area>?,
        position: Int?
    ) {
        val currentWeather = getCurrentWeather(areas, position, items)
        currentWeather?.run {
            view.text = reh
        }
    }

    @BindingAdapter("setWct", "setArea", "setPosition")
    @JvmStatic
    fun setWct(
        view: TextView,
        items: List<CurrentWeather>?,
        areas: List<Area>?,
        position: Int?
    ) {
        val currentWeather = getCurrentWeather(areas, position, items)
        currentWeather?.run {
            view.text = wct
        }
    }

    @BindingAdapter("setSky", "setArea", "setPosition")
    @JvmStatic
    fun setSky(
        view: ImageView,
        items: List<CurrentWeather>?,
        areas: List<Area>?,
        position: Int?
    ) {
        val currentWeather = getCurrentWeather(areas, position, items)
        currentWeather?.run {
            val dayOrNight = WeatherUtils.getDayOrNight(dateTime)
            val rainType = WeatherUtils.getRainFallType(pty)
            val skyState = WeatherUtils.getSkyCloudCover(sky)
            view.setImageResource(
                if (rainType == "없음")
                    WeatherUtils.getSkyResId(skyState, dayOrNight)
                else
                    WeatherUtils.getSkyResId(rainType, dayOrNight)
            )
        }
    }

    @BindingAdapter("setDateTime", "setPty", "setSky")
    @JvmStatic
    fun setSky(view: ImageView, dateTime: Long?, pty: String?, sky: String?) {
        view.setImageResource(WeatherUtils.getSkyIcon(dateTime, pty, sky))
    }

    @BindingAdapter("setSky", "setDayOrNight")
    @JvmStatic
    fun setWeeklyCloud(view: ImageView, sky: String?, dayOrNight: Int) {
        sky?.let {
            view.setImageResource(WeatherUtils.getSkyResId(sky, dayOrNight))
        }
    }

    @BindingAdapter("setRainGauge", "setArea", "setPosition")
    @JvmStatic
    fun setRainGauge(
        view: TextView,
        items: List<CurrentWeather>?,
        areas: List<Area>?,
        position: Int?
    ) {
        val currentWeather = getCurrentWeather(areas, position, items)
        currentWeather?.run {
            view.text = WeatherUtils.getRainGauge(rn1)
        }
    }

    @BindingAdapter("setMinMax", "setArea", "setPosition")
    @JvmStatic
    fun setMinMax(
        view: TextView,
        items: List<MinMaxTpr>?,
        areas: List<Area>?,
        position: Int?
    ) {
        val area = getAreaFromViewModel(areas, position)
        area?.run {
            items?.filter {
                it.gridX == gridX && it.gridY == gridY
            }?.let {
                if (it.size == 2) {
                    val tpr = when (view.id) {
                        R.id.tv_forecast_now_tpr_min_info -> it[0].tmn
                        R.id.tv_forecast_now_tpr_max_info -> it[1].tmx
                        else -> null
                    }
                    view.text = tpr
                }
            }
        }
    }

    @BindingAdapter("setDate", "setType")
    @JvmStatic
    fun setDate(textView: TextView, dateTime: Long?, type: Int = 0) {
        dateTime?.let {
            val time =
                if (type == 1) Utils.convertStringToDate(date = it.toString())?.time else dateTime
            time?.let {
                val date = Utils.convertDateTime(time = time, type = type)
                textView.text = date
            }
        }
    }

    @BindingAdapter("setTime")
    @JvmStatic
    fun setTime(textView: TextView, dateTime: Long?) {
        dateTime?.let {
            textView.text = getTodayOrTomorrow(dateTime)
        }
    }

    /**
     * 06시 발표 <Case 1 : 오늘, 내일 모레>, <Case2 : 3시간 간격으로 66시간 이후까지>
     * 09~12 : h3    >> idx : 0
     * 12~15 : h6    >> idx : 1
     * 15~18 : h9    >> idx : 2
     * 18~21 : h12   >> idx : 3
     * 21~24 : h15   >> idx : 4
     * 00~03 : h18   >> idx : 5
     * 03~06 : h21   >> idx : 6
     * 06~09 : h24   >> idx : 7
     */
    @BindingAdapter("createLifeDay", "createLifeHour", "setArea", "setPosition")
    @JvmStatic
    fun createLife(
        view: LinearLayout,
        lifeDay: List<LifeIndexDay>?,
        lifeHour: List<LifeIndexHour>?,
        areas: List<Area>?,
        position: Int?
    ) {
        view.removeAllViews()
        getAreaFromViewModel(areas, position)?.run {
            val filterDay = lifeDay?.filter {
                it.areaCode == areaCode
            }?.sortedBy {
                it.codeNo
            }
            val filterHour = lifeHour?.filter {
                it.areaCode == areaCode
            }?.sortedBy {
                it.codeNo
            }
            filterDay?.let {
                for (idxDay in filterDay) {
                    val codeNo = idxDay.codeNo
                    val value = idxDay.value
                    val codeName = LifeCode.findLifeCode(codeNo)?.codeName
                    val level = LifeCode.getLifeLevel(codeNo, value)
                    view.context?.let { context ->
                        val itemView = LifeItemLayout(context)
                        with(itemView) {
                            tv_life_title.text = codeName
                            level?.let {
                                tv_life_data.run {
                                    text =
                                        context.getString(R.string.holder_level_value, level, value)
                                    setTextColor(if (level == "주의") Color.BLACK else Color.WHITE)
                                    setBackgroundResource(getLevelColor(level))
                                }
                            }
                        }
                        view.addView(itemView)
                    }
                }
            }
            filterHour?.let {
                for (idxLife in filterHour) {
                    val codeNo = idxLife.codeNo
                    val codeName = LifeCode.findLifeCode(codeNo)?.codeName
                    val currentHour =
                        Utils.getCurrentTime("HH", System.currentTimeMillis()).toIntOrNull()
                    val value = currentHour?.let {
                        when (currentHour / 3) {
                            0 -> idxLife.h18
                            1 -> idxLife.h21
                            2 -> idxLife.h24
                            3 -> idxLife.h3
                            4 -> idxLife.h6
                            5 -> idxLife.h9
                            6 -> idxLife.h12
                            7 -> idxLife.h15
                            else -> idxLife.h3
                        }
                    } ?: idxLife.h3
                    val level = LifeCode.getLifeLevel(codeNo, value)
                    view.context?.let { context ->
                        val itemView = LifeItemLayout(context)
                        with(itemView) {
                            tv_life_title.text = codeName
                            level?.let {
                                tv_life_data.run {
                                    text =
                                        context.getString(R.string.holder_level_value, level, value)
                                    setTextColor(if (level == "주의") Color.BLACK else Color.WHITE)
                                    setBackgroundResource(getLevelColor(level))
                                }
                            }
                        }
                        view.addView(itemView)
                    }
                }
            }
        }
    }

    @BindingAdapter("setAirCode", "setAirLevel", "setArea", "setPosition")
    @JvmStatic
    fun setAirLevel(
        view: TextView,
        code: AirCode,
        items: List<AirMeasure>?,
        areas: List<Area>?,
        position: Int?
    ) {
        getAreaFromViewModel(areas, position)?.run {
            val addressList = LocationUtils.splitAddressLine(address)
            if (addressList.size >= 2) {
                val airMeasure = items?.find {
                    it.sidoName == addressList[0] && it.cityName == addressList[1]
                }
                airMeasure?.let {
                    val value = when (code) {
                        AirCode.PM10 -> it.pm10
                        AirCode.PM25 -> it.pm25
                    }
                    val level = AirCode.getAirLevel(code.codeNo, value)
                    view.run {
                        text =
                            if (TextUtils.isEmpty(value)) context.getString(R.string.text_checking_data) else value
                        setBackgroundResource(getLevelColor(level))
                    }
                }
            }
        }
    }

    @BindingAdapter("setItemVisibility")
    @JvmStatic
    fun setItemVisibility(view: View, items: List<*>?) {
        items?.let {
            view.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    @BindingAdapter("setGuideVisibility")
    @JvmStatic
    fun setGuideVisibility(view: View, items: List<*>?) {
        items?.let {
            view.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    @BindingAdapter("setVisibility", "setArea", "setPosition")
    @JvmStatic
    fun setVisibility(
        view: View,
        items: List<CurrentWeather>?,
        areas: List<Area>?,
        position: Int?
    ) {
        val currentWeather = getCurrentWeather(areas, position, items)
        val value = when (view.id) {
            R.id.ll_on_time_rain_gauge -> currentWeather?.rn1
            R.id.ll_yesterday_wc_temp -> currentWeather?.wct
            else -> null
        }
        view.visibility =
            if (value.isNullOrEmpty() || value == "없음" || value == "0") View.GONE else View.VISIBLE
    }

    @BindingAdapter("createDotPanel", "selectDot")
    @JvmStatic
    fun createDotPanel(view: LinearLayout, count: Int, position: Int) {
        view.removeAllViews()
        view.context?.let {
            val ivDotList = mutableListOf<ImageView>()
            for (i in 0 until count) {
                val iv = ImageView(it).apply {
                    setPadding(5, 5, 5, 5)
                }
                ivDotList.add(iv)
                view.addView(iv)
            }
            val isAgree =
                SharedPrefHelper.getBool(it, SharedPrefHelper.KEY_AGREE_TO_USE_LOCATION)
            selectDot(ivDotList, position, isAgree)
        }
    }

    @JvmStatic
    private fun selectDot(ivDotList: List<ImageView>, position: Int, isAgree: Boolean) {
        for (i in ivDotList.indices) {
            val resId = if (position == i) {// baseLine
                if (isAgree && i == 0)
                    R.drawable.baseline_location_on_white_18
                else
                    R.drawable.baseline_radio_button_checked_white_18
            } else { // outLine
                if (isAgree && i == 0)
                    R.drawable.outline_location_on_white_18
                else
                    R.drawable.baseline_radio_button_unchecked_white_18
            }
            ivDotList[i].setImageResource(resId)
        }
    }

    @JvmStatic
    private fun getLevelColor(value: String?): Int {
        return when (value) {
            "좋음", "낮음" -> R.drawable.border_data_blue
            "보통", "관심" -> R.drawable.border_data_green
            "주의" -> R.drawable.border_data_yellow
            "나쁨", "경고", "높음" -> R.drawable.border_data_orange
            "매우나쁨", "매우높음", "위험", "매우위험" -> R.drawable.border_data_red
            else -> R.drawable.border_data_unknown
        }
    }

    @JvmStatic
    private fun getTodayOrTomorrow(dateTime: Long): String {
        fun convertDateToCalendar(date: Date = Date(), amount: Int = 0): Pair<Int, Int> {
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.DATE, amount)
            return Pair(cal.get(Calendar.YEAR), cal.get(Calendar.DAY_OF_YEAR))
        }

        val timeStr = dateTime.toString()
        val forecastDate = Utils.convertStringToDate(date = timeStr.substring(0..7))!!
        val calForecast = convertDateToCalendar(forecastDate)
        val tomorrow = convertDateToCalendar(amount = 1)
        val dayAfterTomorrow = convertDateToCalendar(amount = 2)
        val date = when (calForecast) {
            tomorrow -> "내일"
            dayAfterTomorrow -> "모레"
            else -> "오늘"
        }
        val hour = timeStr.substring(8..9)
        return "$date ${hour}시"
    }

    @JvmStatic
    private fun sortDailyToWeekly(daily: List<DailyWeather>?, area: Area): List<WeeklyWeather> {
        val filters = daily?.filter {
            it.gridX == area.gridX && it.gridY == area.gridY
        }
        val list = mutableListOf<WeeklyData>()
        filters?.forEach {
            val date = it.dateTime.toString().substring(0..7)
            val rainType = WeatherUtils.getRainFallType(it.pty)
            val wf = if (rainType == "없음") WeatherUtils.getSkyCloudCover(it.sky) else rainType
            list.add(
                WeeklyData(
                    date.toLong(),
                    it.t3h,
                    wf,
                    it.pop
                )
            )
        }
        val result = mutableListOf<WeeklyWeather>()
        if (list.size == 4) {
            val firstDayAm = list[0]
            val firstDayPm = list[1]
            val secondDayAm = list[2]
            val secondDayPm = list[3]
            result.add(
                WeeklyWeather(
                    firstDayAm.dateTime, "", "",
                    firstDayAm.tpr, firstDayPm.tpr,
                    firstDayAm.wf, firstDayPm.wf,
                    firstDayAm.rnSt, firstDayPm.rnSt
                )
            )
            result.add(
                WeeklyWeather(
                    secondDayAm.dateTime, "", "",
                    secondDayAm.tpr, secondDayPm.tpr,
                    secondDayAm.wf, secondDayPm.wf,
                    secondDayAm.rnSt, secondDayPm.rnSt
                )
            )
        }
        return result
    }

    @JvmStatic
    private fun getAreaFromViewModel(areas: List<Area>?, position: Int?): Area? {
        return position?.run {
            areas?.get(position)
        }
    }

    private fun getCurrentWeather(
        areas: List<Area>?,
        position: Int?,
        list: List<CurrentWeather>?
    ): CurrentWeather? {
        return getAreaFromViewModel(areas, position)?.run {
            list?.find {
                it.gridX == gridX && it.gridY == gridY
            }
        }
    }
}