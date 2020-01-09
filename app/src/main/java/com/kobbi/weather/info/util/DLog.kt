package com.kobbi.weather.info.util

import android.content.Context
import android.util.Log
import com.kobbi.weather.info.BuildConfig
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.lang.Exception

class DLog private constructor() {
    companion object {
        private const val TAG_PREFIX = "kobbi_"
        private const val LOG_SUFFIX = "_log.txt"

        @JvmOverloads
        @JvmStatic
        fun v(context: Context? = null, tag: String = "default", message: String) {
            if (BuildConfig.DEBUG) {
                Log.v(TAG_PREFIX + tag, message)
            }
            if (context != null)
                writeLogFile(context, tag, message)
        }

        @JvmOverloads
        @JvmStatic
        fun i(context: Context? = null, tag: String = "default", message: String) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG_PREFIX + tag, message)
            }
            if (context != null)
                writeLogFile(context, tag, message)
        }

        @JvmOverloads
        @JvmStatic
        fun d(context: Context? = null, tag: String = "default", message: String) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG_PREFIX + tag, message)
            }
            if (context != null)
                writeLogFile(context, tag, message)
        }

        @JvmOverloads
        @JvmStatic
        fun e(context: Context? = null, tag: String = "default", message: String) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG_PREFIX + tag, message)
            }
            if (context != null)
                writeLogFile(context, tag, message)
        }

        private fun writeLogFile(context: Context, tag: String, message: String?) {
            val dir = File(context.getExternalFilesDir(null), "log").apply {
                if (!this.exists()) {
                    this.mkdirs()
                }
            }
            val fileName =
                "${Utils.getCurrentTime(time = System.currentTimeMillis())}$LOG_SUFFIX"
            val logFile = File(dir, fileName)
            try {
                FileOutputStream(logFile, true).use {
                    if (logFile.length() > 0)
                        it.write("\r\n".toByteArray())
                    it.write("[${Utils.getCurrentTime(Utils.VALUE_TIME_FORMAT)}][$tag] $message".toByteArray())
                }
            } catch (e: FileNotFoundException) {
                //Nothing.
            }
        }
    }
}