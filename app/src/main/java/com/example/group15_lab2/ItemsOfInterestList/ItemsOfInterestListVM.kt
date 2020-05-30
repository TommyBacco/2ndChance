package com.example.group15_lab2.ItemsOfInterestList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClasses.Item
import com.example.group15_lab2.FirebaseRepository

class ItemsOfInterestListVM : ViewModel() {
    private val items: MutableLiveData<List<Item>> by lazy { MutableLiveData<List<Item>>().also { loadItems() } }

    fun getItemsOfInterest(): LiveData<List<Item>> {
        return items
    }

    private fun loadItems() {
        FirebaseRepository.getItemsOfInterest().addSnapshotListener { res, err ->
            if (err != null) {
                Log.d("ERROR-TAG", "Listen getItemsOfInterest failed")
                return@addSnapshotListener
            }
            val savedAddressList : MutableList<Item> = mutableListOf()
            for(doc in res!!){
                val item = doc.toObject(Item::class.java)
                if(item.soldTo != FirebaseRepository.getUserAccount().value?.uid)
                    savedAddressList.add(item)
            }
            items.value = savedAddressList
        }
    }
}