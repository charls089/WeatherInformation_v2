package com.kobbi.weather.info.data.network.api

import com.kobbi.weather.info.data.network.response.AirResponse
import com.kobbi.weather.info.util.ApiConstants
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface AirMeasureApi {
    @GET(ApiConstants.API_DISTRICT_AVG_LIST)
    fun requestAirMeasure(
        @Query("ServiceKey") apiKey: String,
        @QueryMap params: @JvmSuppressWildcards Map<String, Any>
    ): Call<AirResponse>
}