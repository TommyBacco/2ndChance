package com.example.group15_lab2

import androidx.lifecycle.LiveData
import com.example.group15_lab2.DataClass.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

object Repository {
    var userID: FirebaseUser? = null
    private val db = FirebaseFirestore.getInstance()

    fun getUserData():User{
        var user = User()

        db.collection("Utenti")
            .document("user:"+userID?.uid)
            .get()
            .addOnSuccessListener { res ->
                user = res.toObject(User::class.java) ?: User()
            }

        return user
    }
}