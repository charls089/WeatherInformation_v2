package com.kobbi.weather.info.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.kobbi.weather.info.R

class LifeItemLayout(context: Context) : LinearLayout(context) {
    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.item_life_list, this, true)
    }
}