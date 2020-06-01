package com.example.group15_lab2.ShowUserRating

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClasses.Rating
import com.example.group15_lab2.FirebaseRepository

class ShowUserRatingVM : ViewModel() {
    private val ratings: MutableLiveData<List<Rating>> by lazy { MutableLiveData<List<Rating>>().also { loadRatings() } }
    private var userID:String? = null

    fun setUserID(id:String?){
        userID = id
    }

    fun getUserRatings(): LiveData<List<Rating>> {
        return ratings
    }

    private fun loadRatings() {

        FirebaseRepository.getUserRating(userID).addSnapshotListener{ res, err ->
            if (err != null) {
                Log.d("ERROR-TAG", "Listen getRatings failed")
                return@addSnapshotListener
            }
            val list : MutableList<Rating> = mutableListOf()
            for(doc in res!!){
                val item = doc.toObject(Rating::class.java)
                list.add(item)
            }
            ratings.value = list
        }

    }
}