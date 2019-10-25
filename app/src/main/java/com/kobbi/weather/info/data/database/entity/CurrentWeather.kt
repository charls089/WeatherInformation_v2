package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "CurrentWeather",
    primaryKeys = ["gridX", "gridY", "dateTime"],
    indices = [Index("gridX", "gridY", "dateTime")]
)
data class CurrentWeather(
    val dateTime: Long,
    val gridX: Int,
    val gridY: Int,
    val t1h: String,
    val rn1: String,
    val sky: String,
    val uuu: String,
    val vvv: String,
    val reh: String,
    val pty: String,
    val lgt: String,
    val vec: String,
    val wsd: String,
    val wct: String
)
