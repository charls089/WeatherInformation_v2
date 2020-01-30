package com.kobbi.weather.info.data.network.api

import com.kobbi.weather.info.BuildConfig
import com.kobbi.weather.info.data.network.response.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface WeatherApi {
    @GET("{serviceUrl}/{apiUrl}?serviceKey=" + BuildConfig.API_KEY)
    fun requestWeather(@Path("serviceUrl") serviceUrl: String, @Path("apiUrl") apiUrl: String, @QueryMap params: @JvmSuppressWildcards Map<String, Any>): Call<WeatherResponse>
}