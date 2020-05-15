package com.example.group15_lab2

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

class LogInFragment : Fragment() {

    private val AUTH_REQUEST_CODE = 100

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO MIGLIORARE VISTA FRAGMENT

        logIn()

        bn_login.setOnClickListener {
            logIn()
        }

    }

    private fun logIn(){

        val provider = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.EmailBuilder().build())

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(provider).build(),
            AUTH_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == AUTH_REQUEST_CODE && resultCode == Activity.RESULT_OK){
           FirebaseRepository.setUserID(FirebaseAuth.getInstance().currentUser)
            //SNACKBAR
            val snack: Snackbar = Snackbar.make(message_login, "You have been successfully logged", Snackbar.LENGTH_LONG)
            val tv: TextView = snack.view.findViewById(com.google.android.material.R.id.snackbar_text)
            tv.setTextColor(Color.WHITE)
            tv.typeface = Typeface.DEFAULT_BOLD
            snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.editedItem))
            snack.show()
            findNavController().popBackStack()
        }
        else{
            val snack: Snackbar = Snackbar.make(message_login, "Error while connecting,\nplease check your connection and try again", Snackbar.LENGTH_LONG)
            val tv: TextView = snack.view.findViewById(com.google.android.material.R.id.snackbar_text)
            tv.setTextColor(Color.WHITE)
            tv.typeface = Typeface.DEFAULT_BOLD
            snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.longTitle))
            snack.show()
        }
    }
}