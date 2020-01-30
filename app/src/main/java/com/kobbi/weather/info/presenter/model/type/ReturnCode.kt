package com.kobbi.weather.info.presenter.model.type

enum class ReturnCode {
    NO_ERROR,
    DATA_IS_NULL,
    NOT_UPDATE_TIME,
    NETWORK_DISABLED,
    SOCKET_TIMEOUT,
    AREA_IS_NOT_FOUND,
    UNKNOWN_ERROR
}