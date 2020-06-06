package com.example.group15_lab2

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.group15_lab2.MainActivity.MainActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class LogInFragment : Fragment() {

    private val AUTH_REQUEST_CODE = 100

    private val providers by lazy {
        arrayListOf(
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.EmailBuilder().build()
        ) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setToolbarTitle("Sign In")
        //(activity as MainActivity).hideToolbarNavigationUp()

        logIn()

        bn_login.setOnClickListener {
            Toast.makeText(requireContext(),FirebaseRepository.getUserAccount().value?.uid ?: "NULL",Toast.LENGTH_LONG).show()
            logIn()
        }

    }

    private fun logIn(){
        AuthUI.getInstance().signOut(requireContext())
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .setLogo(R.drawable.logo_app)
                .build(),
            AUTH_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTH_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                FirebaseRepository.updateUserAccount()
                //Positive Snackbar
                val snack: Snackbar = Snackbar.make(requireView(), "You have been successfully logged", Snackbar.LENGTH_LONG)
                val tv: TextView = snack.view.findViewById(com.google.android.material.R.id.snackbar_text)
                tv.setTextColor(Color.WHITE)
                tv.typeface = Typeface.DEFAULT_BOLD
                snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.editedItem))
                snack.show()
                findNavController().popBackStack()
            } else {
                val snack: Snackbar = Snackbar.make(requireView(), "Error while connecting :(", Snackbar.LENGTH_LONG)
                val tv: TextView = snack.view.findViewById(com.google.android.material.R.id.snackbar_text)
                tv.setTextColor(Color.WHITE)
                tv.typeface = Typeface.DEFAULT_BOLD
                snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.longTitle))
                snack.show()
                message_login.visibility = View.GONE
                message_login_error.visibility = View.VISIBLE
                bn_login.visibility = View.VISIBLE
            }
        }
    }
}