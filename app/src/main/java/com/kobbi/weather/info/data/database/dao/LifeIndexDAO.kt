package com.kobbi.weather.info.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kobbi.weather.info.data.database.entity.LifeIndex

@Dao
interface LifeIndexDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(varargs: LifeIndex)

    @Query("SELECT * FROM LifeIndex")
    fun loadAll(): LiveData<List<LifeIndex>>

    @Query("SELECT * FROM LifeIndex WHERE dateTime = :dateTime AND (baseTime = :baseTime OR baseTime = 24)")
    fun load(dateTime: Long, baseTime: Int): List<LifeIndex>

    @Query("SELECT * FROM LifeIndex WHERE dateTime = :dateTime AND (baseTime = :baseTime OR baseTime = 24)")
    fun loadLive(dateTime: Long, baseTime: Int): LiveData<List<LifeIndex>>

    @Query("SELECT * FROM LifeIndex WHERE dateTime = :dateTime AND (baseTime = :baseTime OR baseTime = 24) AND areaCode = :areaCode")
    fun findData(dateTime: Long, baseTime: Int, areaCode: String): List<LifeIndex>
}