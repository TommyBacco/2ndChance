package com.example.group15_lab2

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_show_profile.*

class ShowProfileFragment : Fragment() {

    private var imageRotation:Float=0F
    private val sharedPref: SharedPreferences by lazy { this.activity!!.getPreferences(Context.MODE_PRIVATE) }
    private var imageByteArray:ByteArray?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_show_profile,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateTextView()
        populateImageView()

    }

    private fun populateTextView() {

        val jsonString:String? = sharedPref.getString(KEY_UserProfile,null)

        if(jsonString!=null){

            val profile:Profile = Gson().fromJson(jsonString,Profile::class.java)

            with(profile){
                user_fullname.text = fullName
                user_nickname.text = nickname
                user_email.text = email
                user_location.text = location
                user_address.text = address
                user_telephone.text = telephone
                imageRotation = rotation
            }
        }
    }

    private fun populateImageView(){
        try{
            showImage(this.activity!!.openFileInput(FILE_UserProfile_Avatar).readBytes(),imageRotation)
        } catch (exc:Exception){
            showImage(null,0F)
        }
    }

    private fun showImage(byteArray: ByteArray?,rotation:Float){
        if(byteArray != null){
            imageByteArray = byteArray
            var imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            if(rotation!=0F)
                imageBitmap = rotateImage(imageBitmap, rotation)
            item_image.setImageBitmap(imageBitmap)
        }
        else
            item_image.setImageResource(R.drawable.user_icon)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_pencil, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.pencil_button -> {
                editProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun editProfile(){
        val bundle = Bundle()
        with(bundle){
            putByteArray("group15.lab2.AVATAR",imageByteArray)
            putFloat("group15.lab2.AVATAR_ROTATION",imageRotation)
            putString("group15.lab2.FULL_NAME", user_fullname.text.toString())
            putString("group15.lab2.NICKNAME", user_nickname.text.toString())
            putString("group15.lab2.EMAIL", user_email.text.toString())
            putString("group15.lab2.LOCATION", user_location.text.toString())
            putString("group15.lab2.ADDRESS", user_address.text.toString())
            putString("group15.lab2.TELEPHONE", user_telephone.text.toString())
        }
        findNavController().navigate(R.id.action_showProfileFragment_to_editProfileFragment, bundle)
    }
}