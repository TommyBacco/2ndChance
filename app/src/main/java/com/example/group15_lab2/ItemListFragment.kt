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

        val myData = arrayListOf(
            Item("Item1",1,"30/04/2020"),
            Item("Item2",2,"29/04/2020"),
            Item("Item3",3,"28/04/2020"),
            Item("Item4",4,"27/04/2020"),
            Item("Item5",5,"26/04/2020"))
        myData[0].setPosition(0)
        myData[1].setPosition(1)
        myData[2].setPosition(2)
        myData[3].setPosition(3)
        myData[4].setPosition(4)
        val sharedPref: SharedPreferences by lazy { this.activity!!.getPreferences(Context.MODE_PRIVATE) }
        sharedPref.edit().putString(myData[0].getKey(), Gson().toJson(myData[0])).apply()
        sharedPref.edit().putString(myData[1].getKey(), Gson().toJson(myData[1])).apply()
        sharedPref.edit().putString(myData[2].getKey(), Gson().toJson(myData[2])).apply()
        sharedPref.edit().putString(myData[3].getKey(), Gson().toJson(myData[3])).apply()
        sharedPref.edit().putString(myData[4].getKey(), Gson().toJson(myData[4])).apply()

        recyclerView.layoutManager = LinearLayoutManager(context)
        val myAdapter = ItemAdapter(myData)
        recyclerView.adapter = myAdapter

        myAdapter.setOnItemClickListener(object : ItemAdapter.ClickListener{
            override fun onItemClick(position: Int) {
                val bundle = Bundle()
                with(bundle){
                    putString("group15.lab2.KEY", myData[position].getKey())
                }
                findNavController().navigate(R.id.action_itemListFragment_to_itemDetailsFragment, bundle)
            }

            override fun onItemEdit(position: Int) {
                Toast.makeText(context,"EDIT on ${myData[position].title}",Toast.LENGTH_SHORT).show()
                val bundle = Bundle()
                with(bundle){ putString("group15.lab2.KEY", myData[position].getKey()) }
                findNavController().navigate(R.id.action_itemListFragment_to_itemEditFragment, bundle)
            }
        })

    }
}