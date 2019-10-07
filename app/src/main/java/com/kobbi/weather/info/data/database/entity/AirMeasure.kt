package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "AirMeasure",
    primaryKeys = ["key"],
    indices = [Index("sidoName", "cityName", "dateTime")]
)
data class AirMeasure(
    val key: String,
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