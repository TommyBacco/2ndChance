package com.example.group15_lab2.ItemDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.JsonObjectRequest
import com.example.group15_lab2.DataClasses.Item
import com.example.group15_lab2.DataClasses.ItemFABStatus
import com.example.group15_lab2.DataClasses.LocationPosition
import com.example.group15_lab2.FirebaseRepository

class ItemDetailsVM : ViewModel() {

    private val item: MutableLiveData<Item> by lazy { MutableLiveData<Item>().also { loadItem() } }
    private val itemID:MutableLiveData<String> by lazy { MutableLiveData<String>().apply { value = null } }
    private val fabStatus:MutableLiveData<ItemFABStatus> by lazy { MutableLiveData<ItemFABStatus>().apply { value = ItemFABStatus() } }

    fun getItemData(): LiveData<Item> {
        return item
    }

    fun setItemID(id:String?){
        itemID.value = id
    }

    fun getItemID():LiveData<String>{
        return itemID
    }

    fun getInterest():LiveData<ItemFABStatus>{
        return fabStatus
    }

    fun getItemLocation(): LocationPosition? = item.value?.itemLocation

    private fun loadItem() {
        FirebaseRepository.getItemData(itemID.value).addSnapshotListener { doc, err ->
            if (err != null) {
                Log.d("ERROR-TAG", "Listen ShowProfile failed")
                item.value = Item()
            } else {
                val i = doc?.toObject(Item::class.java) ?: Item()
                item.value = i
                if(i.soldTo == FirebaseRepository.getUserAccount().value?.uid)
                    fabStatus.value = ItemFABStatus(isInterested = false, soldToMe = true)
                else if(i.interestedUsers.contains(FirebaseRepository.getUserAccount().value?.uid))
                    fabStatus.value = ItemFABStatus(isInterested = true, soldToMe = false)
                else
                    fabStatus.value = ItemFABStatus()
            }
        }
    }

    fun modifyInterest(): JsonObjectRequest? {

        var request:JsonObjectRequest? = null

        if(fabStatus.value?.isInterested!!){
            FirebaseRepository.removeItemInterest(itemID.value ?: "")
            FirebaseRepository.unsubscribeFromTopic(itemID.value ?: "")
            fabStatus.value = ItemFABStatus()
        } else {
            FirebaseRepository.addItemInterest(itemID.value ?: "")
            FirebaseRepository.subscribeToTopic(itemID.value ?: "")
            request = FirebaseRepository.getNotificationRequest(item.value ?: Item(),"newInterest")
            fabStatus.value = ItemFABStatus(isInterested = true, soldToMe = false)
        }

        return request
    }
}