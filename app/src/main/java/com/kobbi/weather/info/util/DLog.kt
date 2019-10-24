package com.kobbi.weather.info.util

import android.content.Context
import android.util.Log
import com.kobbi.weather.info.BuildConfig
import java.io.File
import java.io.FileOutputStream

class DLog private constructor() {
    companion object {
        private const val TAG_PREFIX = "##_kbi_"
        private const val LOG_SUFFIX = "_log.txt"

        @JvmStatic
        fun i(tag: String = "default", message: String) {
            if (BuildConfig.DEBUG)
                Log.i(TAG_PREFIX + tag, message)
        }

        @JvmStatic
        fun d(tag: String = "default", message: String) {
            if (BuildConfig.DEBUG)
                Log.d(TAG_PREFIX + tag, message)
        }

        @JvmStatic
        fun d(clazz: Class<*>, message: String) {
            if (BuildConfig.DEBUG)
                Log.d(TAG_PREFIX + clazz.simpleName, message)
        }

        @JvmStatic
        fun e(tag: String = "default", message: String?) {
            Log.e(TAG_PREFIX + tag, message)
        }

        @JvmStatic
        fun e(clazz: Class<*>, message: String?) {
            Log.e(TAG_PREFIX + clazz.simpleName, message)
        }

        @JvmStatic
        fun writeLogFile(context: Context, tag: String = "default", message: String?) {
            if (BuildConfig.DEBUG)
                e(tag, message)
            val dir = File(context.getExternalFilesDir(null), "log").apply {
                if (!this.exists()) {
                    this.mkdirs()
                }
            }
            val fileName = "${Utils.getCurrentTime(time = System.currentTimeMillis())}$LOG_SUFFIX"
            val logFile = File(dir, fileName)
            FileOutputStream(logFile, true).use {
                if (logFile.length() > 0)
                    it.write("\r\n".toByteArray())
                it.write("[${Utils.getCurrentTime(Utils.VALUE_TIME_FORMAT)}] $message".toByteArray())
            }
        }
    }
}