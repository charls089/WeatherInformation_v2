package com.kobbi.weather.info.presenter.listener

interface CompleteListener {
    fun onComplete(code: Int = -1, data: Any = Any())
}