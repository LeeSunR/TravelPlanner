package com.leesunr.travelplanner.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.circleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.model.ChatDBHelper
import com.leesunr.travelplanner.model.Group
import com.leesunr.travelplanner.model.Message
import com.leesunr.travelplanner.model.User
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import com.leesunr.travelplanner.util.App
import com.leesunr.travelplanner.util.JWT
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_group_chat.*
import org.json.JSONObject
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*


class GroupChatActivity : AppCompatActivity() {
    private lateinit var group:Group
    private lateinit var mSocket:Socket
    private lateinit var user:User
    private var lastMessage:Message? = null
    private var lastLayout:LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        if(intent.hasExtra("group"))
            group = intent.getParcelableExtra<Group>("group");

        mSocket = IO.socket(this.resources.getString(R.string.chat_server_base_url))

        //서버로부터 인증정보 요청을 받음
        mSocket.on("auth",Emitter.Listener {
            when(it[0].toString()){
                "REQUEST_TOKEN" -> {
                    var json = Gson().toJsonTree(group).asJsonObject
                    json.addProperty("access_token",App.prefs_access.myAccessToken)
                    mSocket.emit("auth",json.toString())
                }
            }
        })

        layout_group_chat.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            scroll_group_chat.fullScroll(ScrollView.FOCUS_DOWN)
        }

        layout_group_chat.setOnHierarchyChangeListener(object: ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewRemoved(parent: View?, child: View?) {}

            override fun onChildViewAdded(parent: View?, child: View?) {
                val layout = parent as LinearLayout
                if(layout.childCount>1){
                    val previousChild = layout.getChildAt(layout.childCount-2)
                    if(previousChild.findViewById<TextView>(R.id.tv_checkbox_id).text == child!!.findViewById<TextView>(R.id.tv_checkbox_id).text)
                        if(previousChild.findViewById<TextView>(R.id.tv_checkbox_date).text == child!!.findViewById<TextView>(R.id.tv_checkbox_date).text)
                            previousChild.findViewById<TextView>(R.id.tv_checkbox_date).visibility = View.GONE

                    if(child!!.findViewById<TextView>(R.id.tv_checkbox_id).text!=user.id)
                        if(previousChild.findViewById<TextView>(R.id.tv_checkbox_id).text == child!!.findViewById<TextView>(R.id.tv_checkbox_id).text){
                            child.findViewById<TextView>(R.id.tv_checkbox_nickname).visibility = View.GONE
                            child.findViewById<ImageView>(R.id.im_checkbox_profile).visibility = View.GONE
                        }
                }
            }
        })

        //서버로부터 메세지를 받음
        mSocket.on("message",Emitter.Listener {
            val json = JSONObject(it[0].toString())
            val dbHandler = ChatDBHelper(this)
            val message = Message()
            message.gno = json.getInt("gno")
            message.id = json.getString("id")
            message.message = json.getString("message")
            message.nickname = json.getString("nickname")
            message.timestamp = json.getLong("timestamp")
            message.photourl = json.getString("photourl")
            dbHandler.insert(message)
            printMessage(message)
        })

        btn_group_chat_send.setOnClickListener {
            messageSend()
        }
    }

    override fun onResume() {
        super.onResume()
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this,myAPI.checkAccessToken(),{
            val payload = JWT.decoded(App.prefs_access.myAccessToken)
            user = User().parseUser(payload!!)
            try {
                mSocket.connect()
            } catch (e: URISyntaxException) {
                e.printStackTrace()
                finish()
            } finally {
                layout_group_chat.removeAllViews()
                val dbHandler = ChatDBHelper(this)
                val list = dbHandler.select(group.gno!!)
                for(message in list){
                    printMessage(message)
                }
            }
        },{
            return@call true
        })
    }

    override fun onPause() {
        super.onPause()
        mSocket.disconnect()
    }

    private fun messageSend(){
        val message = et_group_chat_message.text.toString()
        if(message != ""){
            mSocket.send(message)
        }
    }


    private fun printMessage(message: Message){
        val date = Date(message.timestamp!!)
        val format = SimpleDateFormat("MM월 dd일 HH시 mm분")
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout:LinearLayout
        if (message.id == user.id){
            layout = inflater.inflate(R.layout.chatbox_me, null) as LinearLayout
            runOnUiThread {
                layout.findViewById<TextView>(R.id.tv_checkbox_id).text = message.id
                layout.findViewById<TextView>(R.id.tv_checkbox_message).text = message.message
                layout.findViewById<TextView>(R.id.tv_checkbox_date).text = format.format(date)
                layout_group_chat.addView(layout)
            }
        }
        else {
            layout = inflater.inflate(R.layout.chatbox_others, null) as LinearLayout
            runOnUiThread {
                layout.findViewById<TextView>(R.id.tv_checkbox_id).text = message.id
                layout.findViewById<TextView>(R.id.tv_checkbox_nickname).text = message.nickname
                layout.findViewById<TextView>(R.id.tv_checkbox_message).text = message.message
                layout.findViewById<TextView>(R.id.tv_checkbox_date).text = format.format(date)
                Glide.with(this).load(this.getString(R.string.server_base_url)+message.photourl).apply(RequestOptions().circleCrop()).into(layout.findViewById<ImageView>(R.id.im_checkbox_profile))
                layout_group_chat.addView(layout)
            }
        }
    }
}
