package com.example.group15_lab2.EditProfile

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClass.User
import com.example.group15_lab2.Repository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class EditProfileVM : ViewModel() {

    private val user : MutableLiveData<User> by lazy {MutableLiveData<User>().also { loadUser() }}
    private val db = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().getReference()
    private val image : MutableLiveData<Bitmap> by lazy {MutableLiveData<Bitmap>()}
    private var isOld = true
    fun isImageOld() = isOld

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
    }

    fun getImage() : LiveData<Bitmap> {
        return image
    }

    fun setImage(img:Bitmap?){
        if(img!=null){
            isOld=false
            image.value = img
        }
    }

    fun rotateImage() {
        var matrix =  Matrix()
        matrix.postRotate(90F)
        var startBitmap = image.value
        if(startBitmap != null)
            image.value = Bitmap.createBitmap(startBitmap, 0, 0, startBitmap.width, startBitmap.height, matrix, true)
    }

    fun editData(param:String, value:String){
        when(param){
            "fullName" -> user.value?.fullName=value
            "nickname" -> user.value?.nickname=value
            "email" -> user.value?.email=value
            "address" -> user.value?.address=value
            "location" -> user.value?.location=value
            "telephone" -> user.value?.telephone=value
        }
    }

    fun saveData(){
        if(image.value != null)
            saveImage()
        else
            saveUser()
    }

    private fun saveImage() {
        val byteArray = ByteArrayOutputStream()
        image.value?.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)

        val imageRef = storageRef.child("UsersImages/user:"+Repository.userID?.uid)
        val uploadTask = imageRef.putBytes(byteArray.toByteArray())

        uploadTask.addOnSuccessListener {
            val downloadUrl = imageRef.downloadUrl
            downloadUrl.addOnSuccessListener {
                user.value?.avatarURL = it.toString()
                saveUser()
            }
        }
            .addOnFailureListener {
                //TODO GESTIRE CASO ERRORE
            }
    }

    private fun saveUser(){
        user.value?.ID = Repository.userID?.uid

        db.collection("Users")
            .document("user:"+Repository.userID?.uid)
            .set(user.value ?: User())
    }
}