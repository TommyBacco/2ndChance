package com.example.group15_lab2

import android.graphics.Bitmap
import android.graphics.Matrix

val KEY_UserProfile = "profile"
val FILE_UserProfile_Avatar = "User_Image_Avatar"

data class Profile(val fullName:String?,val nickname:String?, val email:String?, val location:String?,
                   val address:String?,val telephone:String?,val rotation:Float=0F )

fun rotateImage(startBitmap: Bitmap, degree:Float): Bitmap {
    var matrix =  Matrix()
    matrix.postRotate(degree)
    return Bitmap.createBitmap(startBitmap, 0, 0, startBitmap.width, startBitmap.height,
        matrix, true)
}