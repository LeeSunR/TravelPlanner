package com.leesunr.travelplanner.google

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "MessagingService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {


        Log.e("fcm", "${remoteMessage.notification?.body}")
        if (remoteMessage!!.notification != null) {
            Log.e("fcm", "From: " + remoteMessage!!.from)
            Log.e("fcm", "${remoteMessage.notification?.body}")
        }

        if (remoteMessage.data.isNotEmpty()) {
            Log.e(TAG, "${remoteMessage.data} : 이것은 data입니다.")
        }
    }

    override fun onMessageSent(p0: String) {
        super.onMessageSent(p0)
    }

}