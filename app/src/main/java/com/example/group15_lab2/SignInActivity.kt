package com.example.group15_lab2

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private val AUTH_REQUEST_CODE = 100

    private val providers by lazy {
        arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        supportActionBar?.setTitle(R.string.login_title)

        bn_login.setOnClickListener {
            logIn()
        }

    }

    private fun logIn(){
        AuthUI.getInstance().signOut(this)
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
                val text = resources.getString(R.string.sign_in_toast1)
                Toast.makeText(this, text, Toast.LENGTH_LONG).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                val text = resources.getString(R.string.sign_in_snack2)
                val snack: Snackbar = Snackbar.make(bn_login, text, Snackbar.LENGTH_LONG)
                val tv: TextView = snack.view.findViewById(com.google.android.material.R.id.snackbar_text)
                tv.setTextColor(Color.WHITE)
                tv.typeface = Typeface.DEFAULT_BOLD
                snack.view.setBackgroundColor(ContextCompat.getColor(this, R.color.longTitle))
                snack.show()
                message_login.visibility = View.GONE
                message_login_error.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
    }
}