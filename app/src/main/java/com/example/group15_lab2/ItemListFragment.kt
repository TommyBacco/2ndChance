package com.example.group15_lab2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        val myData = arrayListOf<Item>(Item(R.drawable.item_icon,"Item1","Much€"),
            Item(R.drawable.item_icon,"Item2","MuchMore€€€"))
        recyclerView.layoutManager = LinearLayoutManager(context)
        var myAdapter = ItemAdapter(myData)
        recyclerView.adapter = myAdapter
        myAdapter.setOnItemClickListener(object : ItemAdapter.ClickListener{
            override fun onItemClick(position: Int) {
                Toast.makeText(context,"You have pressed item at position=$position",Toast.LENGTH_SHORT).show()
            }

            override fun onItemEdit(position: Int) {
                Toast.makeText(context,"EDIT at position=$position",Toast.LENGTH_SHORT).show()
            }


        })

    }
}