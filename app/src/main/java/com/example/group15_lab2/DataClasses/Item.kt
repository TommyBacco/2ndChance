package com.example.group15_lab2.DataClasses

data class Item (
    var imageURL:String = "No URL",
    var ID:String? = null,
    var ownerID:String? = null,
    var title:String? = null,
    var price:String? = null,
    var currency:String? = "€",
    var expireDate: String? = null,
    var category: String? = null,
    var subcategory: String? = null,
    var location: String? = null,
    var deliveryType: String? = null,
    var description: String? = null,
    var interestedUsers:ArrayList<String> = ArrayList(),
    var status:String? = "On sale",
    var soldTo:String? = null,
    var rated:Boolean = false
)