package com.example.group15_lab2.ItemList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClasses.Item
import com.example.group15_lab2.FirebaseRepository

class ItemListVM : ViewModel() {
    private val items: MutableLiveData<List<Item>> by lazy { MutableLiveData<List<Item>>().also { loadItem() } }

    fun getMyItems(): LiveData<List<Item>> {
        return items
    }

    private fun loadItem() {

        FirebaseRepository.getMyItems().addSnapshotListener{ res, err ->
            if (err != null) {
                Log.d("ERROR-TAG", "Listen getMyItems failed")
                return@addSnapshotListener
            }
            val myItems : MutableList<Item> = mutableListOf()
            for(doc in res!!){
                val item = doc.toObject(Item::class.java)
                myItems.add(item)
            }
            items.value = myItems
        }

    }
}