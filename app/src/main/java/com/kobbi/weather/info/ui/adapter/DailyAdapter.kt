package com.kobbi.weather.info.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kobbi.weather.info.BR
import com.kobbi.weather.info.R
import com.kobbi.weather.info.data.database.entity.DailyWeather
import com.kobbi.weather.info.databinding.ItemForecastTimeDataBinding

class DailyAdapter(items: List<DailyWeather>) :
    RecyclerView.Adapter<DailyAdapter.ViewHolder>() {
    private val mItems = mutableListOf<DailyWeather>()

    init {
        mItems.addAll(items)
    }

    fun setItems(items: List<DailyWeather>) {
        mItems.clear()
        mItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = mItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemForecastTimeDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_forecast_time_data,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems[position])
    }

    class ViewHolder(private val binding: ItemForecastTimeDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(daily: DailyWeather) {
            binding.setVariable(BR.daily, daily)
        }
    }
}