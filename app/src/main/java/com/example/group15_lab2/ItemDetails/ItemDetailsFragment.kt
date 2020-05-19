package com.example.group15_lab2.ItemDetails

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.group15_lab2.*
import com.example.group15_lab2.DataClass.User
import com.example.group15_lab2.MainActivity.MainActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_item_details.*
import kotlinx.android.synthetic.main.fragment_item_list.*

class ItemDetailsFragment : Fragment() {

    private val myViewModel: ItemDetailsVM by viewModels()
    private var itemID:String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_item_details,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState == null) {
            val id = arguments?.getString("group15.lab3.ITEM_ID")
            myViewModel.setItemID(id)
        }
        populateItemViews()

        myViewModel.getItemID().observe(viewLifecycleOwner, Observer {
            itemID = it
        })

        myViewModel.getInterest().observe(viewLifecycleOwner, Observer { interested ->
            message_interest.visibility =
                if(interested) View.VISIBLE
                else View.GONE
        })

        fab_item_interested.setOnClickListener {
            myViewModel.modifyInterest()
        }


        bn_show_interested.setOnClickListener {
            val bundle = bundleOf(Pair("group15.lab3.ITEM_ID",itemID))
           findNavController().navigate(R.id.action_nav_itemDetails_to_iterestedUserListFragment,bundle)
        }

    }

    private fun populateItemViews() {
        myViewModel.getItemData().observe(viewLifecycleOwner, Observer { i ->
            item_title.text = i.title
            item_price.text = i.price
            item_date.text = i.expireDate
            item_category.text = i.category
            item_sub_category.text = i.subcategory
            item_location.text = i.location
            item_delivery.text = i.deliveryType
            item_description.text = i.description
            currency.text = i.currency

            Picasso.get()
                .load(i.imageURL.toUri())
                .fit()
                .centerInside()
                .error(R.drawable.item_icon)
                .into(item_image)

            if(i.ownerID != FirebaseRepository.getUserAccount().value?.uid){
                setHasOptionsMenu(false)
                fab_item_interested.visibility = View.VISIBLE
                bn_show_interested.visibility = View.GONE
            } else{
                fab_item_interested.visibility = View.GONE
                bn_show_interested.visibility = View.VISIBLE
            }
        })

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
        val bundle = bundleOf(Pair("group15.lab3.ITEM_ID",itemID))
        findNavController().navigate(R.id.action_itemDetailsFragment_to_itemEditFragment, bundle)
    }

}