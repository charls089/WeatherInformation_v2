package com.kobbi.weather.info.util

import android.content.Context
import android.content.SharedPreferences

class SharedPrefHelper private constructor() {
    companion object {
        private const val KEY_PREFERENCE = "_Preference"

        const val KEY_AREA_CODE_DATA_INITIALIZED = "area_code_db_init"
        const val KEY_AGREE_TO_USE_LOCATION = "agree_to_use_location"
        const val KEY_AGREE_TO_USE_NOTIFICATION = "agree_to_use_notify"

        const val KEY_APP_WIDGET_ID = "app_widget_id"

        @JvmStatic
        fun setBool(context: Context, key: String, value: Boolean) {
            getPreference(context).edit().run {
                putBoolean(key, value)
                apply()
            }
        }

        @JvmStatic
        fun getBool(context: Context, key: String, defValue: Boolean = false): Boolean {
            return getPreference(context).getBoolean(key, defValue)
        }

        @JvmStatic
        fun setInt(context: Context, key: String, value: Int) {
            getPreference(context).edit().run {
                putInt(key, value)
                apply()
            }
        }

        @JvmStatic
        fun getInt(context: Context, key: String, defValue: Int = Int.MIN_VALUE): Int {
            return getPreference(context).getInt(key, defValue)
        }

        @JvmStatic
        fun setLong(context: Context, key: String, value: Long) {
            getPreference(context).edit().run {
                putLong(key, value)
                apply()
            }
        }

        @JvmStatic
        fun getLong(context: Context, key: String, defValue: Long = 0L): Long {
            return getPreference(context).getLong(key, defValue)
        }

        @JvmStatic
        fun registerPrefChangeListener(
            context: Context,
            listener: SharedPreferences.OnSharedPreferenceChangeListener
        ) {
            getPreference(context).registerOnSharedPreferenceChangeListener(listener)
        }

        @JvmStatic
        fun unregisterPrefChangeListener(
            context: Context,
            listener: SharedPreferences.OnSharedPreferenceChangeListener
        ) {
            getPreference(context).unregisterOnSharedPreferenceChangeListener(listener)
        }

        private fun getPreference(context: Context): SharedPreferences {
            return context.applicationContext.getSharedPreferences(
                "${context.packageName}${KEY_PREFERENCE}", Context.MODE_PRIVATE
            )
        }
    }
}