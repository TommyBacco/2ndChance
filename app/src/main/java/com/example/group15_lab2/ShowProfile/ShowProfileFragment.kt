package com.example.group15_lab2.ShowProfile

import android.os.Bundle
import android.view.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.group15_lab2.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_show_profile.*

class ShowProfileFragment : Fragment() {

    private val myViewModel: ShowProfileVM by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_show_profile,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState == null) {
            val id = arguments?.getString("group15.lab3.USER_ID",null)
            myViewModel.setUserID(id)
        }
        populateUserView()
    }

    private fun populateUserView() {
        myViewModel.getUserData().observe(viewLifecycleOwner, Observer { u ->

            if(u.ID == null || u.ID == FirebaseRepository.getUserAccount().value?.uid){
                user_fullname.text = u.fullName
                user_address.text = u.address
                user_telephone.text = u.telephone

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
}