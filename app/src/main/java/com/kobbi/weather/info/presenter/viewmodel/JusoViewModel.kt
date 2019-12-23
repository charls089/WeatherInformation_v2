package com.kobbi.weather.info.presenter.viewmodel

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.core.os.postDelayed
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kobbi.weather.info.R
import com.kobbi.weather.info.data.network.domain.juso.JusoItem
import com.kobbi.weather.info.presenter.listener.CompleteListener
import com.kobbi.weather.info.presenter.model.type.Address
import com.kobbi.weather.info.presenter.model.type.ReturnCode
import com.kobbi.weather.info.presenter.repository.ApiRequestRepository
import com.kobbi.weather.info.presenter.repository.WeatherRepository
import com.kobbi.weather.info.util.ApiConstants
import com.kobbi.weather.info.util.Constants
import com.kobbi.weather.info.util.DLog
import com.kobbi.weather.info.util.SingleLiveEvent
import kotlin.concurrent.thread

class JusoViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val PRVN_DEPTH = 1
        private const val CITY_DEPTH = 2
        private const val DONG_DEPTH = 3
    }

    val depth: LiveData<Int> get() = _depth
    val address: LiveData<String> get() = _address
    val jusoTitle: LiveData<Int> get() = _jusoTitle
    val jusoList: LiveData<List<String>> get() = _jusoList
    private val _depth = MutableLiveData<Int>()
    private val _address = MutableLiveData<String>()
    private val _jusoTitle = MutableLiveData<Int>()
    private val _jusoList = MutableLiveData<List<String>>()

    private val _weatherRepository = WeatherRepository.getInstance(application)

    val clickEnd: LiveData<Any> get() = _clickEnd
    val clickClose: LiveData<Any> get() = _clickClose
    private val _clickEnd = SingleLiveEvent<Any>()
    private val _clickPrev = SingleLiveEvent<Any>()
    private val _clickClose = SingleLiveEvent<Any>()

    // 광역시/도, 시/군/구 데이터 저장 리스트
    private val mCodeList = mutableListOf<String>()

    private var mPrevPosition = -1
    private var mSidoCode = ""
    private var mSigunguCode = ""
    private var mDongCode = ""
    private var mDepth = 0

    private var mIsLocked = false

    private val listener = object : CompleteListener {
        override fun onComplete(code: ReturnCode, data: Any) {
            when (code) {
                ReturnCode.NO_ERROR -> {
                    if (data is List<*>) {
                        val jusoItems = mutableListOf<String>()
                        data.forEach {
                            if (it is JusoItem) {
                                val item = if (it.brtcNm != null) {
                                    Address.getFullName(it.brtcNm)?.fullName ?: ""
                                } else {
                                    it.cd
                                }
                                jusoItems.add(item)
                            }
                        }
                        _jusoList.postValue(jusoItems)
                    }
                }
                else -> {
                    //Nothing
                }
            }
        }
    }

    init {
        loadJusoList()
    }

    fun clickEnd() {
        setAreaJuso(getApplication())
        _clickEnd.call()
    }

    fun clickPrev() {
        DLog.d(tag = "JusoModel", message = "clickPrev($mDepth)")
        mDepth -= 2
        _depth.postValue(mDepth)
        loadJusoList()
        _clickPrev.call()
    }

    fun clickClose() {
        _clickClose.call()
    }

    fun loadJusoList(position: Int = -1) {
        if (mIsLocked)
            return
        mIsLocked = true
        Handler(Looper.getMainLooper()).postDelayed(500) {
            mIsLocked = false
        }
        mPrevPosition = if (mPrevPosition == position) -1 else position
        if (mPrevPosition != -1) {
            val selectedJuso = _jusoList.value?.get(position)
            setJusoCode(selectedJuso)
        }
        val apiUrl = getApiUrl()
        setJusoTitle(apiUrl)
        val jusoList = getJusoList()
        mCodeList.clear()
        mCodeList.addAll(jusoList)
        _address.postValue(convertAddress(mCodeList))
        if (mDepth == DONG_DEPTH) {
            setAreaJuso(getApplication())
            clickClose()
        } else {
            mPrevPosition = -1
            _depth.postValue(++mDepth)
            ApiRequestRepository.requestJuso(apiUrl, mCodeList, listener = listener)
        }
    }

    private fun setAreaJuso(context: Context) {
        thread {
            with(_weatherRepository) {
                insertArea(context, mCodeList, Constants.STATE_CODE_ACTIVE)
                val address = convertAddress(mCodeList)
                insertPlace(address)
            }
        }
    }

    private fun setJusoCode(selectedJuso: String?) {
        selectedJuso?.let {
            when (mDepth) {
                PRVN_DEPTH -> {
                    val address = Address.getSidoCode(selectedJuso)
                    mSidoCode = address?.fullName ?: ""
                    if (mSidoCode == Address.SEJONG.fullName)
                        mSigunguCode = ""
                }
                CITY_DEPTH -> {
                    mSigunguCode = selectedJuso
                }
                DONG_DEPTH -> {
                    mDongCode = selectedJuso
                }
            }
        }
    }

    private fun getJusoList(): List<String> {
        val result = mutableListOf<String>()
        when (mDepth) {
            PRVN_DEPTH -> result.add(0, mSidoCode)
            CITY_DEPTH -> {
                result.run {
                    add(0, mSidoCode)
                    add(1, mSigunguCode)
                }
            }
            DONG_DEPTH -> {
                result.run {
                    add(0, mSidoCode)
                    add(1, mSigunguCode)
                    add(2, mDongCode)
                }
            }
        }
        return result
    }

    private fun getApiUrl(): String {
        return when (mDepth) {
            PRVN_DEPTH -> {
                if (mSidoCode == Address.SEJONG.fullName) {
                    mDepth++
                    ApiConstants.API_DONG_URL
                } else
                    ApiConstants.API_CITY_URL
            }
            CITY_DEPTH -> ApiConstants.API_DONG_URL
            DONG_DEPTH -> ""
            else -> ApiConstants.API_PRVN_URL
        }
    }

    private fun setJusoTitle(apiUrl: String) {
        _jusoTitle.postValue(
            when (apiUrl) {
                ApiConstants.API_CITY_URL -> R.string.guide_city_select
                ApiConstants.API_DONG_URL -> R.string.guide_district_select
                else -> R.string.guide_prvn_select
            }
        )
    }

    private fun convertAddress(addressList: List<String>): String {
        var str = ""
        addressList.forEach {
            if (!TextUtils.isEmpty(it))
                str += "$it "
        }
        str = str.dropLast(1)
        return str
    }
}