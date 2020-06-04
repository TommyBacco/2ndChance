package com.example.group15_lab2.SetMapPosition

import android.location.Address
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClasses.LocationPosition
import com.example.group15_lab2.FirebaseRepository

class SetMapPositionVM : ViewModel(){

    private var currentPosition = FirebaseRepository.getUserPosition().value

    fun getCurrentPosition() = currentPosition

    fun setCurrentPosition(location: String?) {
        if(location != currentPosition?.locality){
            val position = LocationPosition(location)
            FirebaseRepository.setUserPosition(position)
            currentPosition = position
        }
    }

    fun setPosition(address: Address){
        val actualLocation =
            if(address.locality == null) address.countryName
            else address.locality
        currentPosition = LocationPosition(actualLocation,address.latitude,address.longitude)
    }

    fun savePosition(){
        FirebaseRepository.setUserPosition(currentPosition ?: LocationPosition())
    }

}