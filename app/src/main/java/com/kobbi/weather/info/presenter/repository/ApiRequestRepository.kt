package com.kobbi.weather.info.presenter.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.kobbi.weather.info.data.database.entity.Area
import com.kobbi.weather.info.data.network.client.AirMeasureClient
import com.kobbi.weather.info.data.network.client.JusoClient
import com.kobbi.weather.info.data.network.client.WeatherClient
import com.kobbi.weather.info.data.network.response.*
import com.kobbi.weather.info.presenter.listener.CompleteListener
import com.kobbi.weather.info.presenter.model.data.AreaCode
import com.kobbi.weather.info.presenter.model.data.GridData
import com.kobbi.weather.info.presenter.model.type.Address
import com.kobbi.weather.info.presenter.model.type.ReturnCode
import com.kobbi.weather.info.presenter.model.type.OfferType
import com.kobbi.weather.info.util.ApiConstants
import com.kobbi.weather.info.util.DLog
import com.kobbi.weather.info.util.LocationUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.util.*
import kotlin.collections.LinkedHashMap

class ApiRequestRepository private constructor() {
    companion object {
        private const val TAG = "ApiRequestRepository"

        @JvmStatic
        fun requestVillage(
            context: Context,
            type: OfferType,
            gridData: GridData,
            listener: CompleteListener? = null
        ) {
            if (!isNetworkConnected(context)) {
                listener?.onComplete(ReturnCode.NETWORK_DISABLED)
                return
            }
            val apiUrl = when (type) {
                OfferType.CURRENT, OfferType.YESTERDAY -> ApiConstants.API_FORECAST_TIME_DATA
                OfferType.MIN_MAX, OfferType.DAILY -> ApiConstants.API_FORECAST_SPACE_DATA
                else -> return
            }
            val baseTime = OfferType.getBaseDateTime(type)
            val params = LinkedHashMap<String, Any>().apply {
                put("base_date", baseTime.first)
                put("base_time", baseTime.second)
                put("nx", gridData.x)
                put("ny", gridData.y)
                put("numOfRows", 1000)
                put("dataType", "json")
            }
            val client = WeatherClient.getInstance()
            client.requestWeather(
                    ApiConstants.API_VILLAGE_SERVICE,
                    apiUrl,
                    getApiKey(context),
                    params
                )
                .enqueue(object : Callback<WeatherResponse> {
                    override fun onResponse(
                        call: Call<WeatherResponse>,
                        response: Response<WeatherResponse>
                    ) {
                        DLog.d(
                            context,
                            TAG,
                            "requestWeather.onResponse() -> <$apiUrl>call : $call, response : $response"
                        )
                        val items = response.body()?.response?.body?.items?.item
                        DLog.d(tag = TAG, message = "requestWeather.items : $items")
                        WeatherRepository.getInstance(context).insertWeather(gridData, items)
                        listener?.onComplete(ReturnCode.NO_ERROR, type)
                    }

                    override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                        DLog.e(
                            context,
                            TAG,
                            "requestWeather.onFailure() -> <$apiUrl>call : $call, t : $t"
                        )
                        listener?.run {
                            if (t is SocketTimeoutException) {
                                listener.onComplete(ReturnCode.SOCKET_TIMEOUT, type)
                            } else {
                                listener.onComplete(ReturnCode.UNKNOWN_ERROR, type)
                            }
                        }
                    }
                })
        }

