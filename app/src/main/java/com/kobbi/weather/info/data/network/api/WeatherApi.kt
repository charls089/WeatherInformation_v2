package com.kobbi.weather.info.data.network.api

import com.kobbi.weather.info.BuildConfig
import com.kobbi.weather.info.data.network.response.LifeResponse
import com.kobbi.weather.info.data.network.response.MiddleResponse
import com.kobbi.weather.info.data.network.response.NewsResponse
import com.kobbi.weather.info.data.network.response.WeatherResponse
import com.kobbi.weather.info.util.ApiConstants
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface WeatherApi {
    @GET(ApiConstants.API_WEATHER_SERVICE + "{apiUrl}?ServiceKey=" + BuildConfig.API_KEY)
    fun requestWeather(@Path("apiUrl") apiUrl: String, @QueryMap params: @JvmSuppressWildcards Map<String, Any>): Call<WeatherResponse>

    @GET(ApiConstants.API_MIDDLE_SERVICE + "{apiUrl}?ServiceKey=" + BuildConfig.API_KEY)
    fun requestMiddle(@Path("apiUrl") apiUrl: String, @QueryMap params: @JvmSuppressWildcards Map<String, Any>): Call<MiddleResponse>

    @GET(ApiConstants.API_LIFE_SERVICE + "{apiUrl}?ServiceKey=" + BuildConfig.API_KEY)
    fun requestLife(@Path("apiUrl") apiUrl: String, @QueryMap params: @JvmSuppressWildcards Map<String, Any>): Call<LifeResponse>

    @GET(ApiConstants.API_SPECIAL_REPORT_SERVICE + ApiConstants.API_SPECIAL_STATUS + "?ServiceKey=" + BuildConfig.API_KEY)
    fun requestNews(@QueryMap params: @JvmSuppressWildcards Map<String, Any>): Call<NewsResponse>
}