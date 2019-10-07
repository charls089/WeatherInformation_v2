package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "LifeIndexDay",
    primaryKeys = ["key"],
    indices = [Index("areaCode", "dateTime")]
)
data class LifeIndexDay(
    val key: String,
    val areaCode: String,
    val dateTime: Long,
    val codeNo: String,
    val value: Int
)