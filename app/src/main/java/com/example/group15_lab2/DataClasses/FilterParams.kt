package com.example.group15_lab2.DataClasses

data class FilterParams (
    var category:String = "ALL",
    var from_price:Int? = null,
    var to_price:Int? = null
)