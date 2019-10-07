package com.kobbi.weather.info.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kobbi.weather.info.data.database.entity.AirMeasure

@Dao
interface AirMeasureDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(airMeasures: List<AirMeasure>)

    @Delete
    fun delete(vararg airMeasures: AirMeasure)

    @Query("SELECT * FROM AirMeasure")
    fun loadAll(): LiveData<List<AirMeasure>>
    @Query("SELECT * FROM AirMeasure WHERE dateTime = (SELECT MAX(dateTime) FROM AirMeasure)")
    fun loadLive(): LiveData<List<AirMeasure>>

    @Query("SELECT * FROM AirMeasure WHERE dateTime = :dateTime")
    fun load(dateTime: Long): List<AirMeasure>

    @Query("SELECT * FROM AirMeasure WHERE sidoName = :sidoName AND cityName = :cityName AND dateTime = (SELECT MAX(dateTime) FROM AirMeasure)")
    fun findData(sidoName:String, cityName:String) : AirMeasure
}