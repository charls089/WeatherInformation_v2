package com.kobbi.weather.info.presenter.repository

import android.content.Context
import com.kobbi.weather.info.data.database.entity.Area
import com.kobbi.weather.info.data.network.client.AirMeasureClient
import com.kobbi.weather.info.data.network.client.JusoClient
import com.kobbi.weather.info.data.network.client.WeatherClient
import com.kobbi.weather.info.data.network.response.*
import com.kobbi.weather.info.presenter.listener.OnCompleteListener
import com.kobbi.weather.info.presenter.model.type.Address
import com.kobbi.weather.info.presenter.model.data.AreaCode
import com.kobbi.weather.info.presenter.model.data.GridData
import com.kobbi.weather.info.presenter.model.type.OfferType
import com.kobbi.weather.info.util.ApiConstants
import com.kobbi.weather.info.util.DLog
import com.kobbi.weather.info.util.LocationUtils
import com.kobbi.weather.info.util.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.LinkedHashMap

class ApiRequestRepository private constructor() {
    companion object {
        private const val TAG = "ApiRequestRepository"

        @JvmStatic
        fun requestWeather(
            context: Context, apiUrl: String, gridData: GridData, isFirst: Boolean = false
        ) {
            val baseTime = OfferType.getBaseDateTime(
                when {
                    apiUrl == ApiConstants.API_FORECAST_TIME_DATA -> OfferType.CURRENT
                    isFirst -> OfferType.MINMAX
                    else -> OfferType.DAILY
                }
            )
            val params = LinkedHashMap<String, Any>().apply {
                put("base_date", baseTime.first)
                put("base_time", baseTime.second)
                put("nx", gridData.x)
                put("ny", gridData.y)
                put("numOfRows", 1000)
                put("_type", "json")
            }
            val client = WeatherClient.getInstance()
            client.requestWeather(apiUrl, params).enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    DLog.d(
                        TAG,
                        "requestWeather.onResponse() -> <$apiUrl>call : $call, response : $response"
                    )
                    val item = response.body()?.response?.body?.items?.item
                    DLog.writeLogFile(context, TAG, "requestWeather.item : $item")
                    WeatherRepository.getInstance(context).insertWeather(gridData, item)
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    DLog.writeLogFile(context, TAG, "onFailure() -> <$apiUrl>call : $call, t : $t")
                }
            })
        }

        @JvmStatic
        fun requestMiddle(context: Context, apiUrl: String, areaCode: AreaCode) {
            val id =
                if (apiUrl == ApiConstants.API_MIDDLE_TEMPERATURE) areaCode.cityCode else areaCode.prvnCode
            val tmFc = OfferType.getBaseDateTime(OfferType.WEEKLY)
            val params = LinkedHashMap<String, Any>().apply {
                put("regId", id)
                put("tmFc", tmFc.first + tmFc.second)
                put("_type", "json")
            }
            val client = WeatherClient.getInstance()
            client.requestMiddle(apiUrl, params).enqueue(object : Callback<MiddleResponse> {
                override fun onResponse(
                    call: Call<MiddleResponse>,
                    response: Response<MiddleResponse>
                ) {
                    DLog.writeLogFile(
                        context, TAG,
                        "requestMiddle.onResponse() -> <$apiUrl>call : $call, response : $response"
                    )
                    val item = response.body()?.response?.body?.items?.item
                    DLog.writeLogFile(context, TAG, "requestMiddle.item : $item")
                    WeatherRepository.getInstance(context).insertMiddle(areaCode, item)
                }

                override fun onFailure(call: Call<MiddleResponse>, t: Throwable) {
                    DLog.writeLogFile(context, TAG, "onFailure() -> <$apiUrl>call : $call, t : $t")
                }
            })
        }

        @JvmStatic
        fun requestLife(context: Context, areaNo: String) {
            val params = java.util.LinkedHashMap<String, Any>().apply {
                put("areaNo", areaNo)
                put("time", Utils.getCurrentTime() + "06")
                put("_type", "json")
            }
            val apiList = ApiConstants.LifeApi.checkRequestUrl()
            for (apiUrl in apiList) {
                val client = WeatherClient.getInstance()
                client.requestLife(apiUrl.url, params).enqueue(object : Callback<LifeResponse> {
                    override fun onResponse(
                        call: Call<LifeResponse>, response: Response<LifeResponse>
                    ) {
                        DLog.writeLogFile(
                            context, TAG,
                            "requestLife.onResponse() -> <$apiUrl>call : $call, response : $response"
                        )
                        val indexModel = response.body()?.response?.body?.indexModel
                        DLog.writeLogFile(context, TAG, "<$apiUrl>indexModel : $indexModel")
                        WeatherRepository.getInstance(context).insertLife(areaNo, indexModel)
                    }

                    override fun onFailure(call: Call<LifeResponse>, t: Throwable) {
                        DLog.writeLogFile(
                            context, TAG, "onFailure() -> <$apiUrl>call : $call, t : $t"
                        )
                    }
                })
            }
        }

        fun requestNews(context: Context) {
            val params = java.util.LinkedHashMap<String, Any>().apply {
                put("numOfRows", 100)
                put("_type", "json")
            }
            val client = WeatherClient.getInstance()
            client.requestNews(params).enqueue(object : Callback<NewsResponse> {
                override fun onResponse(
                    call: Call<NewsResponse>,
                    response: Response<NewsResponse>
                ) {
                    DLog.writeLogFile(
                        context, TAG,
                        "requestNews.onResponse() -> call : $call, response : $response"
                    )
                    val item = response.body()?.response?.body?.items?.item
                    DLog.writeLogFile(context, TAG, "requestNews.item : $item")
                    WeatherRepository.getInstance(context).insertSpecialNews(item)
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    DLog.writeLogFile(context, TAG, "onFailure() -> call : $call, t : $t")
                }
            })
        }

        @JvmStatic
        fun requestAirMeasure(context: Context, sidoName: String) {
            val addressCode = Address.getSidoCode(sidoName)
            addressCode?.let {
                val params = java.util.LinkedHashMap<String, Any>().apply {
                    put("numOfRows", 1000)
                    put("sidoName", addressCode.shortName)
                    put("searchCondition", "HOUR")
                    put("_returnType", "json")
                }
                val client = AirMeasureClient.getInstance()
                client.requestAirMeasure(params).enqueue(object : Callback<AirResponse> {
                    override fun onResponse(
                        call: Call<AirResponse>,
                        response: Response<AirResponse>
                    ) {
                        DLog.writeLogFile(
                            context, TAG,
                            "requestAirMeasure.onResponse() -> call : $call, response : $response"
                        )
                        val list = response.body()?.list
                        DLog.writeLogFile(context, TAG, "requestAirMeasure.list : $list")
                        WeatherRepository.getInstance(context)
                            .insertAirMeasure(addressCode.fullName, list)
                    }

                    override fun onFailure(call: Call<AirResponse>, t: Throwable) {
                        DLog.writeLogFile(
                            context, TAG, "onFailure() -> call : $call, t : $t"
                        )
                    }

                })
            }
        }

        fun requestJuso(apiUrl: String, code: List<String>, listener: OnCompleteListener) {
            val params = LinkedHashMap<String, Any>().apply {
                if (code.isNotEmpty()) {
                    val brtcCd = Address.getSidoCode(code[0])?.shortName ?: return
                    put("brtcCd", brtcCd)
                    if (code.size == 2 && brtcCd != "세종")
                        put("signguCd", code[1])
                }
                put("_type", "json")
            }
            val client = JusoClient.getInstance()
            client.requestJuso(apiUrl, params).enqueue(object : Callback<JusoResponse> {
                override fun onResponse(
                    call: Call<JusoResponse>,
                    response: Response<JusoResponse>
                ) {
                    DLog.d(TAG, "requestJuso.onResponse() -> call : $call, response : $response")
                    val items = response.body()?.response?.body
                    DLog.d(TAG, "requestJuso.items : $items")
                    if (items != null)
                        listener.onComplete(0, items)
                    else
                        listener.onComplete(1, Any())
                }

                override fun onFailure(call: Call<JusoResponse>, t: Throwable) {
                    val message = "onFailure() -> call : $call, t : $t"
                    listener.onComplete(2, message)
                }
            })
        }

        fun initBaseAreaData(context: Context, area: Area) {
            val isNewArea =
                WeatherRepository.getInstance(context).loadAreaFromAddress(area.address) == null
            DLog.d(TAG, "initBaseAreaData() --> isNewArea : $isNewArea")
            if (isNewArea) {
                val sidoName = LocationUtils.splitAddressLine(area.address)
                val areaCode = AreaCode(
                    area.prvnCode,
                    area.cityCode
                )
                val gridData =
                    GridData(area.gridX, area.gridY)
                val areaNo = area.areaCode
                requestWeather(context, ApiConstants.API_FORECAST_TIME_DATA, gridData)
                requestWeather(context, ApiConstants.API_FORECAST_SPACE_DATA, gridData, true)
                requestWeather(context, ApiConstants.API_FORECAST_SPACE_DATA, gridData)
                requestMiddle(context, ApiConstants.API_MIDDLE_LAND_WEATHER, areaCode)
                requestMiddle(context, ApiConstants.API_MIDDLE_TEMPERATURE, areaCode)
                requestLife(context, areaNo)
                requestAirMeasure(context, sidoName[0])
                requestNews(context)
            }
        }
    }
}