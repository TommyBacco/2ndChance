package com.example.group15_lab2.ShowProfile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClasses.Rating
import com.example.group15_lab2.DataClasses.User
import com.example.group15_lab2.FirebaseRepository

class ShowProfileVM : ViewModel() {

    private val user: MutableLiveData<User> by lazy { MutableLiveData<User>().also { loadUser() } }
    private var userID:String? = null
    private val userRatings: MutableLiveData<List<Rating>> by lazy { MutableLiveData<List<Rating>>().also { loadRatings() } }

    fun getUserID():String? = userID
    fun setUserID(id:String?){
        userID=id
    }


    fun getUserData(): LiveData<User> {
        return user
    }

    fun getUserRatings():LiveData<List<Rating>> = userRatings

    private fun loadUser() {
        FirebaseRepository.getUserData(userID).addSnapshotListener { doc, err ->
            if (err != null) {
                Log.d("ERROR-TAG", "Listen ShowProfile failed")
                user.value = User()
            } else {
                user.value = doc?.toObject(User::class.java) ?: User()
            }
        }
    }

    private fun loadRatings() {
        FirebaseRepository.getUserRating(userID).addSnapshotListener { res, err ->
            if (err != null) {
                Log.d("ERROR-TAG", "Listen getAdvertisements failed")
                return@addSnapshotListener
            }
            val list : MutableList<Rating> = mutableListOf()
            for(doc in res!!){
                val item = doc.toObject(Rating::class.java)
                list.add(item)
            }
            userRatings.value = list
        }
    }
}