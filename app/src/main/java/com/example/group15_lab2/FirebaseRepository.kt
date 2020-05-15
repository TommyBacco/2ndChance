package com.example.group15_lab2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.group15_lab2.DataClass.Item
import com.example.group15_lab2.DataClass.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object FirebaseRepository {
    private val userID:MutableLiveData<FirebaseUser> by lazy { MutableLiveData<FirebaseUser>().apply { value = null } }
    private val db = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference

    fun getUserID():LiveData<FirebaseUser>{
        return userID
    }

    fun setUserID(id:FirebaseUser?){
        userID.value = id
    }

    fun getUserData(): DocumentReference {
        return db.document("Users/user:" + userID.value?.uid)
    }

    fun saveUserData(user: User?, image: ByteArray) {

        user?.ID = userID.value?.uid

        if (image.isEmpty()){
            saveUser(user)
            return
        }

        val imageRef = storageRef.child("UsersImages/user:" + userID.value?.uid)
        val uploadTask = imageRef.putBytes(image)

         uploadTask.addOnSuccessListener {
            val downloadUrl = imageRef.downloadUrl
            downloadUrl.addOnSuccessListener {
                user?.avatarURL = it.toString()
                saveUser(user)
            }
        }
            .addOnFailureListener {
                //TODO GESTIRE CASO ERRORE
            }
    }

    private fun saveUser(user: User?) {
        db.collection("Users")
            .document("user:" + userID.value?.uid)
            .set(user ?: User())
    }

    fun getItemData(id:String?): DocumentReference{
        return db.document("Items/item:$id")
    }

    fun saveItemData(item: Item?, image: ByteArray) {

        item?.ownerID = userID.value?.uid

        if (image.isEmpty()){
            saveItem(item)
            return
        }

        val imageRef = storageRef.child("ItemsImages/item:" + item?.ID)
        val uploadTask = imageRef.putBytes(image)

        uploadTask.addOnSuccessListener {
            val downloadUrl = imageRef.downloadUrl
            downloadUrl.addOnSuccessListener {
                item?.imageURL = it.toString()
                saveItem(item)
            }
        }
            .addOnFailureListener {
                //TODO GESTIRE CASO ERRORE
            }
    }


    private fun saveItem(item: Item?) {
        db.collection("Items")
            .document("item:" + item?.ID)
            .set(item ?: Item())
    }

    fun getMyItemsList():FirestoreRecyclerOptions<Item>{
        val query = db.collection("Items").whereEqualTo("ownerID", userID.value?.uid)
        return FirestoreRecyclerOptions.Builder<Item>()
            .setQuery(query,Item::class.java)
            .build()
    }

}