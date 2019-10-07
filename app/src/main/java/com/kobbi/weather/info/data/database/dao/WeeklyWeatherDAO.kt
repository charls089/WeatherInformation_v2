package com.kobbi.weather.info.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kobbi.weather.info.data.database.entity.WeeklyLand
import com.kobbi.weather.info.data.database.entity.WeeklyTpr
import com.kobbi.weather.info.data.database.entity.WeeklyWeather

@Dao
interface WeeklyWeatherDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLand(weathers: List<WeeklyLand>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTpr(weathers: List<WeeklyTpr>)

    @Query("SELECT * FROM WeeklyWeather")
    fun loadAll(): List<WeeklyWeather>

    @Query("SELECT * FROM WeeklyWeather")
    fun loadAllLive(): LiveData<List<WeeklyWeather>>

    @Query("SELECT * FROM WeeklyWeather WHERE dateTime BETWEEN :startDate AND :endDate")
    fun load(startDate: Long, endDate: Long): List<WeeklyWeather>

    @Query("SELECT * FROM WeeklyWeather WHERE dateTime BETWEEN :startDate AND :endDate")
    fun loadLive(startDate: Long, endDate: Long): LiveData<List<WeeklyWeather>>

    @Query("SELECT * FROM WeeklyWeather WHERE (dateTime BETWEEN :startDate AND :endDate) AND cityCode = :cityCode")
    fun findData(startDate: Long, endDate: Long, cityCode: String): List<WeeklyWeather>
}