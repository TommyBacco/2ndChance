package com.example.group15_lab2.ItemDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClass.Item
import com.example.group15_lab2.FirebaseRepository

class ItemDetailsVM : ViewModel() {

    private val item: MutableLiveData<Item> by lazy { MutableLiveData<Item>().also { loadItem() } }
    private val itemID:MutableLiveData<String> by lazy { MutableLiveData<String>().apply { value = null } }

    fun getItemData(): LiveData<Item> {
        return item
    }

    fun setItemID(id:String?){
        itemID.value = id
    }

    fun getItemID():LiveData<String>{
        return itemID
    }

    private fun loadItem() {
        FirebaseRepository.getItemData(itemID.value).addSnapshotListener { doc, err ->
            if (err != null) {
                Log.d("ERROR-TAG", "Listen ShowProfile failed")
                item.value = Item()
            } else {
                item.value = doc?.toObject(Item::class.java) ?: Item()
            }
        }
    }
}