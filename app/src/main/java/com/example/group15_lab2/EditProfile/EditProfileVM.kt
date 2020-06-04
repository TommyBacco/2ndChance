package com.example.group15_lab2.EditProfile

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClasses.LocationPosition
import com.example.group15_lab2.DataClasses.User
import com.example.group15_lab2.FirebaseRepository
import java.io.ByteArrayOutputStream

class EditProfileVM : ViewModel() {

    private val user : MutableLiveData<User> by lazy {MutableLiveData<User>().also { loadUser() }}
    private val image : MutableLiveData<Bitmap> by lazy {MutableLiveData<Bitmap>()}
    private var isOld = true
    fun isImageOld() = isOld

    fun getUserData(): LiveData<User> {
        return user
    }

    fun getUserLocation(): LiveData<LocationPosition>{
        return FirebaseRepository.getUserPosition()
    }

    private fun loadUser() {
        FirebaseRepository.getUserData()
            .get()
            .addOnSuccessListener { res ->
                user.value = res.toObject(User::class.java) ?: User()
                FirebaseRepository.setUserPosition(user.value?.userLocation ?: LocationPosition())
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
            "location" -> user.value?.location = value
            "telephone" -> user.value?.telephone=value
        }
    }

    fun editUserLocation(location:String){
       FirebaseRepository.setUserPosition(LocationPosition(location))
    }

    fun saveData(){
        val stream = ByteArrayOutputStream()
        image.value?.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        FirebaseRepository.saveUserData(user.value,stream.toByteArray())
    }

}