package com.example.group15_lab2.RatingUser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClasses.Rating
import com.example.group15_lab2.FirebaseRepository
import java.text.SimpleDateFormat
import java.util.*

class RatingUserVM : ViewModel() {

    private var itemID:String? = null
    private var ownerID:String? = null
    private val ratingUser: MutableLiveData<Rating> by lazy { MutableLiveData<Rating>()
        .apply {
            val rating = Rating()
            rating.buyer = FirebaseRepository.getUserAccount().value?.uid
            value = rating
        } }

    fun setItemData(id: String?, owner: String?) {
        itemID = id
        ownerID = owner
    }

    fun getRatingUser() : LiveData<Rating> = ratingUser

    fun editComment(value: String){
        ratingUser.value?.comment=value
    }

    fun editRating(value:Float){
        ratingUser.value?.rating=value
    }

    fun sendUserRating(userNickname: String?) {
        ratingUser.value?.buyer_nickname = userNickname
        val currentTime: Date = Calendar.getInstance().time
        val df = SimpleDateFormat.getDateInstance()
        ratingUser.value?.date = df.format(currentTime)

        FirebaseRepository.sendUserRating(ownerID,ratingUser.value ?: Rating())
        FirebaseRepository.updateItemRating(itemID)
    }

}