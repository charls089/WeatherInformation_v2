package com.kobbi.weather.info.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.kobbi.weather.info.data.database.entity.Area
import com.kobbi.weather.info.ui.view.fragment.WeatherViewFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, items:List<Area>) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val mItems = mutableListOf<Area>()

    init {
        mItems.addAll(items)

    }

    fun setItems(items: List<Area>) {
        mItems.clear()
        mItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return WeatherViewFragment.newInstance(position)
    }

    override fun getItemPosition(any: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int = mItems.size
}