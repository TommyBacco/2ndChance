package com.example.group15_lab2.ShowProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClass.User
import com.example.group15_lab2.Repository
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ShowProfileVM : ViewModel() {

    private val user: MutableLiveData<User> by lazy { MutableLiveData<User>().also { loadUser() } }
    private val db = FirebaseFirestore.getInstance()

    fun getUserData(): LiveData<User> {
        return user
    }

    private fun loadUser() {

        db.collection("Users")
            .document("user:" + Repository.userID?.uid)
            .get()
            .addOnSuccessListener { res ->
                user.value = res.toObject(User::class.java) ?: User()
            }

        addListener()
    }

    private fun addListener() {

        db.collection("Users")
            .document("user:" + Repository.userID?.uid)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snap != null && snap.exists()) {
                    user.value = snap.toObject(User::class.java)
                }
            }
    }

}