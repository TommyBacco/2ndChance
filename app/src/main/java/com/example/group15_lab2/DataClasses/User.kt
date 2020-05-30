package com.example.group15_lab2.DataClasses

data class User (
    var avatarURL:String = "No URL",
    var ID:String? = null,
    var fullName :String? = null,
    var nickname:String? = null,
    var email:String? = null,
    var location:String? = null,
    var address:String? = null,
    var telephone:String? = null,
    var itemsOfInterest:ArrayList<String> = ArrayList()
)
