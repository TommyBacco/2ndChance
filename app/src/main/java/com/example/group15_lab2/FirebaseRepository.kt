package com.example.group15_lab2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.group15_lab2.DataClass.Item
import com.example.group15_lab2.DataClass.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage


object FirebaseRepository {
    private val userAccount:MutableLiveData<FirebaseUser> by lazy { MutableLiveData<FirebaseUser>().apply { value = null } }
    private val db = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference

    fun getUserAccount():LiveData<FirebaseUser>{
        return userAccount
    }

    fun setUserAccount(id:FirebaseUser?){
        userAccount.value = id
    }

    fun getUserData(userID:String? = null): DocumentReference {

        db.document("Users/user:")

        val userToGet:String?
        if(userID == null)
            userToGet = userAccount.value?.uid
        else
            userToGet = userID
        return db.document("Users/user:$userToGet")
    }

    fun saveUserData(user: User?, image: ByteArray) {

        user?.ID = userAccount.value?.uid

        if (image.isEmpty()){
            saveUser(user)
            return
        }

        val imageRef = storageRef.child("UsersImages/user:" + userAccount.value?.uid)
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
            .document("user:" + userAccount.value?.uid)
            .set(user ?: User())
    }

    fun getItemData(id:String?): DocumentReference{
        return db.document("Items/item:$id")
    }

    fun saveItemData(item: Item?, image: ByteArray) {

        item?.ownerID = userAccount.value?.uid

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

    fun getMyItemsOptions():FirestoreRecyclerOptions<Item>{
        val query = db.collection("Items")
            .whereEqualTo("ownerID", userAccount.value?.uid)

        return FirestoreRecyclerOptions.Builder<Item>()
            .setQuery(query,Item::class.java)
            .build()
    }

    fun getInterestedUsersOptions(itemID:String?):FirestoreRecyclerOptions<User>{
        val query = db.collection("Users")
            .whereArrayContains("itemsOfInterest",itemID ?: "No item id")

        return FirestoreRecyclerOptions.Builder<User>()
            .setQuery(query,User::class.java)
            .build()
    }

    fun getAdvertisements(): CollectionReference {
        return db.collection("Items")
    }

    fun addItemInterest(itemID:String){
       db.collection("Users")
            .document("user:"+ userAccount.value?.uid)
            .update("itemsOfInterest", FieldValue.arrayUnion(itemID))
            .addOnFailureListener {
                val u = User()
                u.ID = userAccount.value?.uid
                u.itemsOfInterest.add(itemID)
                saveUser(u)
            }

        db.collection("Items")
            .document("item:$itemID")
            .update("interestedUsers", FieldValue.arrayUnion(userAccount.value?.uid))
    }

    fun removeItemInterest(itemID:String){
        db.collection("Users")
            .document("user:"+ userAccount.value?.uid)
            .update("itemsOfInterest", FieldValue.arrayRemove(itemID))

        db.collection("Items")
            .document("item:$itemID")
            .update("interestedUsers", FieldValue.arrayRemove(userAccount.value?.uid))

    }


}