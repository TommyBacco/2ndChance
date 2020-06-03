package com.example.group15_lab2.SetMapPosition

import android.graphics.Color
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.group15_lab2.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_set_map_position.*

class SetMapPositionFragment:Fragment() {

    private lateinit var map:GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private val DEFAULT_ZOOM = 12f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_set_map_position, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment = childFragmentManager.findFragmentById(R.id.search_map) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it
            map.isMyLocationEnabled = true
            map.uiSettings.isMapToolbarEnabled = false
            map.uiSettings?.isMyLocationButtonEnabled = false
        }

        input_search.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                geolocate()
            }
            return@setOnEditorActionListener false
        }

        fab_find_my_location.setOnClickListener {
            getDeviceLocation()
        }

        fab_save.setOnClickListener {
            setSnackbar("save-result")
            findNavController().popBackStack()
        }

    }

    private fun geolocate(){
        val searchString = input_search.text.toString()

        val geocoder = Geocoder(requireContext())
        var list:List<Address> = ArrayList()

        try {
            list = geocoder.getFromLocationName(searchString,1)
        } catch (exc:Exception){
            Log.d("ERR","Error while geolocate ${exc.message}")
            setSnackbar("err-geo")
        }

        if(list.isNotEmpty()){
            val address = list[0]
            moveCamera(LatLng(address.latitude,address.longitude),DEFAULT_ZOOM,address.getAddressLine(0))
        } else {
            setSnackbar("no-results")
        }
    }

    private fun getDeviceLocation(){
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        try {
            val location = fusedLocationProviderClient.lastLocation
            location.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val currentLocation: Location = task.result!!
                    moveCamera(LatLng(currentLocation.latitude, currentLocation.longitude),DEFAULT_ZOOM,"My position")
                } else {
                    setSnackbar("err-currloc")
                }
            }
        } catch (exc:Exception){
            Log.d("ERR","getDeviceLocation error: ${exc.message}")
            setSnackbar("err-currloc")
        }

    }

    private fun moveCamera(latLng: LatLng, zoom:Float, title:String){
        map.clear()
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom))

        val marker = MarkerOptions()
            .position(latLng)
            .title(title)
        map.addMarker(marker)
    }


    private fun setSnackbar(tipe:String){
        val text:String
        val background:Int
        when(tipe){
            "err-currloc" -> {
                text = "Unable to get current location"
                background = R.color.longTitle
            }
            "no-results" -> {
                text = "No results found"
                background = R.color.longTitle
            }
            "err-geo" -> {
                text = "Error while searching"
                background = R.color.longTitle
            }
            else -> {
                text = "The location has been saved!"
                background =  R.color.editedItem
            }
        }
        val snack: Snackbar = Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG)
        val tv: TextView = snack.view.findViewById(com.google.android.material.R.id.snackbar_text)
        tv.setTextColor(Color.WHITE)
        tv.typeface = Typeface.DEFAULT_BOLD
        snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(), background))
        snack.show()
    }
}