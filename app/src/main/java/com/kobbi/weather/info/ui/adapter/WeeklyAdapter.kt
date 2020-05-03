package com.kobbi.weather.info.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kobbi.weather.info.BR
import com.kobbi.weather.info.R
import com.kobbi.weather.info.data.database.entity.WeeklyWeather
import com.kobbi.weather.info.databinding.ItemForecastWeekDataBinding

class WeeklyAdapter(items: List<WeeklyWeather>) : RecyclerView.Adapter<WeeklyAdapter.ViewHolder>() {
    private val mItems = mutableListOf<WeeklyWeather>()

    init {
        mItems.addAll(items)
    }

    fun setItems(items: List<WeeklyWeather>) {
        mItems.clear()
        mItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = mItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemForecastWeekDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_forecast_week_data,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems[position])

    }

    class ViewHolder(private val binding: ItemForecastWeekDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(weekly: WeeklyWeather) {
            binding.setVariable(BR.weekly, weekly)
        }
    }
}