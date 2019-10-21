package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "WeeklyTpr",
    primaryKeys = ["cityCode", "dateTime"],
    indices = [Index("cityCode", "dateTime")]
)
data class WeeklyTpr(
    val dateTime: Long,
    val prvnCode: String,
    val cityCode: String,
    val taMin: String,
    val taMax: String
)
