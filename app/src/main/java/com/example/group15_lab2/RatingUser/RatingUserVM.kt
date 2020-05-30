package com.example.group15_lab2.RatingUser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClasses.Rating
import com.example.group15_lab2.FirebaseRepository

class RatingUserVM : ViewModel() {

    private val ratingUser: MutableLiveData<Rating> by lazy { MutableLiveData<Rating>()
        .apply {
            val rating = Rating()
            rating.buyer = FirebaseRepository.getUserAccount().value?.uid
            value = rating
        } }

    fun getRatingUser() : LiveData<Rating> = ratingUser

    fun editComment(value: String){
        ratingUser.value?.comment=value
    }

    fun editRating(value:Float){
        ratingUser.value?.rating=value
    }

}