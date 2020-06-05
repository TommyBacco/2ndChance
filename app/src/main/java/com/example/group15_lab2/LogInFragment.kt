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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.group15_lab2.MainActivity.MainActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

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
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setToolbarTitle("Log-IN")

        logOut()

        bn_login.setOnClickListener {
            logIn()
        }

    }

    private fun logIn(){
        message_login.visibility=View.VISIBLE
        bn_login.visibility=View.GONE
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false).setAvailableProviders(providers).build(),
            AUTH_REQUEST_CODE)
    }

    private fun logOut(){
         FirebaseAuth.getInstance().signOut()
         AuthUI.getInstance().signOut(activity as MainActivity)

              if(FirebaseRepository.getUserAccount().value?.uid!=null){
             val snack: Snackbar = Snackbar.make(requireView(), "You have been successfully logged out", Snackbar.LENGTH_LONG)
             val tv: TextView = snack.view.findViewById(com.google.android.material.R.id.snackbar_text)
             tv.setTextColor(Color.WHITE)
             tv.typeface = Typeface.DEFAULT_BOLD
             snack.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.editedItem))
             snack.show()}

         bn_login.visibility=View.VISIBLE
         message_login.visibility=View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTH_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                FirebaseRepository.setUserAccount(FirebaseAuth.getInstance().currentUser)
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