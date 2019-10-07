package com.kobbi.weather.info.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kobbi.weather.info.data.database.entity.LifeIndexDay

@Dao
interface LifeIndexDayDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(varargs: LifeIndexDay)

    @Query("SELECT * FROM LifeIndexDay")
    fun loadAll(): LiveData<List<LifeIndexDay>>

    @Query("SELECT * FROM LifeIndexDay WHERE dateTime = :dateTime")
    fun load(dateTime: Long): List<LifeIndexDay>

    @Query("SELECT * FROM LifeIndexDay WHERE dateTime = :dateTime")
    fun loadLive(dateTime: Long): LiveData<List<LifeIndexDay>>

    @Query("SELECT * FROM LifeIndexDay WHERE dateTime = :dateTime AND areaCode = :areaCode")
    fun findData(dateTime: Long, areaCode: String): List<LifeIndexDay>
}