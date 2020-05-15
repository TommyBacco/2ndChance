package com.example.group15_lab2

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val sharedPref: SharedPreferences by lazy { this.getPreferences(Context.MODE_PRIVATE) }
    private lateinit var headView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_profile, R.id.nav_advertisements), drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        headView = navView.getHeaderView(0)

        //TODO INSERT IMAGE AND DATA OF NAV DRAWER
        //setUserData()

        if(Repository.userID == null)
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_nav_advertisements_to_logInFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /*

    fun setUserData() {
        val jsonString: String? = sharedPref.getString(KEY_UserProfile, null)

        if (jsonString != null) {
            val profile: Profile = Gson().fromJson(jsonString, Profile::class.java)
            val user_nickname_view: TextView = headView.findViewById(R.id.nav_user_nickname)
            val user_email_view: TextView = headView.findViewById(R.id.nav_user_email)
            val user_avatar_view: ImageView = headView.findViewById(R.id.nav_user_avatar)

            with(profile) {
                user_nickname_view.text = nickname
                user_email_view.text = email
                var imageBitmap = getUserAvatar(rotation)
                if (imageBitmap != null)
                    user_avatar_view.setImageBitmap(imageBitmap)
                else
                    user_avatar_view.setImageResource(R.drawable.user_icon)
            }
        }
    }

    private fun getUserAvatar(rotation: Float): Bitmap? {
        val fileName = FILE_UserProfile_Avatar

        return try {
            val byteArray = this.openFileInput(fileName).readBytes()
            var imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            if (rotation != 0F)
                imageBitmap = rotateImage(imageBitmap, rotation)
            imageBitmap
        } catch (exc: Exception) {
            null
        }
    }

     */

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
