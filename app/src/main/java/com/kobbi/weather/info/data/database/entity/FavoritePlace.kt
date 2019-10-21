package com.kobbi.weather.info.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "FavoritePlace",
    primaryKeys = ["address"]
)
data class FavoritePlace(
    val address: String
)