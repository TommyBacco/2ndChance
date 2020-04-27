package com.example.group15_lab2

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_item_details.*

class ItemDetailsFragment : Fragment() {

    private var itemID: Int = 0
    private val sharedPref: SharedPreferences by lazy { this.activity!!.getPreferences(Context.MODE_PRIVATE) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_item_details,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState == null)
            itemID = arguments?.getInt("group15.lab2.ITEM_ID") ?: -1
        else
            itemID = savedInstanceState.getInt("group15.lab2.ITEM_ID",-1)

        if(itemID != -1){
            val key:String = KEY_ItemDetails + itemID
            val jsonString: String? = sharedPref.getString(key, null)
            if (jsonString != null) {
                val item: Item = Gson().fromJson(jsonString, Item::class.java)
                populateViews(item)
            }
        }
    }

    private fun populateViews(itemShow: Item) {
        item_title.text = itemShow.title
        item_price.text = itemShow.price
        item_date.text = itemShow.expireDate
        item_category.text = itemShow.category
        item_sub_category.text = itemShow.subcategory
        item_location.text = itemShow.location
        item_delivery.text = itemShow.delivery
        item_description.text = itemShow.description
        populateImageView(itemShow.imageRotation)
    }

    private fun populateImageView(rotation: Float){
        try{
            val fileName = FILE_ItemImage + itemID
            val byteArray: ByteArray? = context!!.openFileInput(fileName).readBytes()
            if(byteArray != null){
                var imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                if(rotation != 0F)
                    imageBitmap = rotateImage(imageBitmap, rotation)
                item_image.setImageBitmap(imageBitmap)
            }
            else
                item_image.setImageResource(R.drawable.item_icon)
        } catch (exc:Exception){
            item_image.setImageResource(R.drawable.item_icon)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_pencil, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.pencil_button -> {
                editItem()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun editItem(){
        val bundle = bundleOf(Pair("group15.lab2.ITEM_ID",itemID))
        findNavController().navigate(R.id.action_itemDetailsFragment_to_itemEditFragment, bundle)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("group15.lab2.ITEM_ID",itemID)
    }
}