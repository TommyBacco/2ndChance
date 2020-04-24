package com.example.group15_lab2

import android.graphics.Bitmap
import android.graphics.Matrix

const val KEY_UserProfile = "Profile_Details"
const val FILE_UserProfile_Avatar = "User_Image_Avatar"
const val KEY_ItemLastID = "itemLastID"
const val KEY_ItemDetails = "Item_Details_"
const val FILE_ItemImage = "Item_Image_"

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
    var price:String,
    var expireDate: String,
    var category: String? = "Category",
    var subcategory: String? = "Sub-category",
    var location: String? = "Location",
    var delivery: String? = "Delivery type",
    //var description: String? = "Description",
    var description: String? = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
            "Ut semper viverra nunc ut aliquet. Vestibulum fermentum ex at ante mollis " +
            "venenatis. Fusce non neque et velit aliquam placerat vitae at metus. Donec ac " +
            "pulvinar arcu. Maecenas malesuada dui sem, at fermentum leo mattis vel. Ut " +
            "consequat nec lectus in dictum. In lacinia risus in ex fringilla, sed bibendum" +
            " felis suscipit. Suspendisse rhoncus libero ipsum, eu pharetra nisl laoreet ut.",
    var imageRotation: Float = 0F
)

fun rotateImage(startBitmap: Bitmap, degree:Float): Bitmap {
    var matrix =  Matrix()
    matrix.postRotate(degree)
    return Bitmap.createBitmap(startBitmap, 0, 0, startBitmap.width, startBitmap.height,
        matrix, true)
}