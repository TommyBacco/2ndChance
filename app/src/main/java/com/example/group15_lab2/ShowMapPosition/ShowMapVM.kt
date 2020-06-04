package com.example.group15_lab2.ShowMapPosition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClasses.LocationPosition
import com.google.android.gms.maps.model.MarkerOptions

class ShowMapVM : ViewModel() {

    private var position:LocationPosition = LocationPosition()
    private var userPosition:LocationPosition = LocationPosition()
    private var position_mo:MarkerOptions? = null
    private val hasValidLocation: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>().apply { value = false } }
    private var isItem = false

    fun setPosition(locationPosition: LocationPosition?){
        position = locationPosition ?: LocationPosition()
    }

    fun setUserPosition(locationPosition: LocationPosition?){
        userPosition = locationPosition ?: LocationPosition()
    }

    fun setIsItem(value:Boolean){
        isItem = value
    }

    fun setHasValidLocation(value:Boolean){
        if(isItem)
            hasValidLocation.value = value
    }

    fun setPositionMarkerOptions(pos: MarkerOptions){
        position_mo = pos
    }

    fun getPosition() = position
    fun getPositionMarkerOptions() = position_mo
    fun getUserPosition() = userPosition
    fun getHasValidLocation():LiveData<Boolean> = hasValidLocation
}