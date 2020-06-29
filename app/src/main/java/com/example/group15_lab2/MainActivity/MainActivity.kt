package com.example.group15_lab2.MainActivity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.group15_lab2.DataClasses.LocationPosition
import com.example.group15_lab2.R
import com.example.group15_lab2.SignInActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_sign_in.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var headView: View
    private lateinit var navController:NavController
    private val myViewModel: MainActivityVM by viewModels()
    private val REQUEST_LOGIN = 4000

    fun setToolbarTitle(title:String){
        supportActionBar?.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout:DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_profile,
                R.id.nav_myItems,
                R.id.nav_advertisements,
                R.id.nav_itemsOfInterest,
                R.id.nav_boughtItems
            ), drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        headView = navView.getHeaderView(0)

        myViewModel.getUserAccount().observe(this, Observer { currentuser ->
            if(currentuser == null){
                val intent = Intent(this, SignInActivity::class.java)
                startActivityForResult(intent,REQUEST_LOGIN)
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
            }
            else {
                setUserDataIntoDrawer()
                myViewModel.addNotificationListener()
            }
        })

        val bn:ImageView = headView.findViewById(R.id.bn_logout)

        bn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.alert_logout_title))
                .setIcon(R.drawable.ic_signout)
                .setMessage(R.string.alert_logout_message)
                .setPositiveButton(R.string.alert_logout_positiveBN) {_,_ ->
                    myViewModel.signOut()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                .setNeutralButton(R.string.alert_logout_neutralBN) {_,_ ->}
                .show()
        }
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

        myViewModel.loadUserData()

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

    fun getUserLocation():LocationPosition?{
        return myViewModel.getUserLocation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {
            navController.popBackStack(R.id.nav_advertisements,true)
            navController.navigate(R.id.nav_advertisements)
        }
    }

}
