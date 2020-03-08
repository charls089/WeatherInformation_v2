package com.kobbi.weather.info.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.kobbi.weather.info.R
import com.kobbi.weather.info.databinding.FragmentWeatherBinding
import com.kobbi.weather.info.presenter.WeatherApplication

class WeatherViewFragment : Fragment() {

    companion object {
        private const val POSITION_INDEX_CODE = "index"

        fun newInstance(index: Int): WeatherViewFragment {
            val fragment = WeatherViewFragment()
            val args = Bundle().apply {
                putInt(POSITION_INDEX_CODE, index)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentWeatherBinding>(
            inflater, R.layout.fragment_weather, container, false
        ).apply {
            activity?.run {
                val weatherApplication = this.application as WeatherApplication
                weatherVm = weatherApplication.weatherViewModel
                areaVm = weatherApplication.areaViewModel.apply {
                    position.observe(this@run, Observer {
                        svContainer.smoothScrollTo(0, 0)
                        rvForecastDailyData.smoothScrollToPosition(0)
                        rvForecastWeeklyData.smoothScrollToPosition(0)
                    })
                }
            }
            position = arguments?.getInt(POSITION_INDEX_CODE) ?: 0
            lifecycleOwner = this@WeatherViewFragment
        }
        return binding.root
    }
}
