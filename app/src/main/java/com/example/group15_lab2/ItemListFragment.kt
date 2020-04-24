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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_item_list.*

class ItemListFragment : Fragment() {

    //QUANDO SI EFFETTUA UNA NAVIGAZIONE, AL SUCCESSIVO FRAGMENT VERRA' TRASFERITO SOLAMENTE L'ID DEL CORRISPETTIVO ITEM
    private val sharedPref: SharedPreferences by lazy { this.activity!!.getPreferences(Context.MODE_PRIVATE) }
    private var itemLastID: Int = -1
    private lateinit var items: ArrayList<Item>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO remove this initalization of items
        items = arrayListOf(
            Item("Item0","0.99","30/04/2020"),
            Item("Item1","1.00","04/05/2020"),
            Item("Item2","2.50","28/04/2020"),
            Item("Item3","3.00","15/06/2020"),
            Item("Item4","4.70","01/10/2020"),
            Item("Item5","5.20","30/08/2020"),
            Item("Item6","6.10","11/05/2020"))
        items.forEach{
            itemLastID++
            val jsonString = KEY_ItemDetails + itemLastID.toString()
            val item = Gson().toJson(it)
            sharedPref.edit().putString(jsonString, Gson().toJson(item)).apply()
        }
        sharedPref.edit().putInt(KEY_ItemLastID, itemLastID).apply()
        emptyMessage.visibility = View.INVISIBLE
        //items.clear()

        //populateItemList()

        recyclerView.layoutManager = LinearLayoutManager(context)
        val myAdapter = ItemAdapter(items)
        recyclerView.adapter = myAdapter

        myAdapter.setOnItemClickListener(object : ItemAdapter.ClickListener{
            override fun onItemClick(position: Int) {
                val bundle = Bundle()
                with(bundle){
                    putInt("group15.lab2.ITEM_ID", position)
                }
                findNavController().navigate(R.id.action_itemListFragment_to_itemDetailsFragment, bundle)
            }

            override fun onItemEdit(position: Int) {
                Toast.makeText(context,"EDIT on ${items[position].title}",Toast.LENGTH_SHORT).show()
                val bundle = Bundle()
                with(bundle){
                    putInt("group15.lab2.ITEM_ID", position)
                    putBoolean("group15.lab2.NEW", false)
                }
                findNavController().navigate(R.id.action_itemListFragment_to_itemEditFragment, bundle)
            }
        })

        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab_new_item)
        fab?.show()
        fab?.setOnClickListener {
            sharedPref.edit().putInt(KEY_ItemLastID, itemLastID).apply()
            val bundle = Bundle()
            with(bundle){
                putInt("group15.lab2.ITEM_ID", itemLastID)
                putBoolean("group15.lab2.NEW", true)
            }
            findNavController().navigate(R.id.action_itemListFragment_to_itemEditFragment, bundle)
            //TODO bisogna aggiornare la lista
        }
    }

    private fun populateItemList() {
        itemLastID = sharedPref.getInt(KEY_ItemLastID, -1)
        if (itemLastID != -1) { //Popolamento interfaccia con item da 0 a itemLastID
            emptyMessage.visibility = View.INVISIBLE
            for (i in 0..itemLastID) {
                //TODO non so cosa ci sia di sbagliato
                val jsonString: String? = sharedPref.getString(KEY_ItemDetails + i.toString(), null)
                if (jsonString != null) {
                    val item: Item = Gson().fromJson(jsonString, Item::class.java)
                    items.add(item)
                }
            }
        } else
            emptyMessage.visibility = View.VISIBLE
    }

}