package com.kobbi.weather.info.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.kobbi.weather.info.presenter.model.data.WeatherInfo

/**
 * SELECT area.address as address, today.dateTime as today_dateTime, today.t1h as today_tpr, today.rn1 as today_rn,
 * today.pty as today_pty, today.sky as today_sky, today.wct as today_wct, minTpr.tmn as tmn, maxTpr.tmx as tmx
 * from CurrentWeather as today
 * inner join DailyWeather as maxTpr on maxTpr.gridX = today.gridX AND maxTpr.gridY = today.gridY AND maxTpr.dateTime = :today+1500
 * inner join DailyWeather as minTpr on minTpr.gridX = today.gridX AND minTpr.gridY = today.gridY AND minTpr.dateTime = :today+0600
 * inner join ForecastArea as area on area.gridX = today.gridX AND area.gridY = today.gridY
 * where today.gridX = :x AND today.gridY = :y AND today.dateTime = :time AND area.address = :address
 * */
@Dao
interface WeatherInfoDAO {
    @Query(
        "SELECT area.address as address, today.dateTime as today_dateTime, today.t1h as today_tpr, " +
                "today.rn1 as today_rn, today.pty as today_pty, today.sky as today_sky, today.wct as today_wct, " +
                "minTpr.tmn as tmn, maxTpr.tmx as tmx\n" +
                "from CurrentWeather as today\n" +
                "inner join DailyWeather as maxTpr on\n" +
                "maxTpr.gridX = today.gridX AND maxTpr.gridY = today.gridY AND maxTpr.dateTime = :today+1500\n" +
                "inner join DailyWeather as minTpr on\n" +
                "minTpr.gridX = today.gridX AND minTpr.gridY = today.gridY AND minTpr.dateTime = :today+0600\n" +
                "inner join ForecastArea as area on\n" +
                "area.gridX = today.gridX AND area.gridY = today.gridY \n" +
                "where today.gridX = :x AND today.gridY = :y AND today.dateTime = :time AND area.address = :address"
    )
    fun loadWeatherInfo(address: String, today: Long, time: Long, x: Int, y: Int): WeatherInfo?
}