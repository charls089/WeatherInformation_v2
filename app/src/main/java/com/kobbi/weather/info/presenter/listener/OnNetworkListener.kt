package com.kobbi.weather.info.presenter.listener

import org.json.JSONObject

interface OnNetworkListener {
    fun onSuccess(jsonObj: JSONObject)
    fun onError(errorCode: Int, message: String)
}