package com.kobbi.weather.info.presenter.model.data

data class BranchCode(
    val areaNo: String,
    val gridX: Int,
    val gridY: Int
) {
    override fun toString(): String {
        return "areaNo : $areaNo, gridX : $gridX, gridY : $gridY"
    }
}