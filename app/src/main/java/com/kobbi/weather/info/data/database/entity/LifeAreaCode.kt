package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "LifeAreaCode",
    primaryKeys = ["areaNo"],
    indices = [Index(value = ["prvnName", "cityName", "guGunName", "dongName"])]
)
class LifeAreaCode(
    val areaNo: String,
    val prvnName: String,
    val cityName: String,
    val guGunName: String,
    val dongName: String
)