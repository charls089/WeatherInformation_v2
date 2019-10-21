package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "WeeklyLand",
    primaryKeys = ["prvnCode", "dateTime"],
    indices = [Index("prvnCode", "dateTime")]
)
data class WeeklyLand(
    val dateTime: Long,
    val prvnCode: String,
    val wfAm: String,
    val wfPm: String,
    val rnStAm: String,
    val rnStPm: String
)
