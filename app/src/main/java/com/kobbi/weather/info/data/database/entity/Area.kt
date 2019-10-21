package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ForecastArea",
    primaryKeys = ["address"],
    indices = [Index(value = ["address"], unique = true)]
)
data class Area(
    val address: String,
    val prvnCode: String,
    val cityCode: String,
    val areaCode: String,
    val gridX: Int,
    val gridY: Int,
    //0 -> Active, 1 -> Inactive
    val isActive: Int = 0
)