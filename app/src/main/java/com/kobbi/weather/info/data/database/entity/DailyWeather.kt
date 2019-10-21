package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "DailyWeather",
    primaryKeys = ["gridX", "gridY", "dateTime"],
    indices = [Index("gridX", "gridY", "dateTime")]
)
data class DailyWeather(
    val dateTime: Long,
    val gridX: Int,
    val gridY: Int,
    val pop: String,
    val t3h: String,
    val tmn: String,
    val tmx: String,
    val r06: String,
    val s06: String,
    val sky: String,
    val uuu: String,
    val vvv: String,
    val reh: String,
    val pty: String,
    val lgt: String,
    val wav: String,
    val vec: String,
    val wsd: String,
    val wct: String
)
