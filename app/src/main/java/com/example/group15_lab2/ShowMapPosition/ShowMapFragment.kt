package com.example.group15_lab2.ShowMapPosition

import android.graphics.Color
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.group15_lab2.DataClasses.LocationPosition
import com.example.group15_lab2.MainActivity.MainActivity
import com.example.group15_lab2.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_show_map.*

class ShowMapFragment:Fragment() {

    private lateinit var map:GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private val DEFAULT_ZOOM = 15f
    private val DEFAULT_MAP_PADDING = 400
    private lateinit var myViewModel:ShowMapVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = resources.getString(R.string.showMap_title)
        (activity as MainActivity).setToolbarTitle(title)

        myViewModel = ViewModelProvider(this).get(ShowMapVM::class.java)

        if(savedInstanceState == null){
            val loc = arguments?.getString("group15.lab4.LOCALITY")
            val lat = arguments?.getDouble("group15.lab4.LATITUDE") ?: 0.0
            val lng = arguments?.getDouble("group15.lab4.LONGITUDE") ?: 0.0
            myViewModel.setPosition(LocationPosition(loc,lat,lng))
            val isItem = arguments?.getBoolean("group15.lab4.IS_ITEM") ?: false
            myViewModel.setIsItem(isItem)
            val user_loc = arguments?.getString("group15.lab4.USER_LOCALITY")
            val user_lat = arguments?.getDouble("group15.lab4.USER_LATITUDE") ?: 0.0
            val user_lng = arguments?.getDouble("group15.lab4.USER_LONGITUDE") ?: 0.0
            myViewModel.setUserPosition(LocationPosition(user_loc,user_lat,user_lng))
        }

        mapFragment = childFragmentManager.findFragmentById(R.id.mapAPI) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it
            map.uiSettings.isMapToolbarEnabled = false
            setPosition(myViewModel.getPosition())
        }

        myViewModel.getHasValidLocation().observe(viewLifecycleOwner, Observer {
            fab_road_to_road.isVisible = it
        })

        fab_road_to_road.setOnClickListener {
            showRoadToRoad()
        }
    }

    private fun setPosition(position: LocationPosition) {

        if(position.locality == null || position.locality == ""){
            setSnackbar("no-loc")
            return
        }

        val geocoder = Geocoder(requireContext())
        var list:List<Address> = ArrayList()

        try {
            list =
                if(position.latitude == 0.0 && position.longitude == 0.0)
                    geocoder.getFromLocationName(position.locality,1)
                else {
                        geocoder.getFromLocation(position.latitude,position.longitude, 1)
                }

        } catch (exc:Exception){
            Log.d("ERR","Error while geolocate ${exc.message}")
            setSnackbar("err-geo")
        }

        if(list.isNotEmpty()){
            val address = list[0]
            myViewModel.setHasValidLocation(true)
            moveCamera(LatLng(address.latitude,address.longitude),DEFAULT_ZOOM,address.getAddressLine(0))
        } else {
            setSnackbar("no-results")
        }
    }

    private fun moveCamera(latLng: LatLng, zoom:Float, title:String){
        map.clear()
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom))

        val marker = MarkerOptions()
            .position(latLng)
            .title(title)

        myViewModel.setPositionMarkerOptions(marker)

        map.addMarker(marker)
    }

    private fun setSnackbar(tipe:String){
        val background = R.color.longTitle
        val text = when(tipe){
            "no-loc" -> resources.getString(R.string.showMapPos_snack1)
            "no-results" -> resources.getString(R.string.map_snack_err_no_results)
            "no-user-results" -> resources.getString(R.string.showMapPos_snack3)
            else  -> resources.getString(R.string.map_snack_err_conn)
        }
        val snack: Snackbar = Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG)
        val tv: TextView = snack.view.findViewById(com.google.android.material.R.id.snackbar_text)
        tv.setTextColor(Color.WHITE)
        tv.typeface = Typeface.DEFAULT_BOLD
        snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(), background))
        snack.show()
    }

    private fun checkLocationValidity(position: LocationPosition):MarkerOptions?{
        if(position.locality == null || position.locality == ""){
            setSnackbar("no-user-results")
            return null
        }
        val geocoder = Geocoder(requireContext())
        var list:List<Address> = ArrayList()
        try {
            list =
                if(position.latitude == 0.0 && position.longitude == 0.0)
                    geocoder.getFromLocationName(position.locality,1)
                else {
                    geocoder.getFromLocation(position.latitude,position.longitude, 1)
                }

        } catch (exc:Exception){
            Log.d("ERR","Error while geolocate ${exc.message}")
            setSnackbar("err-geo")
        }

        if(list.isNotEmpty()){
            val address = list[0]
            return MarkerOptions()
                .position(LatLng(address.latitude,address.longitude))
                .title(address.getAddressLine(0))
        } else {
            setSnackbar("no-user-results")
        }
        return null
    }

    private fun showRoadToRoad(){
        val user_mo = checkLocationValidity(myViewModel.getUserPosition()) ?: return

        map.addPolyline(
            PolylineOptions()
                .add(myViewModel.getPositionMarkerOptions()?.position,user_mo.position)
                .width(8f).color(Color.BLUE).geodesic(true)
        )
        map.addMarker(user_mo)

        val builder =  LatLngBounds.Builder()

        builder.include(myViewModel.getPositionMarkerOptions()?.position)
        builder.include(user_mo.position)

        val bounds = builder.build()

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, DEFAULT_MAP_PADDING)

        map.moveCamera(cu)
    }
}