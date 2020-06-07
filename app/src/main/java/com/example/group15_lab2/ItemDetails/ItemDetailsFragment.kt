package com.example.group15_lab2.ItemDetails

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.android.volley.toolbox.Volley
import com.example.group15_lab2.*
import com.example.group15_lab2.MainActivity.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_item_details.*

class ItemDetailsFragment : Fragment() {

    private val myViewModel: ItemDetailsVM by viewModels()
    private val REQUEST_LOCATION_PERMISSION = 5000
    private val FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION

    private var itemID:String? = null
    private var isSoldToMe:Boolean = false
    private var itemOwner:String? = null
    private var isRated:Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_details,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = resources.getString(R.string.itemDetails_title)
        (activity as MainActivity).setToolbarTitle(title)

        if(savedInstanceState == null) {
            val id = arguments?.getString("group15.lab3.ITEM_ID")
            myViewModel.setItemID(id)
        }
        populateItemViews()

        myViewModel.getItemID().observe(viewLifecycleOwner, Observer {
            itemID = it
        })

        myViewModel.getInterest().observe(viewLifecycleOwner, Observer { fabStatus ->
            val fabImage:Int

            when {
                fabStatus.soldToMe -> {
                    message_interest.text = resources.getString(R.string.item_soldToUser)
                    message_interest.visibility = View.VISIBLE
                    fabImage = R.drawable.ic_rating
                    isSoldToMe = true
                }
                fabStatus.isInterested -> {
                    message_interest.text = resources.getString(R.string.item_userInterested)
                    message_interest.visibility = View.VISIBLE
                    fabImage = R.drawable.ic_bookmark_remove
                    isSoldToMe = false
                }
                else -> {
                    message_interest.visibility = View.GONE
                    fabImage = R.drawable.ic_bookmark_add
                    isSoldToMe = false
                }
            }

            fab_item_interested.setImageResource(fabImage)
        })

        fab_item_interested.setOnClickListener {

            if(isSoldToMe)
                handleRating()
            else{
                val request = myViewModel.modifyInterest()
                if(request != null){
                    Volley.newRequestQueue(context).add(request)
                    setSnackbar("addInterest")
                } else
                    setSnackbar("removeInterest")
            }
        }

        bn_show_interested.setOnClickListener {
            val bundle = bundleOf(Pair("group15.lab3.ITEM_ID",itemID))
           findNavController().navigate(R.id.action_nav_itemDetails_to_iterestedUserListFragment,bundle)
        }

        item_show_seller_profile.setOnClickListener {
            val bundle = bundleOf(Pair("group15.lab3.USER_ID",itemOwner))
            findNavController().navigate(R.id.action_nav_itemDetails_to_nav_profile,bundle)
        }

        item_show_position.setOnClickListener {
            checkPermission()
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
            isRated = i.rated
            itemOwner = i.ownerID

            val status = i.status
            var color = Color.DKGRAY

            if(status == "Sold")
                color = Color.RED
            if(status == "On sale")
                color = Color.parseColor("#00b600") //Green

            item_status.text = status
            item_status.setTextColor(color)

            Picasso.get()
                .load(i.imageURL.toUri())
                .fit()
                .centerInside()
                .error(R.drawable.item_icon)
                .into(item_image)

            if(i.ownerID != FirebaseRepository.getUserAccount().value?.uid){
                setHasOptionsMenu(false)
                bn_show_interested.visibility = View.GONE
                item_interested_users.visibility = View.GONE
                fab_item_interested.visibility = View.VISIBLE
                item_show_seller_profile.visibility = View.VISIBLE
            } else{
                setHasOptionsMenu(true)
                fab_item_interested.visibility = View.GONE
                item_show_seller_profile.visibility = View.GONE
                bn_show_interested.visibility = View.VISIBLE
                item_interested_users.visibility = View.VISIBLE
                val numOfInterest = i.interestedUsers.size
                val text = "${resources.getString(R.string.default_interestedUsers)} $numOfInterest"
                item_interested_users.text = text
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

    private fun handleRating(){
        if(isRated){
            setSnackbar("isRated")
        }
        else{
            val bundle =
                bundleOf(Pair("group15.lab3.ITEM_ID",itemID),
                    Pair("group15.lab3.ITEM_OWNER",itemOwner))
            findNavController().navigate(R.id.action_nav_itemDetails_to_ratingUserFragment,bundle)
        }
    }

    private fun setSnackbar(tipe:String){
        val text:String
        val background:Int
        when(tipe){
            "isRated" -> {
                text = resources.getString(R.string.itemDetals_snack1)
                background = R.color.longTitle
            }
            "addInterest" -> {
                text = resources.getString(R.string.itemDetals_snack2)
                background = R.color.editedItem
            }
            else -> {
                text = resources.getString(R.string.itemDetals_snack3)
                background = R.color.longTitle
            }
        }
        val snack: Snackbar = Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG)
        val tv: TextView = snack.view.findViewById(com.google.android.material.R.id.snackbar_text)
        tv.setTextColor(Color.WHITE)
        tv.typeface = Typeface.DEFAULT_BOLD
        snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(), background))
        snack.show()
    }


    private fun navigateToMap(){
        val location = myViewModel.getItemLocation()
        val userLocation = (activity as MainActivity).getUserLocation()
        val bundle = bundleOf(
            Pair("group15.lab4.LOCALITY",location?.locality),
            Pair("group15.lab4.LATITUDE",location?.latitude),
            Pair("group15.lab4.LONGITUDE",location?.longitude),
            Pair("group15.lab4.IS_ITEM",true),
            Pair("group15.lab4.USER_LOCALITY",userLocation?.locality),
            Pair("group15.lab4.USER_LATITUDE",userLocation?.latitude),
            Pair("group15.lab4.USER_LONGITUDE",userLocation?.longitude)
        )
        findNavController().navigate(R.id.action_nav_itemDetails_to_showMapFragment,bundle)
    }

    private fun checkPermission(){
        val permissions = arrayOf(FINE_LOCATION,COARSE_LOCATION)

        if (ContextCompat.checkSelfPermission(requireActivity(), FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireActivity(), COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(permissions,REQUEST_LOCATION_PERMISSION)
        else
            navigateToMap()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_LOCATION_PERMISSION ->{
                if(grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    navigateToMap()
                }
                else {
                    // permission denied, boo!
                    val text = resources.getString(R.string.permission_map)
                    Toast.makeText(
                        requireActivity(), text,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}