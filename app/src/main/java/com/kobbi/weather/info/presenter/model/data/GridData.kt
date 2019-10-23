package com.kobbi.weather.info.presenter.model.data

import androidx.room.ColumnInfo

data class GridData(
    @ColumnInfo(name = "gridX")
    val x: Int,
    @ColumnInfo(name = "gridY")
    val y: Int
)