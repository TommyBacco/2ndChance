package com.example.group15_lab2.MainActivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClass.User
import com.example.group15_lab2.FirebaseRepository

class MainActivityVM : ViewModel() {

    private val user: MutableLiveData<User> by lazy { MutableLiveData<User>().also { loadUser() } }

    fun getUserData(): LiveData<User> {
        return user
    }

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
}