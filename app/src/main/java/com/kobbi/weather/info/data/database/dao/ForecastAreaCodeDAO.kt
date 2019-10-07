package com.kobbi.weather.info.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kobbi.weather.info.data.database.entity.ForecastAreaCode
import com.kobbi.weather.info.presenter.model.data.AreaCode

@Dao
interface ForecastAreaCodeDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg code: ForecastAreaCode)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(list: List<ForecastAreaCode>)

    @Query("SELECT * FROM ForecastAreaCode")
    fun loadAll() : List<ForecastAreaCode>

    @Query("SELECT * FROM ForecastAreaCode WHERE areaName = :areaName")
    fun loadCode(areaName: String) : ForecastAreaCode

    @Query("SELECT prvnCode, cityCode FROM ForecastAreaCode WHERE areaName = :areaName")
    fun findCode(areaName: String) : AreaCode?
}
