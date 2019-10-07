package com.kobbi.weather.info.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kobbi.weather.info.data.database.entity.LifeIndexHour

@Dao
interface LifeIndexHourDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(varargs: LifeIndexHour)

    @Query("SELECT * FROM LifeIndexHour")
    fun loadAll(): LiveData<List<LifeIndexHour>>

    @Query("SELECT * FROM LifeIndexHour WHERE dateTime = :dateTime")
    fun load(dateTime: Long): List<LifeIndexHour>

    @Query("SELECT * FROM LifeIndexHour WHERE dateTime = :dateTime")
    fun loadLive(dateTime: Long): LiveData<List<LifeIndexHour>>

    @Query("SELECT * FROM LifeIndexHour WHERE dateTime = :dateTime AND areaCode = :areaCode")
    fun findData(dateTime: Long, areaCode: String): List<LifeIndexHour>
}