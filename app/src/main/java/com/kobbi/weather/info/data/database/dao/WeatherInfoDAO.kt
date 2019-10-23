package com.kobbi.weather.info.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.kobbi.weather.info.presenter.model.data.WeatherInfo

@Dao
interface WeatherInfoDAO {
    @Query(
        "SELECT today.t1h as today_tpr, today.rn1 as today_rn, today.pty as today_pty, today.sky as today_sky, today.wct as today_wct, yesterday.wct as yesterday_wct , minTpr.tmn as tmn, maxTpr.tmx as tmx\n" +
                "from CurrentWeather as today\n" +
                "inner join CurrentWeather as yesterday on\n" +
                "yesterday.gridX = today.gridX AND yesterday.gridY = today.gridY AND yesterday.dateTime = :yesterday+:time\n" +
                "inner join DailyWeather as maxTpr on\n" +
                "maxTpr.gridX = today.gridX AND maxTpr.gridY = today.gridY AND maxTpr.dateTime = :today+1500\n" +
                "inner join DailyWeather as minTpr on\n" +
                "minTpr.gridX = today.gridX AND minTpr.gridY = today.gridY AND minTpr.dateTime = :today+0600\n" +
                "where today.gridX = :x AND today.gridY = :y AND today.dateTime = (:today + :time) "
    )
    fun getWeatherInfo(today: Long, yesterday: Long, time: Long, x: Int, y: Int): WeatherInfo?
}