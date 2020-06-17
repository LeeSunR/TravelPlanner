package com.leesunr.travelplanner.google

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.leesunr.travelplanner.DBHelper.ChatDBHelper
import com.leesunr.travelplanner.DBHelper.GroupDBHelper
import com.leesunr.travelplanner.model.Group
import com.leesunr.travelplanner.model.Message
import com.leesunr.travelplanner.util.App
import org.json.JSONObject
import java.lang.Exception
import java.sql.SQLException


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "MessagingService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage!!.notification != null) {
            Log.e("fcm", "From: " + remoteMessage!!.from)
            Log.e("fcm", "${remoteMessage.notification?.body}")
        }

        if (remoteMessage.data.isNotEmpty()) {
            Log.e(TAG, "${remoteMessage.data}")
            val json = JSONObject(remoteMessage.data)

            when(json.getString("type")){
                "chat"->{
                    val message = Message()
                    message.gno = json.getInt("gno")
                    message.id = json.getString("id")
                    message.message = json.getString("message")
                    message.nickname = json.getString("nickname")
                    message.timestamp = json.getLong("timestamp")
                    message.photourl = json.getString("photourl")
                    val dbHandler =
                        ChatDBHelper(
                            applicationContext
                        )
                    dbHandler.insert(message,false)
                    val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    val mChannel = NotificationChannel("chatChannel", "채팅 알림", NotificationManager.IMPORTANCE_HIGH)
                    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(mChannel)

                    val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "chatChannel")
                    builder.setSmallIcon(R.drawable.stat_notify_chat)
                    builder.setContentTitle(json.getString("nickname"))
                    builder.setContentText(json.getString("message"))
                    builder.setAutoCancel(true)

                    notificationManager.notify(2, builder.build())

                    val intent = Intent("chatReceived")
                    intent.putExtra("gno",json.getInt("gno"))
                    sendBroadcast(intent)
                }
                "createPlan"->{
                    val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    val mChannel = NotificationChannel("planChannel", "일정 알림", NotificationManager.IMPORTANCE_HIGH)
                    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(mChannel)
                    val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "planChannel")
                    builder.setSmallIcon(R.drawable.stat_notify_chat)
                    builder.setContentTitle("일정이 추가되었습니다")
                    builder.setContentText(json.getString("nickname")+"님이 <"+json.getString("pname")+"> 일정을 추가했습니다.")
                    builder.setAutoCancel(true)

                    notificationManager.notify(2, builder.build())

                    val group = Group()
                    group.gno=json.getInt("gno")

                    val groupjson = JSONObject(App.groupConfirmed.groupConfirmed)
                    groupjson.put(group.gno.toString(),0)
                    App.groupConfirmed.groupConfirmed = groupjson.toString()

                    val intent = Intent("planReceived")
                    intent.putExtra("gno",json.getInt("gno"))
                    sendBroadcast(intent)
                }
                "modifyPlan"->{
                    val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    val mChannel = NotificationChannel("planChannel", "일정 알림", NotificationManager.IMPORTANCE_HIGH)
                    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(mChannel)
                    val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "planChannel")
                    builder.setSmallIcon(R.drawable.stat_notify_chat)
                    builder.setContentTitle("일정이 수정되었습니다")
                    builder.setContentText(json.getString("nickname")+"님이 <"+json.getString("pname")+"> 일정을 수정했습니다.")
                    builder.setAutoCancel(true)

                    notificationManager.notify(2, builder.build())

                    val group = Group()
                    group.gno=json.getInt("gno")

                    val groupjson = JSONObject(App.groupConfirmed.groupConfirmed)
                    groupjson.put(group.gno.toString(),0)
                    App.groupConfirmed.groupConfirmed = groupjson.toString()

                    val intent = Intent("planReceived")
                    intent.putExtra("gno",json.getInt("gno"))
                    sendBroadcast(intent)
                }
            }







        }
    }

    override fun onMessageSent(p0: String) {
        super.onMessageSent(p0)
    }
}