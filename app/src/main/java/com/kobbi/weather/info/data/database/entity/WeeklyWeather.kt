package com.kobbi.weather.info.data.database.entity

import androidx.room.DatabaseView

@DatabaseView(
    "SELECT  tpr.prvnCode, tpr.cityCode, tpr.dateTime, tpr.taMin, tpr.taMax, land.wfAm, land.wfPm, land.rnStAm, land.rnStPm FROM WeeklyTpr as tpr INNER JOIN WeeklyLand as land ON (land.prvnCode = tpr.prvnCode AND land.dateTime = tpr.dateTime)"
)
data class WeeklyWeather(
    val dateTime: Long,
    val prvnCode:String,
    val cityCode:String,
    val taMin: String,
    val taMax: String,
    val wfAm: String,
    val wfPm: String,
    val rnStAm: String,
    val rnStPm: String
)
