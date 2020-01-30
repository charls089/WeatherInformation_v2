package com.kobbi.weather.info.data.network.api

import com.kobbi.weather.info.BuildConfig
import com.kobbi.weather.info.data.network.response.AirResponse
import com.kobbi.weather.info.util.ApiConstants
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface AirMeasureApi {
    @GET(ApiConstants.API_DISTRICT_AVG_LIST+"?ServiceKey=" + BuildConfig.API_KEY)
    fun requestAirMeasure(@QueryMap params: @JvmSuppressWildcards Map<String, Any>) : Call<AirResponse>
}