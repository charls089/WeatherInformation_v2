package com.kobbi.weather.info.data.network.api

import com.kobbi.weather.info.BuildConfig
import com.kobbi.weather.info.data.network.response.JusoResponse
import com.kobbi.weather.info.util.ApiConstants
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface JusoApi {
    @GET(ApiConstants.API_EPOST_SERVICE + ApiConstants.API_EPOST_SERVICE + "{apiUrl}?ServiceKey=" + BuildConfig.API_KEY)
    fun requestJuso(@Path("apiUrl") apiUrl: String, @QueryMap params: @JvmSuppressWildcards Map<String, Any>): Call<JusoResponse>
}