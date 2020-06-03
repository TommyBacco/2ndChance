package com.example.group15_lab2.ShowProfile

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.group15_lab2.*
import com.example.group15_lab2.MainActivity.MainActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_show_profile.*

class ShowProfileFragment : Fragment() {

    private val myViewModel: ShowProfileVM by viewModels()
    private val REQUEST_LOCATION_PERMISSION = 5000
    private val FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_show_profile,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setToolbarTitle("User Profile")

        if(savedInstanceState == null) {
            val id = arguments?.getString("group15.lab3.USER_ID",null)
            myViewModel.setUserID(id)
        }
        populateUserView()
        setUserRatings()

        user_number_ratings.setOnClickListener {
            val userID = myViewModel.getUserID()
            val bundle = bundleOf(Pair("group15.lab4.USER_ID",userID))
            findNavController().navigate(R.id.action_nav_profile_to_showUserRatingFragment,bundle)
        }

        user_show_position.setOnClickListener {
            checkPermission()
        }
    }

    private fun populateUserView() {
        myViewModel.getUserData().observe(viewLifecycleOwner, Observer { u ->

            if(u.ID == null || u.ID == FirebaseRepository.getUserAccount().value?.uid){
                user_fullname.text = u.fullName
                user_address.text = u.address
                user_telephone.text = u.telephone
                setHasOptionsMenu(true)
            } else {
                // Fields not shown for privacy reasons
                user_fullname.visibility = View.GONE
                user_address.visibility = View.GONE
                user_telephone.visibility = View.GONE
                setHasOptionsMenu(false)
            }

            user_nickname.text = u.nickname
            user_email.text = u.email
            user_location.text = u.location

            Picasso.get()
                .load(u.avatarURL.toUri())
                .fit()
                .centerInside()
                .error(R.drawable.user_icon)
                .into(user_avatar)
        })
    }

    private fun setUserRatings(){
        myViewModel.getUserRatings().observe(viewLifecycleOwner, Observer { ratingList->
            var ratingAverage:Float=0F
            if(ratingList.isNotEmpty()) {
                for (rating in ratingList)
                    ratingAverage += rating.rating
                ratingAverage /= ratingList.size
            }
            user_rating_value.text = String.format("%.1f",ratingAverage)
            user_ratingBar.rating = ratingAverage
            val text = "${ratingList.size} ${resources.getString(R.string.default_review)}"
            user_number_ratings.text = text

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_pencil, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.pencil_button -> {
                findNavController().navigate(R.id.action_showProfileFragment_to_editProfileFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToMap(){
        findNavController().navigate(R.id.action_nav_profile_to_showMapFragment)
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
                    Toast.makeText(
                        requireActivity(), "Permission is needed\nto to access the map",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        }
    }
}