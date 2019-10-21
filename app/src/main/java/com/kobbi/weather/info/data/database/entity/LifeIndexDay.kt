package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "LifeIndexDay",
    primaryKeys = ["areaCode", "dateTime", "codeNo"],
    indices = [Index("areaCode", "dateTime", "codeNo")]
)
data class LifeIndexDay(
    val dateTime: Long,
    val areaCode: String,
    val codeNo: String,
    val value: Int
)