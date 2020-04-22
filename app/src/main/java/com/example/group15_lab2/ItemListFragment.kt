package com.example.group15_lab2

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_item_list.*

class ItemListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myData = arrayListOf<Item>(
            Item("Item1",1,"30/04/2010"),
            Item("Item2",2,"30/04/2010"))
        myData[0].setPosition(0)
        myData[1].setPosition(1)
        val sharedPref: SharedPreferences by lazy { this.activity!!.getPreferences(Context.MODE_PRIVATE) }
        sharedPref.edit().putString(myData[0].getKey(), Gson().toJson(myData[0])).apply()
        sharedPref.edit().putString(myData[1].getKey(), Gson().toJson(myData[1])).apply()

        recyclerView.layoutManager = LinearLayoutManager(context)
        var myAdapter = ItemAdapter(myData)
        recyclerView.adapter = myAdapter

        myAdapter.setOnItemClickListener(object : ItemAdapter.ClickListener{
            override fun onItemClick(position: Int) {
                Toast.makeText(context,"You have pressed on ${myData[position].title}",Toast.LENGTH_SHORT).show()
                val bundle = Bundle()
                with(bundle){
                    putString("group15.lab2.KEY", myData[position].getKey())
                }
                findNavController().navigate(R.id.action_itemListFragment_to_itemDetailsFragment, bundle)
            }

            override fun onItemEdit(position: Int) {
                Toast.makeText(context,"EDIT on ${myData[position].title}",Toast.LENGTH_SHORT).show()
                val bundle = Bundle()
                with(bundle){
                    putString("group15.lab2.KEY", myData[position].getKey())
                }
                findNavController().navigate(R.id.action_itemListFragment_to_itemEditFragment, bundle)
            }


        })

    }
}