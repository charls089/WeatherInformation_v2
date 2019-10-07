package com.kobbi.weather.info.presenter.listener

import android.view.View


interface LongClickListener {
    fun onItemLongClick(position: Int, view: View)
}