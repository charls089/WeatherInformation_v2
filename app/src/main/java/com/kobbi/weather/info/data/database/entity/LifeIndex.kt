package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "LifeIndex",
    primaryKeys = ["areaCode", "dateTime", "baseTime", "codeNo"],
    indices = [Index("areaCode", "dateTime", "baseTime", "codeNo")]
)
data class LifeIndex(
    val dateTime: Long,
    val baseTime: Int,
    val areaCode: String,
    val codeNo: String,
    val value: Int
)