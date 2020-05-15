package com.example.group15_lab2.ItemDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClass.Item
import com.google.firebase.firestore.FirebaseFirestore

class ItemDetailsVM : ViewModel() {

    private val item: MutableLiveData<Item> by lazy { MutableLiveData<Item>().also { loadItem() } }
    private val db = FirebaseFirestore.getInstance()
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

        db.collection("Items")
            .document("item:${itemID.value}")
            .get()
            .addOnSuccessListener { res ->
                item.value = res.toObject(Item::class.java) ?: Item()
            }

        addListener()
    }

    private fun addListener() {

        db.collection("Items")
            .document("item:${itemID.value}")
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snap != null && snap.exists()) {
                    item.value = snap.toObject(Item::class.java)
                }
            }
    }


}