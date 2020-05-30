package com.example.group15_lab2.OnSaleList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClasses.FilterParams
import com.example.group15_lab2.DataClasses.Item
import com.example.group15_lab2.FirebaseRepository

class OnSaleListVM: ViewModel() {

    private val items: MutableLiveData<List<Item>> by lazy { MutableLiveData<List<Item>>().also { loadItems() } }
    private val filterParams:MutableLiveData<FilterParams> by lazy { MutableLiveData<FilterParams>().apply { value = FilterParams() }}

    fun getAdvertisements(): LiveData<List<Item>> = items

    private fun loadItems() {
        FirebaseRepository.getAdvertisements().addSnapshotListener { res, err ->
            if (err != null) {
                Log.d("ERROR-TAG", "Listen getAdvertisements failed")
                return@addSnapshotListener
            }
            val list : MutableList<Item> = mutableListOf()
            for(doc in res!!){
                val item = doc.toObject(Item::class.java)
                if(item.ownerID != FirebaseRepository.getUserAccount().value?.uid)
                list.add(item)
            }
                items.value = list
        }
    }

    fun getFilterParams():LiveData<FilterParams> = filterParams

    fun setFilterParams(category:String,from:Int?,to:Int?){
        val params = FilterParams(category,from,to)
        filterParams.value = params
    }

}