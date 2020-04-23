package com.example.group15_lab2

import android.graphics.Bitmap
import android.graphics.Matrix

val DEFAULT_IMAGE_PROFILE = "Default_Image_Profile"
val DEFAULT_IMAGE_ITEM = "Default_Image_Item"

val KEY_UserProfile = "Profile_Details"
val FILE_UserProfile_Avatar = "User_Image_Avatar"

data class Profile(
    val fullName:String?,
    val nickname:String?,
    val email:String?,
    val location:String?,
    val address:String?,
    val telephone:String?,
    val rotation:Float = 0F
)

data class Item(
    var title:String,
    var price:Int,
    var expireDate: String,
    var category: String? = "Category",
    var subcategory: String? = "Sub-category",
    var location: String? = "Location",
    var delivery: String? = "Delivery type",
    //var description: String? = "Description",
    var description: String? = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut semper viverra nunc ut aliquet. Vestibulum fermentum ex at ante mollis venenatis. Fusce non neque et velit aliquam placerat vitae at metus. Donec ac pulvinar arcu. Maecenas malesuada dui sem, at fermentum leo mattis vel. Ut consequat nec lectus in dictum. In lacinia risus in ex fringilla, sed bibendum felis suscipit. Suspendisse rhoncus libero ipsum, eu pharetra nisl laoreet ut.",
    var imageRotation: Float = 0F
){
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