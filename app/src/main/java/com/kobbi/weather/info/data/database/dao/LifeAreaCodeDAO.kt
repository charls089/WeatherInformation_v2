package com.kobbi.weather.info.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kobbi.weather.info.data.database.entity.LifeAreaCode

@Dao
interface LifeAreaCodeDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg code: LifeAreaCode)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<LifeAreaCode>)

    @Query("SELECT * FROM LifeAreaCode")
    fun loadAll(): List<LifeAreaCode>

    @Query("SELECT areaNo FROM LifeAreaCode WHERE prvnName = :prvnName AND cityName = :cityName AND guGunName = :guGunName AND dongName = :dongName")
    fun findCodeFromAddress(prvnName: String, cityName: String, guGunName: String, dongName: String): String?

    @Query("SELECT areaNo FROM LifeAreaCode WHERE prvnName = :prvnName AND cityName = :cityName AND guGunName = :guGunName")
    fun findCodeFromAddress(prvnName: String, cityName: String, guGunName: String): String?

    @Query("SELECT areaNo FROM LifeAreaCode WHERE prvnName = :prvnName AND cityName = :cityName")
    fun findCodeFromAddress(prvnName: String, cityName: String): String?

    @Query("SELECT areaNo FROM LifeAreaCode WHERE prvnName = :prvnName")
    fun findCodeFromAddress(prvnName: String): String?
}