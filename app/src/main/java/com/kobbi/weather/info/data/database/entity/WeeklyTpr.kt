package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "WeeklyTpr",
    primaryKeys = ["key"],
    indices = [Index("cityCode", "dateTime")]
)
data class WeeklyTpr(
    val key: String,
    val prvnCode: String,
    val cityCode: String,
    val dateTime: Long,
    val taMin: String,
    val taMax: String
)
