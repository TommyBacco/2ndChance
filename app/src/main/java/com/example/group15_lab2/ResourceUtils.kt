package com.example.group15_lab2

import android.graphics.Bitmap
import android.graphics.Matrix

val KEY_UserProfile = "profile"
val FILE_UserProfile_Avatar = "User_Image_Avatar"

data class Profile(val fullName:String?,val nickname:String?, val email:String?, val location:String?,
                   val address:String?,val telephone:String?,val rotation:Float = 0F )

data class Item(
    var title:String,
    var price:Int,
    var expireDate: String,
    var category: String? = "Electronics",
    var subcategory: String? = "Phone",
    var location: String? = "Italy",
    var delivery: String? = "Standard",
    var description: String? = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
    var imageRotation: Float = 0F
) {
    private var KEY_ItemDetails = "Item_Details_"
    private var FILE_ItemImage = "Item_Image_"

    fun setPosition(position: Int){
        KEY_ItemDetails += position
        FILE_ItemImage += position
    }

    fun getKey() = KEY_ItemDetails

    fun getFile() = FILE_ItemImage
}

fun rotateImage(startBitmap: Bitmap, degree:Float): Bitmap {
    var matrix =  Matrix()
    matrix.postRotate(degree)
    return Bitmap.createBitmap(startBitmap, 0, 0, startBitmap.width, startBitmap.height,
        matrix, true)
}