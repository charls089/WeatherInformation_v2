package com.kobbi.weather.info.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kobbi.weather.info.data.database.entity.FavoritePlace

@Dao
interface FavoritePlaceDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg places: FavoritePlace)

    @Delete
    fun delete(list:List<FavoritePlace>)

    @Query("SELECT * FROM FavoritePlace")
    fun loadAll(): List<FavoritePlace>

    @Query("SELECT address FROM FavoritePlace")
    fun loadAddress(): List<String>

    @Query("SELECT address FROM FavoritePlace")
    fun loadAddressLive(): LiveData<List<String>>
}