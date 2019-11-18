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

    @Query("SELECT * FROM AirMeasure WHERE dateTime = (SELECT MAX(dateTime) FROM AirMeasure)")
    fun load(): List<AirMeasure>

    @Query("SELECT * FROM AirMeasure WHERE sidoName = :sidoName AND dateTime = :dateTime")
    fun findData(sidoName: String, dateTime:Long): List<AirMeasure>
}