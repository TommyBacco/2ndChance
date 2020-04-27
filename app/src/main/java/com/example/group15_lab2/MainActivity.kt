package com.example.group15_lab2

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val sharedPref: SharedPreferences by lazy { this.getPreferences(Context.MODE_PRIVATE) }
    private lateinit var user_avatar_view:ImageView
    private lateinit var user_nickname_view:TextView
    private lateinit var user_email_view:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_profile,R.id.nav_advertisements), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val headView = navView.getHeaderView(0)

        user_nickname_view = headView.findViewById(R.id.nav_user_nickname)
        user_email_view = headView.findViewById(R.id.nav_user_email)
        user_avatar_view = headView.findViewById(R.id.nav_user_avatar)

        setUserData()

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

     fun setUserData() {
        val jsonString: String? = sharedPref.getString(KEY_UserProfile, null)

        if (jsonString != null) {
            val profile: Profile = Gson().fromJson(jsonString, Profile::class.java)
            with(profile) {
                user_nickname_view.text= nickname
                user_email_view.text = email
                setUserAvatar(rotation)
            }
        }
    }

    private fun setUserAvatar(rotation:Float){
        val fileName = FILE_UserProfile_Avatar

        try{
            val byteArray = this.openFileInput(fileName).readBytes()
            var imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            if(rotation != 0F)
                imageBitmap = rotateImage(imageBitmap, rotation)
           user_avatar_view.setImageBitmap(imageBitmap)
        } catch (exc:Exception){
            user_avatar_view.setImageResource(R.drawable.user_icon)
        }
    }
}
