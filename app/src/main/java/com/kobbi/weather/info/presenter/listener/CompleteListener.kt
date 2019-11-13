package com.kobbi.weather.info.presenter.listener

import com.kobbi.weather.info.presenter.model.type.ErrorCode

interface CompleteListener {
    fun onComplete(code: ErrorCode, data: Any = Any())
}