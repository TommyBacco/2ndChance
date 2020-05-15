package com.example.group15_lab2.ItemEdit

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClass.Item
import com.example.group15_lab2.Repository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class ItemEditVM : ViewModel() {
    private val item : MutableLiveData<Item> by lazy { MutableLiveData<Item>().also { loadItem() }}
    private val db = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference
    private val itemID:MutableLiveData<String> by lazy { MutableLiveData<String>().apply { value = null } }
    private val image : MutableLiveData<Bitmap> by lazy { MutableLiveData<Bitmap>() }
    private var isOld = true
    fun isImageOld() = isOld
    private var newItem = false
    fun isNewItem() = newItem

    fun getItemData(): LiveData<Item> {
        return item
    }

    fun setItemID(id:String?){
        itemID.value = id
    }

    fun getItemID():LiveData<String>{
        return itemID
    }

    private fun loadItem() {

        db.collection("Items")
            .document("item:${itemID.value}")
            .get()
            .addOnSuccessListener { res ->
                item.value = res.toObject(Item::class.java) ?: Item()

                if(itemID.value == null){
                    itemID.value = UUID.randomUUID().toString()
                    item.value?.ID = itemID.value
                    newItem = true
                }
            }
    }

    fun getImage() : LiveData<Bitmap> {
        return image
    }

    fun setImage(img: Bitmap?){
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

    fun editItemData(param:String, value:String?){
        when(param){
            "title" -> item.value?.title=value
            "category" -> item.value?.category=value
            "subcategory" -> item.value?.subcategory=value
            "price" -> item.value?.price=value
            "currency" -> item.value?.currency=value
            "expireData" -> item.value?.expireDate = value
            "location" -> item.value?.location=value
            "deliveryType" -> item.value?.deliveryType=value
            "description" -> item.value?.description=value
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

        val imageRef = storageRef.child("ItemImages/item:"+itemID.value)
        val uploadTask = imageRef.putBytes(byteArray.toByteArray())

        uploadTask.addOnSuccessListener {
            val downloadUrl = imageRef.downloadUrl
            downloadUrl.addOnSuccessListener {
                item.value?.imageURL = it.toString()
                saveUser()
            }
        }
            .addOnFailureListener {
                //TODO GESTIRE CASO ERRORE
            }
    }

    private fun saveUser(){
        item.value?.ownerID = Repository.userID?.uid

        db.collection("Items")
            .document("item:"+itemID.value)
            .set(item.value ?: Item())
    }
}