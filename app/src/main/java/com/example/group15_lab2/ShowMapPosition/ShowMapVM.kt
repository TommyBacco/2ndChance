package com.example.group15_lab2.ShowMapPosition

import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClasses.LocationPosition

class ShowMapVM : ViewModel() {

    private var position:LocationPosition = LocationPosition()

    fun setPosition(locationPosition: LocationPosition?){
        position = locationPosition ?: LocationPosition()
    }

    fun getPosition() = position
}