package com.kobbi.weather.info.presenter.listener

import com.kobbi.weather.info.presenter.model.type.ReturnCode

interface CompleteListener {
    fun onComplete(code: ReturnCode, data: Any = Any())
}