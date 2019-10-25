package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity

@Entity(
    tableName = "FavoritePlace",
    primaryKeys = ["address"]
)
data class FavoritePlace(
    val address: String
)