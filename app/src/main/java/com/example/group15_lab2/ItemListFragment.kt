package com.example.group15_lab2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_item_list.*

class ItemListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myData = arrayListOf<User>(User("User1","Sur1"),
            User("User2","Sur2"),
            User("User3","Sur3"),
            User("User4","Sur4"),
            User("User5","Sur5"),
            User("User6","Sur6"),
            User("User7","Sur7"))
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = UserAdapter(myData)
    }
}