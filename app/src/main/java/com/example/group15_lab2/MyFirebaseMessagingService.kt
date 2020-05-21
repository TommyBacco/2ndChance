package com.example.group15_lab2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.group15_lab2.MainActivity.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val notificationType = p0.data.get("nType")
        val itemOwner = p0.data.get("itemOwner")

       if(notificationType != null){
           if(notificationType == "sold"){
               if(itemOwner != FirebaseRepository.getUserAccount().value?.uid)
                   showNot(p0,1)
           } else if(notificationType == "block"){
               if(itemOwner != FirebaseRepository.getUserAccount().value?.uid)
                   showNot(p0,2)
           } else{ //newInterest
               if(itemOwner == FirebaseRepository.getUserAccount().value?.uid)
                   showNot(p0,3)
           }
       }
    }

    @Suppress("DEPRECATION")
    private  fun showNot(msg: RemoteMessage,type:Int){

        val nm:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val notID = System.currentTimeMillis().div(7).toInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            setChannel(nm)

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,0,intent,
            PendingIntent.FLAG_ONE_SHOT)

        val icon = when(type){
            1 -> R.drawable.ic_sold
            2 -> R.drawable.ic_remove_shopping_cart
            else -> R.drawable.ic_bookmark_add
        }

        val not = NotificationCompat.Builder(this)
        not.setSmallIcon(icon)
            .setContentTitle(msg.data.get("nTitle"))
            .setContentText(msg.data.get("nDesc"))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        nm.notify(notID,not.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setChannel(nm: NotificationManager) {

        val adminChannel = NotificationChannel("Admin","new notification",
            NotificationManager.IMPORTANCE_HIGH)

        adminChannel.description = "device to device"
        adminChannel.enableLights(true)
        adminChannel.lightColor = (Color.RED)
        adminChannel.enableVibration(true)

        nm.createNotificationChannel(adminChannel)

    }
}