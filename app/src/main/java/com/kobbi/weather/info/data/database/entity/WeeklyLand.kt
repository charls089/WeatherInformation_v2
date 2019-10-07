package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "WeeklyLand",
    primaryKeys = ["key"],
    indices = [Index("prvnCode", "dateTime")]
)
data class WeeklyLand(
    val key: String,
    val prvnCode: String,
    val dateTime: Long,
    val wfAm: String,
    val wfPm: String,
    val rnStAm: String,
    val rnStPm: String
)
