package com.kobbi.weather.info.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kobbi.weather.info.data.database.entity.DailyWeather
import com.kobbi.weather.info.presenter.model.data.MinMaxTpr

@Dao
interface DailyWeatherDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg weathers: DailyWeather)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<DailyWeather>)

    @Delete
    fun delete(vararg weathers: DailyWeather)

    @Query("SELECT * FROM DailyWeather")
    fun loadAll(): LiveData<List<DailyWeather>>

    @Query("SELECT * FROM DailyWeather WHERE dateTime BETWEEN :startDate AND :endDate")
    fun load(startDate: Long, endDate: Long): List<DailyWeather>

    @Query("SELECT * FROM DailyWeather WHERE dateTime BETWEEN :startDate AND :endDate")
    fun loadLive(startDate: Long, endDate: Long): LiveData<List<DailyWeather>>

    @Query("SELECT * FROM DailyWeather WHERE (dateTime BETWEEN :startDate AND :endDate) AND gridX = :x AND gridY = :y")
    fun findData(startDate: Long, endDate: Long, x: Int, y: Int): List<DailyWeather>

    @Query("SELECT gridX, gridY, tmn, tmx FROM DailyWeather WHERE (dateTime = (:today+0600) OR dateTime = (:today+1500))")
    fun findMinMaxTprLive(today: Long): LiveData<List<MinMaxTpr>>

    @Query("SELECT gridX, gridY, tmn, tmx FROM DailyWeather WHERE (dateTime = (:today+0600) OR dateTime = (:today+1500)) AND gridX = :x AND gridY = :y")
    fun findMinMaxTpr(today: Long, x: Int, y: Int): List<MinMaxTpr>

    @Query("SELECT * FROM DailyWeather WHERE (dateTime BETWEEN :startDate AND :endDate) AND (dateTime LIKE '%0600' OR dateTime LIKE '%1500') ORDER BY dateTime")
    fun findWeekWeather(startDate: Long, endDate: Long): List<DailyWeather>

    @Query("SELECT * FROM DailyWeather WHERE (dateTime BETWEEN :startDate AND :endDate) AND (dateTime LIKE '%0600' OR dateTime LIKE '%1500') ORDER BY dateTime")
    fun findWeekWeatherLive(startDate: Long, endDate: Long): LiveData<List<DailyWeather>>
}