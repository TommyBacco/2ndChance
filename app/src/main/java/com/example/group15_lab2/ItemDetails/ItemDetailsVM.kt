package com.example.group15_lab2.ItemDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.JsonObjectRequest
import com.example.group15_lab2.DataClasses.Item
import com.example.group15_lab2.FirebaseRepository

class ItemDetailsVM : ViewModel() {

    private val item: MutableLiveData<Item> by lazy { MutableLiveData<Item>().also { loadItem() } }
    private val itemID:MutableLiveData<String> by lazy { MutableLiveData<String>().apply { value = null } }
    private val isInterested:MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>().apply { value = false } }

    fun getItemData(): LiveData<Item> {
        return item
    }

    fun setItemID(id:String?){
        itemID.value = id
    }

    fun getItemID():LiveData<String>{
        return itemID
    }

    fun getInterest():LiveData<Boolean>{
        return isInterested
    }

    private fun loadItem() {
        FirebaseRepository.getItemData(itemID.value).addSnapshotListener { doc, err ->
            if (err != null) {
                Log.d("ERROR-TAG", "Listen ShowProfile failed")
                item.value = Item()
            } else {
                val i = doc?.toObject(Item::class.java) ?: Item()
                item.value = i
                if(i.interestedUsers.contains(FirebaseRepository.getUserAccount().value?.uid)){
                    isInterested.value = true
                }
            }
        }
    }

    fun modifyInterest(): JsonObjectRequest? {

        var request:JsonObjectRequest? = null

        if(isInterested.value!!){
            FirebaseRepository.removeItemInterest(itemID.value ?: "")
            FirebaseRepository.unsubscribeFromTopic(itemID.value ?: "")
            isInterested.value = false
        } else {
            FirebaseRepository.addItemInterest(itemID.value ?: "")
            FirebaseRepository.subscribeToTopic(itemID.value ?: "")
            request = FirebaseRepository.getNotificationRequest(item.value ?: Item(),"newInterest")
            isInterested.value = true
        }

        return request
    }
}