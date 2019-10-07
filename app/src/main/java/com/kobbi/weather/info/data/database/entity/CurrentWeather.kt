package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "CurrentWeather",
    primaryKeys = ["key"],
    indices = [Index("gridX", "gridY", "dateTime")]
)
data class CurrentWeather(
    val key: String,
    val gridX: Int,
    val gridY: Int,
    val dateTime: Long,
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
