package com.example.group15_lab2.MainActivity

import android.util.Log
import androidx.lifecycle.*
import com.example.group15_lab2.DataClasses.User
import com.example.group15_lab2.FirebaseRepository

class MainActivityVM : ViewModel() {

    private val user: MutableLiveData<User> = MutableLiveData()

    fun getUserData(): LiveData<User> {
        return user
    }

    fun getUserNickname():String? = user.value?.nickname

    private fun loadUser() {
        FirebaseRepository.getUserData().addSnapshotListener { doc, err ->
            if (err != null) {
                Log.d("ERROR-TAG", "Listen ShowProfile failed")
                user.value = User()
            } else {
                user.value = doc?.toObject(User::class.java) ?: User()
            }
        }
    }

    fun setUser(){
        user.apply{loadUser()}
    }
}