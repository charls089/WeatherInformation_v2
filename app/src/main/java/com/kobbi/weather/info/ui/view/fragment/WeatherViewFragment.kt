package com.kobbi.weather.info.ui.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.kobbi.weather.info.R
import com.kobbi.weather.info.databinding.FragmentWeatherBinding
import com.kobbi.weather.info.presenter.viewmodel.AreaViewModel
import com.kobbi.weather.info.presenter.viewmodel.WeatherViewModel

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
            context?.applicationContext?.let {
                activity?.run {
                    weatherVm =
                        ViewModelProviders.of(this)[WeatherViewModel::class.java]

                    areaVm =
                        ViewModelProviders.of(this)[AreaViewModel::class.java].apply {
                            position.observe(this@run, Observer {
                                svContainer.smoothScrollTo(0, 0)
                            })
                        }
                    position = arguments?.getInt(POSITION_INDEX_CODE) ?: 0
                    lifecycleOwner = this@WeatherViewFragment
                }
            }

        }
        return binding.root
    }
}
