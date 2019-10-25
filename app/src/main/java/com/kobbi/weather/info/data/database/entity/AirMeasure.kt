package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "AirMeasure",
    primaryKeys = ["sidoName", "cityName", "dateTime"],
    indices = [Index("sidoName", "cityName", "dateTime")]
)
data class AirMeasure(
    val dateTime: Long,
    val sidoName: String,
    val cityName: String,
    val so2: String,
    val co: String,
    val o3: String,
    val no2: String,
    val pm10: String,
    val pm25: String
)