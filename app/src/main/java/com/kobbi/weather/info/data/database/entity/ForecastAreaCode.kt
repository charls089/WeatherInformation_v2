package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "ForecastAreaCode",
    primaryKeys = ["cityCode"],
    indices = [Index(value = ["areaName"])])
data class ForecastAreaCode(
    val areaName: String,
    val prvnCode: String,
    val cityCode: String
)