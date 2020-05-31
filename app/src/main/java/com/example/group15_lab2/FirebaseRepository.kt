package com.example.group15_lab2

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.group15_lab2.DataClasses.Item
import com.example.group15_lab2.DataClasses.Rating
import com.example.group15_lab2.DataClasses.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONException
import org.json.JSONObject


object FirebaseRepository {
    private val userAccount:MutableLiveData<FirebaseUser> by lazy { MutableLiveData<FirebaseUser>().apply { value = null } }
    private val db = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference

    private const val googleApisURL = "https://fcm.googleapis.com/fcm/send"
    private const val serverKey = "AAAA_-rvyXk:APA91bGyvWuLQCBYRNkquB4-ZJCeLpggy0tJaKlcHLGcPv3DHxBkaro3G515BuNoXaVZj7GcudN3G2p9EUFZM002oqfd469SAcVxcsP5Cqyq70awOVAMWcNDY4PWtgdHFpyu14tqxQd1"

    fun getUserAccount():LiveData<FirebaseUser>{
        return userAccount
    }

    fun setUserAccount(id:FirebaseUser?){
        userAccount.value = id
    }

    fun getUserData(userID:String? = null): DocumentReference {
        val userToGet = userID ?: userAccount.value?.uid
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
                Log.d("TAG-ERR","Error during uploading userImage into Storage")
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
                Log.d("TAG-ERR","Error during uploading itemImage into Storage")
            }
    }


    private fun saveItem(item: Item?) {
        subscribeToTopic(item?.ID ?: "")
        db.collection("Items")
            .document("item:" + item?.ID)
            .set(item ?: Item())
    }

    fun getAdvertisements(): CollectionReference {
        return db.collection("Items")
    }

    fun getMyItems(): Query {
        return db.collection("Items")
            .whereEqualTo("ownerID", userAccount.value?.uid)
    }

    fun getInterestedUsers(itemID:String?): Query {
        return db.collection("Users")
            .whereArrayContains("itemsOfInterest",itemID ?: "No item id")
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

    fun subscribeToTopic(itemID:String){
        val topic = "item_$itemID"
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "Subscribe completed"
                if (!task.isSuccessful) {
                    msg = "Error during Subscribe!"
                }
                Log.d("TAG-ERR", msg)
            }
    }

    fun unsubscribeFromTopic(itemID:String){
        val topic = "item_$itemID"
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "Unsubscribe completed"
                if (!task.isSuccessful) {
                    msg = "Error during Unsubscribe!"
                }
                Log.d("TAG-ERR", msg)
            }
    }

    fun getNotificationRequest(item:Item,notificationType:String): JsonObjectRequest {
        val notjo = JSONObject()
        val nojoBody = JSONObject()

        val title = "Item: ${item.title}"
        val owner = item.ownerID
        val description = when (notificationType) {
            "sold" -> "has been sold"
            "block" -> "is no longer for sale"
            else -> "there is a new interested user!"
        }
        val topic = "/topics/item_${item.ID}"

        try {
            nojoBody.put("sender", userAccount.value?.uid)
            nojoBody.put("nTitle", title)
            nojoBody.put("nDesc", description)
            nojoBody.put("nType",notificationType)
            nojoBody.put("itemOwner",owner)
            if(notificationType == "sold")
                nojoBody.put("itemSoldTo", item.soldTo)

            notjo.put("to",topic)
            notjo.put("data",nojoBody)

        } catch (e: JSONException){
            Log.d("TAG-ERR","Json error")
        }

       return getPOSTRequest(notjo)

    }

    private fun getPOSTRequest(notjo: JSONObject): JsonObjectRequest {

        val request = object : JsonObjectRequest(
            googleApisURL,
            notjo,
            Response.Listener { Log.d("TAG-ERR","Success Request") },
            Response.ErrorListener { Log.d("TAG-ERR","Error during Request") }){

            override fun getHeaders(): MutableMap<String, String> {
                val map = mutableMapOf<String, String>()
                map.put("Content-Type","application/json")
                map.put("Authorization","key=$serverKey")
                return map
            }
        }

        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        return request
    }

    fun getItemsOfInterest(): Query {
        return db.collection("Items")
            .whereArrayContains("interestedUsers",userAccount.value?.uid ?: "")
    }

    fun getBoughtItems(): Query {
        return db.collection("Items")
            .whereEqualTo("soldTo",userAccount.value?.uid ?: "")
    }

    fun getUserRating(userID:String?):CollectionReference {
        val userToGet = userID ?: userAccount.value?.uid

        return db
            .collection("Users")
            .document("user:$userToGet")
            .collection("Ratings")
    }

    fun sendUserRating(user:String?,rating: Rating){
        rating.buyer = userAccount.value?.uid

        db.collection("Users")
            .document("user:$user")
            .collection("Ratings")
            .add(rating)
    }

    fun updateItemRating(itemID:String?){
        db.collection("Items")
            .document("item:$itemID")
            .update("rated", true)
    }

}