package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "LifeIndexHour",
    primaryKeys = ["key"],
    indices = [Index("areaCode", "dateTime")]
)
data class LifeIndexHour(
    val key: String,
    val areaCode: String,
    val dateTime: Long,
    val codeNo: String,
    val baseTime: String,
    val h3: Int,
    val h6: Int,
    val h9: Int,
    val h12: Int,
    val h15: Int,
    val h18: Int,
    val h21: Int,
    val h24: Int
)