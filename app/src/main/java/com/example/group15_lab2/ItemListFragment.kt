package com.example.group15_lab2

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_item_list.*

class ItemListFragment : Fragment() {

    //QUANDO SI EFFETTUA UNA NAVIGAZIONE, AL SUCCESSIVO FRAGMENT VERRA' TRASFERITO SOLAMENTE L'ID DEL CORRISPETTIVO ITEM
    private val sharedPref: SharedPreferences by lazy { this.activity!!.getPreferences(Context.MODE_PRIVATE) }
    private var itemLastID: Int = -1
    private var items = ArrayList<Item>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateItemList()

        recyclerView.layoutManager = GridLayoutManager(context,2, GridLayoutManager.VERTICAL, false)
        val myAdapter = ItemAdapter(items)
        recyclerView.adapter = myAdapter


        myAdapter.setOnItemClickListener(object : ItemAdapter.ClickListener{
            override fun onItemClick(position: Int) {
                Toast.makeText(context,"SHOW on ${items[position].title}",Toast.LENGTH_SHORT).show()
                val bundle = bundleOf(Pair("group15.lab2.ITEM_ID",position))
                findNavController().navigate(R.id.action_itemListFragment_to_itemDetailsFragment,bundle)
            }

            override fun onItemEdit(position: Int) {
                Toast.makeText(context,"EDIT on ${items[position].title}",Toast.LENGTH_SHORT).show()
                val bundle = bundleOf(Pair("group15.lab2.ITEM_ID",position))
                findNavController().navigate(R.id.action_itemListFragment_to_itemEditFragment,bundle)
            }
        })

        fab_new_item.setOnClickListener {
            Toast.makeText(context,"ADD item",Toast.LENGTH_SHORT).show()
            val bundle = Bundle()
            with(bundle){
                putInt("group15.lab2.ITEM_ID",itemLastID)
                putBoolean("group15.lab2.NEW_ITEM",true)
            }
            findNavController().navigate(R.id.action_itemListFragment_to_itemEditFragment,bundle)
        }

    }

    private fun populateItemList() {

        itemLastID = sharedPref.getInt(KEY_ItemLastID,-1)
        items.clear()

        if (itemLastID != -1) {
            emptyMessage.visibility = View.INVISIBLE
            for (i in 0..itemLastID) {
                val key:String = KEY_ItemDetails + i
                val jsonString: String? = sharedPref.getString(key, null)
                if (jsonString != null) {
                    val item: Item = Gson().fromJson(jsonString, Item::class.java)
                    val imageBitmap = setItemImage(i,item.imageRotation)
                    item.image = imageBitmap
                    items.add(item)
                }
            }
        }
    }

    private fun setItemImage(id:Int, rotation:Float) : Bitmap?{
        val fileName = FILE_ItemImage + id

        return try{
            val byteArray = activity!!.openFileInput(fileName).readBytes()
            var imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            if(rotation!=0F)
                imageBitmap = rotateImage(imageBitmap, rotation)
            imageBitmap
        } catch (exc:Exception){
            null
        }
    }

}