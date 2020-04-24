package com.example.group15_lab2

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_item_details.*

class ItemDetailsFragment : Fragment() {

    private var itemLastID: Int = 0
    private val sharedPref: SharedPreferences by lazy { this.activity!!.getPreferences(Context.MODE_PRIVATE) }
    private var imageByteArray: ByteArray? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_item_details,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemLastID = sharedPref.getInt("group15.lab2.ITEM_ID", -1)
        if(itemLastID != -1){
            val jsonString = KEY_ItemDetails + itemLastID.toString()
            val itemShow = Gson().fromJson(jsonString, Item::class.java)
            populateTextView(itemShow)
            populateImageView(itemShow.imageRotation)
        }

        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab_new_item)
        fab?.show()
        fab?.setOnClickListener {
            sharedPref.edit().putInt(KEY_ItemLastID, itemLastID).apply()
            val bundle = Bundle()
            with(bundle){
                putInt("group15.lab2.ITEM_ID", itemLastID)
                putBoolean("group15.lab2.NEW", true)
            }
            findNavController().navigate(R.id.action_itemDetailsFragment_to_itemEditFragment, bundle)
            //TODO DOPO L'INSERIMENTO DI UN NUOVO ITEM NECESSARIO AGGIORNARE LA LISTA!
        }
    }

    private fun populateTextView(itemShow: Item) {
        item_title.text = itemShow.title
        item_price.text = itemShow.price
        item_date.text = itemShow.expireDate
        item_category.text = itemShow.category
        item_sub_category.text = itemShow.subcategory
        item_location.text = itemShow.location
        item_delivery.text = itemShow.delivery
        item_description.text = itemShow.description
    }

    private fun populateImageView(rotation: Float){
        try{
            val byteArray: ByteArray? = context!!.openFileInput(KEY_ItemDetails + itemLastID.toString()).readBytes()
            if(byteArray != null){
                imageByteArray = byteArray
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_pencil, menu)
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
        val bundle = Bundle()
        with(bundle){
            putInt("group15.lab2.ITEM_ID", itemLastID)
        }
        findNavController().navigate(R.id.action_itemDetailsFragment_to_itemEditFragment, bundle)
    }
}