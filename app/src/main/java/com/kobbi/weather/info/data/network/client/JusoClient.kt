package com.kobbi.weather.info.data.network.client

import com.kobbi.weather.info.data.network.api.JusoApi
import com.kobbi.weather.info.util.ApiConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JusoClient private constructor() {
    companion object {
        private var INSTANCE: JusoApi? = null

        fun getInstance(): JusoApi {
            return INSTANCE ?: synchronized(this) {
                getClient().also {
                    INSTANCE = it
                }
            }
        }

        private fun getClient(): JusoApi {
            val interceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            return Retrofit.Builder().apply {
                baseUrl(ApiConstants.API_EPOST_BASE_URL)
                addConverterFactory(GsonConverterFactory.create())
                client(client)
            }.build().create(JusoApi::class.java)
        }
    }
}