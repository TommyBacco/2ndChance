package com.example.group15_lab2.InterestedUserList

import androidx.lifecycle.ViewModel
import com.example.group15_lab2.DataClass.User
import com.example.group15_lab2.FirebaseRepository
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class InterestedUserListVM : ViewModel() {

    private var itemID:String? = "No item id"

    fun setItemID(id:String?){
        itemID = id
    }

    fun getInterestedUsersOptions(): FirestoreRecyclerOptions<User> {
        return FirebaseRepository.getInterestedUsersOptions(itemID)
    }




}