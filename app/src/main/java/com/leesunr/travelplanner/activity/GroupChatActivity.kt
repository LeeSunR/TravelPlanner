package com.leesunr.travelplanner.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.circleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.adapter.AllPlanRcyAdapter
import com.leesunr.travelplanner.adapter.MessageRcyAdapter
import com.leesunr.travelplanner.model.ChatDBHelper
import com.leesunr.travelplanner.model.Group
import com.leesunr.travelplanner.model.Message
import com.leesunr.travelplanner.model.User
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import com.leesunr.travelplanner.util.App
import com.leesunr.travelplanner.util.App.Companion.context
import com.leesunr.travelplanner.util.JWT
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_group_chat.*
import kotlinx.android.synthetic.main.activity_group_main.*
import org.json.JSONObject
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class GroupChatActivity : AppCompatActivity() {
    private lateinit var group:Group
    private lateinit var mSocket:Socket
    private lateinit var user:User
    private var messageList:ArrayList<Message>? = null
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
            dbHandler.insert(message,true)

            messageList!!.add(message)
            runOnUiThread {
                rcv_group_chat.adapter!!.notifyDataSetChanged()
                rcv_group_chat.scrollToPosition(rcv_group_chat.adapter!!.itemCount - 1);
            }
        })


        /*버튼 엑션*/

        //보내기 버튼
        btn_group_chat_send.setOnClickListener {
            messageSend()
        }

        //채팅방 종료 버튼
        button_group_chat_back.setOnClickListener {
            finish()
        }

        /*이벤트*/

        //텍스트 입력 이벤트
        et_group_chat_message.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.isEmpty())
                    btn_group_chat_send.backgroundTintList = ContextCompat.getColorStateList(context,R.color.colorMessageEnable)
                else
                    btn_group_chat_send.backgroundTintList = ContextCompat.getColorStateList(context,R.color.colorMessageAble)
            }
        })
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
                messageList = ChatDBHelper(this).select(group.gno!!)
                val mAdapter = MessageRcyAdapter(this, messageList!!)
                rcv_group_chat.adapter = mAdapter
                rcv_group_chat.layoutManager = LinearLayoutManager(this)
                rcv_group_chat.scrollToPosition(rcv_group_chat.adapter!!.itemCount - 1);
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
            et_group_chat_message.text=null
        }
    }
}
