package com.kobbi.weather.info.presenter.model.data

data class WeatherData(
    val gridX: Int,
    val gridY: Int,
    val dateTime: Long,
    val tpr: String,
    val rn1: String,
    val sky: String,
    val reh: String,
    val pty: String,
    val pop: String,
    val wsd: String,
    val wct: String
)