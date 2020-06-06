package com.example.group15_lab2.MainActivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClasses.LocationPosition
import com.example.group15_lab2.DataClasses.User
import com.example.group15_lab2.FirebaseRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ListenerRegistration

class MainActivityVM : ViewModel() {

    private val user: MutableLiveData<User> by lazy { MutableLiveData<User>()}
    private var listenerRegistration:ListenerRegistration? = null

    fun getUserData(): LiveData<User> {
        return user
    }

    fun getUserAccount(): LiveData<FirebaseUser> = FirebaseRepository.getUserAccount()

    fun getUserNickname():String? = user.value?.nickname

    fun getUserLocation():LocationPosition? = user.value?.userLocation

    fun loadUserData() {
         listenerRegistration = FirebaseRepository.getUserData().addSnapshotListener { doc, err ->
            if (err != null) {
                Log.d("ERROR-TAG", "Listen ShowProfile failed")
                user.value = User()
            } else {
                user.value = doc?.toObject(User::class.java) ?: User()
            }
        }
    }

    fun signOut(){
        listenerRegistration?.remove()
        FirebaseRepository.signOut()
    }
}