        @JvmStatic
        fun requestMiddle(
            context: Context,
            apiUrl: String,
            areaCode: AreaCode,
            listener: CompleteListener? = null
        ) {
            if (!isNetworkConnected(context)) {
                listener?.onComplete(ReturnCode.NETWORK_DISABLED)
                return
            }
            val id =
                if (apiUrl == ApiConstants.API_MIDDLE_TEMPERATURE) areaCode.cityCode else areaCode.prvnCode
            val tmFc = OfferType.getBaseDateTime(OfferType.WEEKLY)
            val params = LinkedHashMap<String, Any>().apply {
                put("regId", id)
                put("tmFc", tmFc.first + tmFc.second)
                put("dataType", "json")
            }
            val client = WeatherClient.getInstance()
            client.requestWeather(
                    ApiConstants.API_MIDDLE_SERVICE,
                    apiUrl,
                    getApiKey(context),
                    params
                )
                .enqueue(object : Callback<WeatherResponse> {
                    override fun onResponse(
                        call: Call<WeatherResponse>,
                        response: Response<WeatherResponse>
                    ) {
                        DLog.d(
                            context, TAG,
                            "requestMiddle.onResponse() -> <$apiUrl>call : $call, response : $response"
                        )
                        val items = response.body()?.response?.body?.items?.item
                        DLog.d(tag = TAG, message = "requestMiddle.items : $items")
                        WeatherRepository.getInstance(context).insertMiddle(areaCode, items)
                        listener?.onComplete(ReturnCode.NO_ERROR, OfferType.WEEKLY)
                    }

                    override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                        DLog.e(
                            context,
                            TAG,
                            "requestMiddle.onFailure() -> <$apiUrl>call : $call, t : $t"
                        )
                        listener?.run {
                            if (t is SocketTimeoutException) {
                                listener.onComplete(ReturnCode.SOCKET_TIMEOUT, OfferType.WEEKLY)
                            } else {
                                listener.onComplete(ReturnCode.UNKNOWN_ERROR, OfferType.WEEKLY)
                            }
                        }
                    }
                })
        }

        @JvmStatic
        fun requestLife(
            context: Context,
            type: OfferType,
            areaNo: String,
            listener: CompleteListener? = null
        ) {
            if (!isNetworkConnected(context)) {
                listener?.onComplete(ReturnCode.NETWORK_DISABLED)
                return
            }
            val apiList = ApiConstants.LifeHealthApi.checkRequestUrl()
            for (api in apiList) {
                if (api.offerType == type) {
                    val time = OfferType.getBaseDateTime(type)
                    val params = java.util.LinkedHashMap<String, Any>().apply {
                        put("areaNo", areaNo)
                        put("time", time.first + time.second.dropLast(2))
                        put("dataType", "json")
                    }
                    val serviceUrl = when (api.serviceType) {
                        ApiConstants.ServiceType.LIFE -> ApiConstants.API_LIFE_SERVICE
                        ApiConstants.ServiceType.HEALTH -> ApiConstants.API_HEALTH_SERVICE
                    }
                    val client = WeatherClient.getInstance()
                    client.requestWeather(serviceUrl, api.url, getApiKey(context), params)
                        .enqueue(object : Callback<WeatherResponse> {
                            override fun onResponse(
                                call: Call<WeatherResponse>, response: Response<WeatherResponse>
                            ) {
                                DLog.d(
                                    context, TAG,
                                    "requestLife.onResponse() -> <${api.apiName}>call : $call, response : $response"
                                )
                                val items = response.body()?.response?.body?.items?.item
                                DLog.d(tag = TAG, message = "<$api>items : $items")
                                WeatherRepository.getInstance(context).insertLife(areaNo, items)
                                listener?.onComplete(ReturnCode.NO_ERROR, type)
                            }

                            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                                DLog.e(
                                    context,
                                    TAG,
                                    "requestLife.onFailure() -> <${api.apiName}>call : $call, t : $t"
                                )
                                listener?.run {
                                    if (t is SocketTimeoutException) {
                                        listener.onComplete(ReturnCode.SOCKET_TIMEOUT, type)
                                    } else {
                                        listener.onComplete(ReturnCode.UNKNOWN_ERROR, type)
                                    }
                                }
                            }
                        })
                }
            }
        }

        @JvmStatic
        fun requestNews(context: Context, listener: CompleteListener? = null) {
            if (!isNetworkConnected(context)) {
                listener?.onComplete(ReturnCode.NETWORK_DISABLED)
                return
            }
            val params = java.util.LinkedHashMap<String, Any>().apply {
                put("numOfRows", 100)
                put("dataType", "json")
            }
            val client = WeatherClient.getInstance()
            client.requestWeather(
                ApiConstants.API_SPECIAL_SERVICE,
                ApiConstants.API_SPECIAL_STATUS,
                getApiKey(context),
                params
            ).enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    DLog.d(
                        context,
                        TAG,
                        "requestNews.onResponse() -> call : $call, response : $response"
                    )
                    val items = response.body()?.response?.body?.items?.item
                    DLog.d(tag = TAG, message = "requestNews.items : $items")
                    WeatherRepository.getInstance(context).insertSpecialNews(items)
                    listener?.onComplete(ReturnCode.NO_ERROR, OfferType.BASE)
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    DLog.e(context, TAG, "requestNews.onFailure() -> call : $call, t : $t")
                    listener?.run {
                        if (t is SocketTimeoutException) {
                            listener.onComplete(ReturnCode.SOCKET_TIMEOUT, OfferType.BASE)
                        } else {
                            listener.onComplete(ReturnCode.UNKNOWN_ERROR, OfferType.BASE)
                        }
                    }
                }
            })
        }

        @JvmStatic
        fun requestAirMeasure(
            context: Context,
            sidoName: String,
            listener: CompleteListener? = null
        ) {
            if (!isNetworkConnected(context)) {
                listener?.onComplete(ReturnCode.NETWORK_DISABLED)
                return
            }
            val addressCode = Address.getSidoCode(sidoName)
            addressCode?.let {
                val params = java.util.LinkedHashMap<String, Any>().apply {
                    put("numOfRows", 1000)
                    put("sidoName", addressCode.shortName)
                    put("searchCondition", "HOUR")
                    put("_returnType", "json")
                }
                val client = AirMeasureClient.getInstance()
                client.requestAirMeasure(getApiKey(context), params)
                    .enqueue(object : Callback<AirResponse> {
                        override fun onResponse(
                            call: Call<AirResponse>,
                            response: Response<AirResponse>
                        ) {
                            DLog.d(
                                context, TAG,
                                "requestAirMeasure.onResponse() -> call : $call, response : $response"
                            )
                            val list = response.body()?.list
                            DLog.d(tag = TAG, message = "requestAirMeasure.list : $list")
                            WeatherRepository.getInstance(context)
                                .insertAirMeasure(addressCode.fullName, list)
                            listener?.onComplete(ReturnCode.NO_ERROR, OfferType.AIR)
                        }

                        override fun onFailure(call: Call<AirResponse>, t: Throwable) {
                            DLog.e(
                                context,
                                TAG,
                                "requestAirMeasure.onFailure() -> call : $call, t : $t"
                            )
                            listener?.run {
                                if (t is SocketTimeoutException) {
                                    listener.onComplete(ReturnCode.SOCKET_TIMEOUT, OfferType.AIR)
                                } else {
                                    listener.onComplete(ReturnCode.UNKNOWN_ERROR, OfferType.AIR)
                                }
                            }
                        }

                    })
            }
        }

        @JvmStatic
        fun requestJuso(
            context: Context,
            apiUrl: String,
            code: List<String>,
            listener: CompleteListener
        ) {
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
            client.requestJuso(apiUrl, getApiKey(context), params)
                .enqueue(object : Callback<JusoResponse> {
                    override fun onResponse(
                        call: Call<JusoResponse>,
                        response: Response<JusoResponse>
                    ) {
                        DLog.d(
                            tag = TAG,
                            message = "requestJuso.onResponse() -> call : $call, response : $response"
                        )
                        val items = response.body()?.response?.body
                        DLog.d(tag = TAG, message = "requestJuso.items : $items")
                        if (items != null)
                            listener.onComplete(ReturnCode.NO_ERROR, items)
                        else
                            listener.onComplete(ReturnCode.DATA_IS_NULL, Any())
                    }

                    override fun onFailure(call: Call<JusoResponse>, t: Throwable) {
                        val message = "onFailure() -> call : $call, t : $t"
                        listener.onComplete(ReturnCode.UNKNOWN_ERROR, message)
                    }
                })
        }

        @JvmStatic
        fun initBaseAreaData(context: Context, area: Area, listener: CompleteListener? = null) {
            val sidoName = LocationUtils.splitAddressLine(area.address)
            val areaCode = AreaCode(
                area.prvnCode,
                area.cityCode
            )
            val gridData =
                GridData(area.gridX, area.gridY)
            val areaNo = area.areaCode
            requestVillage(context, OfferType.CURRENT, gridData, listener)
            requestVillage(context, OfferType.YESTERDAY, gridData, listener)
            requestVillage(context, OfferType.DAILY, gridData, listener)
            requestVillage(context, OfferType.MIN_MAX, gridData, listener)
            requestMiddle(context, ApiConstants.API_MIDDLE_LAND_WEATHER, areaCode, listener)
            requestMiddle(context, ApiConstants.API_MIDDLE_TEMPERATURE, areaCode, listener)
            requestLife(context, OfferType.LIFE_DAY, areaNo)
            requestLife(context, OfferType.LIFE_TIME, areaNo)
            requestAirMeasure(context, sidoName[0])
            requestNews(context)
        }

        private fun isNetworkConnected(context: Context): Boolean {
            val conn =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            return conn?.run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getNetworkCapabilities(this.activeNetwork)?.let { capabilities ->
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    } ?: false
                } else {
                    val activeNetwork = activeNetworkInfo
                    @Suppress("DEPRECATION")
                    when (activeNetwork?.type) {
                        ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_MOBILE -> true
                        else -> false
                    }
                }
            } ?: false
        }

        private fun getApiKey(context: Context): String {
            val asset = context.applicationContext.assets.open("apiConfig.properties")
            return Properties().run {
                load(asset)
                getProperty("data_api_key")
            }
        }
    }
}