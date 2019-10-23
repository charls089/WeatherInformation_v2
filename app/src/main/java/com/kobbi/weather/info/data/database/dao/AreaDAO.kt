package com.kobbi.weather.info.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kobbi.weather.info.data.database.entity.Area
import com.kobbi.weather.info.presenter.model.data.AreaCode
import com.kobbi.weather.info.presenter.model.data.GridData
import com.kobbi.weather.info.util.Constants

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
    fun loadAllAddress(): List<String>

    @Query("SELECT * FROM ForecastArea WHERE stateCode != ${Constants.STATE_CODE_INACTIVE} ORDER BY stateCode DESC")
    fun loadActiveArea(): List<Area>

    @Query("SELECT * FROM ForecastArea WHERE stateCode != ${Constants.STATE_CODE_INACTIVE} ORDER BY stateCode DESC")
    fun loadActiveAreaLive(): LiveData<List<Area>>

    @Query("SELECT * FROM ForecastArea WHERE stateCode = ${Constants.STATE_CODE_LOCATED}")
    fun loadLocatedArea(): Area?

    @Query("SELECT * FROM ForecastArea WHERE address = :address")
    fun findArea(address: String): Area?

    @Query("select DISTINCT gridX, gridY from ForecastArea")
    fun loadAllGridData() : List<GridData>

    @Query("select DISTINCT areaCode from ForecastArea")
    fun loadAllAreaNo() : List<String>

    @Query("select DISTINCT prvnCode, cityCode from ForecastArea")
    fun loadAllAreaCode() : List<AreaCode>

    @Query("UPDATE ForecastArea SET stateCode = :stateCode WHERE address = :address")
    fun updateCodeArea(address: String, stateCode: Int): Int
}