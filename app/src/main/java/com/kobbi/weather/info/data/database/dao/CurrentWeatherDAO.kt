package com.kobbi.weather.info.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kobbi.weather.info.data.database.entity.CurrentWeather

@Dao
interface CurrentWeatherDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg weathers: CurrentWeather)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<CurrentWeather>)

    @Delete
    fun delete(vararg weathers: CurrentWeather)

    @Query("SELECT * FROM CurrentWeather")
    fun loadAll(): LiveData<List<CurrentWeather>>

    @Query("SELECT * FROM CurrentWeather WHERE dateTime = :dateTime")
    fun load(dateTime: Long): List<CurrentWeather>

    @Query("SELECT * FROM CurrentWeather WHERE dateTime = :dateTime")
    fun loadLive(dateTime: Long): LiveData<List<CurrentWeather>>?

    @Query("SELECT * FROM CurrentWeather WHERE dateTime = :dateTime AND gridX = :x AND gridY = :y")
    fun findData(dateTime: Long, x: Int, y: Int) : CurrentWeather?
}