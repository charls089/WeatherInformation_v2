package com.kobbi.weather.info.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kobbi.weather.info.data.database.entity.SpecialNews

@Dao
interface SpecialNewsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg news: SpecialNews)

    @Delete
    fun delete(vararg news: SpecialNews)

    @Query("SELECT * FROM SpecialNews")
    fun loadAll(): List<SpecialNews>

    @Query("SELECT * FROM SpecialNews")
    fun loadAllLive(): LiveData<List<SpecialNews>>

    @Query("SELECT * FROM SpecialNews WHERE tmFc = :time")
    fun load(time: Long): SpecialNews

    @Query("SELECT * FROM SpecialNews WHERE tmFc = :time")
    fun loadLive(time: Long): LiveData<SpecialNews>

    @Query("SELECT * FROM SpecialNews WHERE tmEf <= :time ORDER BY tmFc DESC LIMIT 1")
    fun loadLastData(time: Long) : SpecialNews

    @Query("SELECT * FROM SpecialNews WHERE tmEf <= :time ORDER BY tmFc DESC LIMIT 1")
    fun loadLastDataLive(time: Long) : LiveData<SpecialNews>
}