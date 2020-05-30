package com.example.group15_lab2.InterestedUserList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClasses.User
import com.example.group15_lab2.FirebaseRepository

class InterestedUserListVM : ViewModel() {

    private var itemID:String? = "No item id"
    private val interestedUsers: MutableLiveData<List<User>> by lazy { MutableLiveData<List<User>>().also { loadUsers() } }

    fun setItemID(id:String?){
        itemID = id
    }

    fun getInterestedUsers(): LiveData<List<User>> {
        return interestedUsers
    }

    private fun loadUsers() {

        FirebaseRepository.getInterestedUsers(itemID).addSnapshotListener{ res, err ->
            if (err != null) {
                Log.d("ERROR-TAG", "Listen getInterestedUsers failed")
                return@addSnapshotListener
            }
            val users : MutableList<User> = mutableListOf()
            for(doc in res!!){
                val item = doc.toObject(User::class.java)
                users.add(item)
            }
            interestedUsers.value = users
        }

    }

}