package com.example.group15_lab2.BoughtItemsList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClasses.Item
import com.example.group15_lab2.FirebaseRepository

class BoughtItemsListVM : ViewModel() {
    private val items: MutableLiveData<List<Item>> by lazy { MutableLiveData<List<Item>>().also { loadItem() } }

    fun getBoughtItems(): LiveData<List<Item>> {
        return items
    }

    private fun loadItem() {

        FirebaseRepository.getBoughtItems().addSnapshotListener{ res, err ->
            if (err != null) {
                Log.d("ERROR-TAG", "Listen getBoughtItems failed")
                return@addSnapshotListener
            }
            val list : MutableList<Item> = mutableListOf()
            for(doc in res!!){
                val item = doc.toObject(Item::class.java)
                list.add(item)
            }
            items.value = list
        }

    }
}