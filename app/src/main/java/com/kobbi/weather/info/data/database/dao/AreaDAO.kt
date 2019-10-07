package com.kobbi.weather.info.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kobbi.weather.info.data.database.entity.Area

@Dao
interface AreaDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg areas: Area)

    @Delete
    fun delete(vararg areas: Area)

    @Query("SELECT * FROM ForecastArea")
    fun loadAll(): List<Area>

    @Query("SELECT * FROM ForecastArea")
    fun loadAllLive(): LiveData<List<Area>>


    @Query("SELECT address FROM ForecastArea")
    fun loadAddress(): LiveData<List<String>>

    @Query("SELECT * FROM ForecastArea WHERE isActive = 0")
    fun loadActiveArea(): List<Area>

    @Query("SELECT * FROM ForecastArea WHERE isActive = 0 ORDER BY ROWID DESC")
    fun loadActiveAreaLive(): LiveData<List<Area>>

    @Query("SELECT * FROM ForecastArea WHERE address = :address")
    fun findArea(address: String): Area?

    @Query("UPDATE ForecastArea SET isActive = :stateCode WHERE address = :address")
    fun updateCodeArea(address: String, stateCode: Int): Int
}