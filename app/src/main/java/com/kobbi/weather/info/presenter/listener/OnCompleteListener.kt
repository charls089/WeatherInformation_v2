package com.kobbi.weather.info.presenter.listener

interface OnCompleteListener {
    fun onComplete(code: Int = -1, data: Any = Any())
}