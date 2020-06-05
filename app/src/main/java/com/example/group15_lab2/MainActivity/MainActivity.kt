package com.example.group15_lab2.MainActivity

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.group15_lab2.FirebaseRepository
import com.example.group15_lab2.R
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var headView: View
    private val myViewModel: MainActivityVM by viewModels()

    fun setToolbarTitle(title:String){
        supportActionBar?.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_profile,
                R.id.nav_myItems,
                R.id.nav_advertisements,
                R.id.nav_itemsOfInterest,
                R.id.nav_boughtItems,
                R.id.logInFragment
            ), drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        headView = navView.getHeaderView(0)

        if(FirebaseRepository.getUserAccount().value== null)
            if(savedInstanceState==null) {
                findNavController(R.id.nav_host_fragment).navigate(
                    R.id.action_nav_advertisements_to_logInFragment
                )
            }

        FirebaseRepository.getUserAccount().observe(this, Observer {
            myViewModel.setUser()
            if(it != null)
                setUserDataIntoDrawer()
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    private fun setUserDataIntoDrawer() {
        val user_fullname_view: TextView = headView.findViewById(R.id.nav_user_fullName)
        val user_nickname_view: TextView = headView.findViewById(R.id.nav_user_nickname)
        val user_email_view: TextView = headView.findViewById(R.id.nav_user_email)
        val user_avatar_view: ImageView = headView.findViewById(R.id.nav_user_avatar)

        myViewModel.getUserData().observe(this, Observer { profile ->
            user_fullname_view.text = profile.fullName
            user_nickname_view.text = profile.nickname
            user_email_view.text = profile.email
            Picasso.get()
                .load(profile.avatarURL.toUri())
                .fit()
                .centerInside()
                .error(R.drawable.user_icon)
                .into(user_avatar_view)
        })
    }

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

    fun getUserNickname():String?{
        return myViewModel.getUserNickname()
    }
}
