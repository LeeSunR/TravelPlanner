package com.leesunr.travelplanner.google

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.leesunr.travelplanner.model.ChatDBHelper
import com.leesunr.travelplanner.model.Message
import org.json.JSONObject


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "MessagingService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage!!.notification != null) {
            Log.e("fcm", "From: " + remoteMessage!!.from)
            Log.e("fcm", "${remoteMessage.notification?.body}")
        }

        if (remoteMessage.data.isNotEmpty()) {
            Log.e(TAG, "${remoteMessage.data} : fcm 수신.")
            val json = JSONObject(remoteMessage.data)
            val dbHandler = ChatDBHelper(applicationContext)
            val message = Message()
            message.gno = json.getInt("gno")
            message.id = json.getString("id")
            message.message = json.getString("message")
            message.nickname = json.getString("nickname")
            message.timestamp = json.getLong("timestamp")
            message.photourl = json.getString("photourl")
            dbHandler.insert(message,false)

            val intent = Intent("chatReceived")
            intent.putExtra("gno",json.getInt("gno"))
            sendBroadcast(intent)
        }
    }

    override fun onMessageSent(p0: String) {
        super.onMessageSent(p0)
    }
}