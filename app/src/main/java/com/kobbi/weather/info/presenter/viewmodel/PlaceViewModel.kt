package com.kobbi.weather.info.presenter.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kobbi.weather.info.presenter.repository.WeatherRepository
import kotlin.concurrent.thread

class PlaceViewModel(application: Application) : AndroidViewModel(application) {
    val place get() = _place
    val selectedPositions: LiveData<List<Int>> get() = _selectedPositions
    val clickPosition: LiveData<String> get() = _clickPosition
    private val weatherRepository = WeatherRepository.getInstance(application)
    private val _place: LiveData<List<String>> = weatherRepository.loadPlaceAddressLive()
    private val _selectedPositions: MutableLiveData<List<Int>> = MutableLiveData()
    private val _clickPosition: MutableLiveData<String> = MutableLiveData()

    private val mDeleteList = mutableListOf<Int>()

    val isMultiCheck: MutableLiveData<Boolean> = MutableLiveData()

    init {
        _selectedPositions.postValue(mDeleteList)
    }

    private fun setMultiCheck(isMulti: Boolean) {
        isMultiCheck.postValue(isMulti)
    }

    fun selectItem(position: Int) {
        place.value?.get(position)?.let {
            _clickPosition.postValue(it)
        }
    }

    fun addDeleteItem(position: Int) {
        setMultiCheck(true)
        if (mDeleteList.contains(position))
            mDeleteList.remove(position)
        else
            mDeleteList.add(position)

        _selectedPositions.postValue(mDeleteList)
        if (mDeleteList.isEmpty())
            setMultiCheck(false)
    }

    fun deletePlace() {
        thread {
            val delList = mutableListOf<String>()
            val placeList = _place.value
            placeList?.let {
                mDeleteList.forEach { idx ->
                    val address = placeList[idx]
                    delList.add(address)
                    weatherRepository.updateAreaCode(address, 1)
                }
            }
            mDeleteList.clear()
            _selectedPositions.postValue(mDeleteList)
            setMultiCheck(false)
            weatherRepository.deletePlace(delList)
        }
    }

    fun clearSelectedPlace() {
        mDeleteList.clear()
        _selectedPositions.postValue(mDeleteList)
        setMultiCheck(false)
    }


}