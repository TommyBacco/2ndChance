package com.example.group15_lab2

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_item_details.*

class ItemDetailsFragment : Fragment() {

    private lateinit var itemShow: Item
    private val sharedPref: SharedPreferences by lazy { this.activity!!.getPreferences(Context.MODE_PRIVATE) }
    private var imageByteArray: ByteArray? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_details,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val jsonString:String? = sharedPref.getString(arguments?.getString("group15.lab2.KEY").toString(),null)
        if(jsonString != null) {
            itemShow = Gson().fromJson(jsonString, Item::class.java)
            populateTextView()
            populateImageView(itemShow.imageRotation)
        }
    }

    private fun populateTextView() {
        item_title.text = itemShow.title
        item_price.text = itemShow.price.toString()
        item_date.text = itemShow.expireDate
        item_category.text = itemShow.category
        item_sub_category.text = itemShow.subcategory
        item_location.text = itemShow.location
        item_delivery.text = itemShow.delivery
        item_description.text = itemShow.description

        val jsonStringImage: String? = sharedPref.getString(itemShow.getFile(), null)
        if (jsonStringImage != null) {
            itemShow = Gson().fromJson(jsonStringImage, Item::class.java)
        }
    }

    private fun populateImageView(rotation: Float){
        try{
            val byteArray = this.activity!!.openFileInput(itemShow.getFile()).readBytes()
            if(byteArray != null){
                imageByteArray = byteArray
                var imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                if(rotation!=0F)
                    imageBitmap=rotateImage(imageBitmap,rotation)
                item_image.setImageBitmap(imageBitmap)
            }
            else
                item_image.setImageResource(R.drawable.item_icon)
        } catch (exc:Exception){
            item_image.setImageResource(R.drawable.item_icon)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

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
            putString("group15.lab2.KEY", itemShow.getKey())
        }
        findNavController().navigate(R.id.action_itemDetailsFragment_to_itemEditFragment, bundle)
    }

}