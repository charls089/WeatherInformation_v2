package com.kobbi.weather.info.presenter.model.data

import androidx.room.ColumnInfo

data class WeatherInfo(
    @ColumnInfo(name = "address")
    val address: String,
    @ColumnInfo(name = "today_dateTime")
    val dateTime: Long,
    @ColumnInfo(name = "today_tpr")
    val tpr: String,
    @ColumnInfo(name = "today_rn")
    val rn: String,
    @ColumnInfo(name = "today_pty")
    val pty: String,
    @ColumnInfo(name = "today_sky")
    val sky: String,
    @ColumnInfo(name = "today_wct")
    val wct: String,
    @ColumnInfo(name = "tmn")
    val tmn: String,
    @ColumnInfo(name = "tmx")
    val tmx: String,
    var yesterdayTpr: String? = null,
    var yesterdayWct: String? = null,
    var pm10: String? = null,
    var pm25: String? = null
)