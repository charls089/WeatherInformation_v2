package com.kobbi.weather.info.presenter.model.data

data class ForecastData(
    val dateTime: String,
    val value: Map<String, String>
) {
    override fun toString(): String {
        return "fcstDateTime : $dateTime / value : $value"
    }
}