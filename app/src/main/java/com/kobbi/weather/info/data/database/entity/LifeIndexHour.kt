package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "LifeIndexHour",
    primaryKeys = ["areaCode", "dateTime", "codeNo"],
    indices = [Index("areaCode", "dateTime", "codeNo")]
)
data class LifeIndexHour(
    val dateTime: Long,
    val areaCode: String,
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