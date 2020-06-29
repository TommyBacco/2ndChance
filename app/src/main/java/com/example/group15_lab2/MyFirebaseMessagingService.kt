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

    private val ADMIN_CHANNEL_ID = "admin_channel"

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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notChannel = setChannel()
            nm.createNotificationChannel(notChannel)
        }


        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,0,intent,
            PendingIntent.FLAG_ONE_SHOT)

        val icon = when(type){
            1 -> R.drawable.ic_sold
            2 -> R.drawable.ic_remove_shopping_cart
            else -> R.drawable.ic_bookmark_add
        }

        val description =
            if(type == 1 && msg.data.get("itemSoldTo") == FirebaseRepository.getUserAccount().value?.uid)
                "congratulations, you bought it!"
        else
                msg.data.get("nDesc")

        val not = NotificationCompat.Builder(this,ADMIN_CHANNEL_ID)
        not.setSmallIcon(icon)
            .setContentTitle(msg.data.get("nTitle"))
            .setContentText(description)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        nm.notify(notID,not.build())
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setChannel(): NotificationChannel {
        val channelName = "New notification"
        val channelDesc = "Device to device notification"

        val notificationChannel = NotificationChannel(ADMIN_CHANNEL_ID,channelName,
            NotificationManager.IMPORTANCE_HIGH)

        notificationChannel.description = channelDesc
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)

        return notificationChannel
    }

}