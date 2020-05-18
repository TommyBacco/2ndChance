package com.example.group15_lab2.OnSaleList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClass.Item
import com.example.group15_lab2.FirebaseRepository

class OnSaleListVM: ViewModel() {

    private val items: MutableLiveData<List<Item>> by lazy { MutableLiveData<List<Item>>().also { loadItem() } }

    fun getAdvertisements(): MutableLiveData<List<Item>> {
        return items
    }


    private fun loadItem() {
        FirebaseRepository.getAdvertisements().addSnapshotListener { res, err ->
            if (err != null) {
                Log.d("ERROR-TAG", "Listen getAdvertisements failed")
                return@addSnapshotListener
            }
            val savedAddressList : MutableList<Item> = mutableListOf()
            for(doc in res!!){
                val item = doc.toObject(Item::class.java)
                if(item.ownerID != FirebaseRepository.getUserAccount().value?.uid)
                savedAddressList.add(item)
            }
            if(savedAddressList.isNotEmpty())
                items.value = savedAddressList
        }
    }

}