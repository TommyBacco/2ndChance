package com.example.group15_lab2.ShowMapPosition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.group15_lab2.DataClasses.LocationPosition
import com.example.group15_lab2.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_show_map.*

class ShowMapFragment:Fragment() {

    private lateinit var map:GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private val DEFAULT_ZOOM = 12f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment = childFragmentManager.findFragmentById(R.id.mapAPI) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it
            map.uiSettings.isMapToolbarEnabled = false
            setPosition()
        }


    }

    private fun setPosition(){

        val latLng = LatLng(30.0,31.0)
        moveCamera(latLng,DEFAULT_ZOOM,"My Location")
    }

    private fun moveCamera(latLng: LatLng, zoom:Float, title:String){
        map.clear()
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom))

        val marker = MarkerOptions()
            .position(latLng)
            .title(title)
        map.addMarker(marker)
    }
}