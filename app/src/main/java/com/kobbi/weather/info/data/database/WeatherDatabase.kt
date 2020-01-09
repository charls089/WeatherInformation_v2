package com.kobbi.weather.info.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kobbi.weather.info.data.database.dao.*
import com.kobbi.weather.info.data.database.entity.*
import com.kobbi.weather.info.util.DLog

@Database(
    entities = [
        Area::class, FavoritePlace::class, CurrentWeather::class,
        DailyWeather::class, WeeklyTpr::class, WeeklyLand::class,
        AirMeasure::class, LifeIndex::class, SpecialNews::class
    ],
    views = [WeeklyWeather::class],
    version = 1
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun areaDao(): AreaDAO
    abstract fun favoritePlaceDao(): FavoritePlaceDAO
    abstract fun currentWeatherDao(): CurrentWeatherDAO
    abstract fun dailyWeatherDao(): DailyWeatherDAO
    abstract fun weeklyWeatherDao(): WeeklyWeatherDAO
    abstract fun airMeasureDao(): AirMeasureDAO
    abstract fun lifeIndexDao(): LifeIndexDAO
    abstract fun specialNewsDao(): SpecialNewsDAO
    abstract fun weatherInfoDao(): WeatherInfoDAO

    companion object {
        private const val TAG = "WeatherDatabase"
        private const val DB_NAME = "WeatherDatabase.db"

        private val mCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                DLog.d(tag = TAG, message = "WeatherDatabase.onCreate()")
            }
        }

        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                DLog.d(tag = TAG, message = "getDatabase($context) - INSTANCE : $INSTANCE")
                Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(mCallback).build().also {
                        INSTANCE = it
                    }
            }
        }
    }
}
