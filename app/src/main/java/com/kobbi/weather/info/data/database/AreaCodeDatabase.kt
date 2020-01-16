package com.kobbi.weather.info.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kobbi.weather.info.data.database.dao.ForecastAreaCodeDAO
import com.kobbi.weather.info.data.database.dao.LifeAreaCodeDAO
import com.kobbi.weather.info.data.database.entity.ForecastAreaCode
import com.kobbi.weather.info.data.database.entity.LifeAreaCode
import com.kobbi.weather.info.util.DLog
import com.kobbi.weather.info.util.SharedPrefHelper
import java.util.concurrent.Executors
import kotlin.concurrent.thread

@Database(
    entities = [ForecastAreaCode::class, LifeAreaCode::class],
    version = 2
)
abstract class AreaCodeDatabase : RoomDatabase() {
    abstract fun fcstAreaCodeDao(): ForecastAreaCodeDAO
    abstract fun lifeAreaCodeDao(): LifeAreaCodeDAO

    companion object {
        private const val TAG = "AreaCodeDatabase"
        private const val DB_NAME = "AreaCodeDatabase.db"

        private const val FCST_AREA_CODE_FILE_NAME = "FcstAreaCode.txt"
        private const val LIFE_AREA_CODE_FILE_NAME = "LifeListAreaCode.txt"

        @Volatile
        private var INSTANCE: AreaCodeDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): AreaCodeDatabase {
            return INSTANCE ?: synchronized(this) {
                DLog.d(tag = TAG, message = "getDatabase($context) - INSTANCE : $INSTANCE")
                val callback = object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        DLog.d(tag = TAG, message = "callback.onCreate()")
                    }
                }
                Room.databaseBuilder(context, AreaCodeDatabase::class.java, DB_NAME)
                    .addCallback(callback)
                    //modify zone info(19.01.06)
                    .addMigrations(object : Migration(1, 2) {
                        override fun migrate(database: SupportSQLiteDatabase) {
                            SharedPrefHelper.setBool(context, SharedPrefHelper.KEY_AREA_CODE_DATA_INITIALIZED, false)
                            initializeDB(context)
                        }
                    })
                    .build().also {
                        INSTANCE = it
                    }
            }
        }

        @JvmStatic
        fun initializeDB(context: Context) {
            val init =
                SharedPrefHelper.getBool(context, SharedPrefHelper.KEY_AREA_CODE_DATA_INITIALIZED)
            DLog.d(tag = TAG, message = "Initialized AreaCode DB -> $init")
            if (!init) {
                thread {
                    setDatabaseData(context, FCST_AREA_CODE_FILE_NAME)
                    setDatabaseData(context, LIFE_AREA_CODE_FILE_NAME)
                }
            }
        }

        private fun setDatabaseData(context: Context, fileName: String) {
            val list =
                getDataFromAsset(context, fileName) as? List<*>
            Executors.newSingleThreadScheduledExecutor().execute {
                val database = getDatabase(context)
                if (list != null) {
                    when (fileName) {
                        FCST_AREA_CODE_FILE_NAME -> {
                            database.fcstAreaCodeDao()
                                .insertAll(list.filterIsInstance<ForecastAreaCode>())
                        }
                        LIFE_AREA_CODE_FILE_NAME -> {
                            database.lifeAreaCodeDao()
                                .insertAll(list.filterIsInstance<LifeAreaCode>())
                        }
                    }
                }
                SharedPrefHelper.setBool(
                    context,
                    SharedPrefHelper.KEY_AREA_CODE_DATA_INITIALIZED,
                    true
                )
            }
        }

        private fun getDataFromAsset(context: Context, fileName: String): List<Any> {
            val list = mutableListOf<Any>()
            context.applicationContext.assets.open(fileName).use { ips ->
                ips.bufferedReader().readLines().run {
                    var count = 0
                    this.forEach {
                        count++
                        val data = it.split(",")
                        val code = when (fileName) {
                            FCST_AREA_CODE_FILE_NAME -> {
                                ForecastAreaCode(data[0], data[1], data[2])
                            }
                            LIFE_AREA_CODE_FILE_NAME -> {
                                LifeAreaCode(
                                    data[0],
                                    data[1],
                                    data[2],
                                    data[3],
                                    data[4],
                                    data[5].toInt(),
                                    data[6].toInt()
                                )
                            }
                            else -> {
                                Any()
                            }
                        }
                        list.add(code)
                    }
                }
            }
            DLog.d(tag = TAG, message = "getDataFromAsset() --> List Size : ${list.size}")
            return list
        }
    }
}