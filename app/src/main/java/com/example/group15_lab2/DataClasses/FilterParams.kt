package com.example.group15_lab2.DataClasses

data class FilterParams (
    var category:String = "ALL",
    var from_price:Float? = null,
    var to_price:Float? = null
)