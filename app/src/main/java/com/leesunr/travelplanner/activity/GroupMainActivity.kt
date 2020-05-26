package com.leesunr.travelplanner.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.adapter.AllPlanRcyAdapter
import com.leesunr.travelplanner.model.ChatDBHelper
import com.leesunr.travelplanner.model.Group
import com.leesunr.travelplanner.model.Plan
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import kotlinx.android.synthetic.main.activity_group_main.*
import org.json.JSONArray
import java.text.SimpleDateFormat


class GroupMainActivity : AppCompatActivity() {
    lateinit var group: Group
    lateinit var myBroadcastReceiver:MyBroadcastReceiver

    lateinit var planAdapter : AllPlanRcyAdapter
    lateinit var allPlanList : ArrayList<ArrayList<Plan>>
    lateinit var planList : ArrayList<Plan>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_main)

        if(intent.hasExtra("group")){
            group = intent.getParcelableExtra<Group>("group")
            button_group_title.text = group.gname
        }

        myBroadcastReceiver = MyBroadcastReceiver(group_chat_alarm,group)

        val filter = IntentFilter()
        filter.addAction("chatReceived")
        registerReceiver(myBroadcastReceiver, filter)

        planList = ArrayList<Plan>()
        allPlanList = ArrayList<ArrayList<Plan>>()
        planAdapter = AllPlanRcyAdapter(this, allPlanList)

        button_group_back.setOnClickListener {
            if(intent.hasExtra("group")){
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("groupFragment", "")
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            } else finish()
        }

        button_group_setting.setOnClickListener {
            val intent = Intent(this, GroupSettingActivity::class.java)
            intent.putExtra("gno", group.gno)
            intent.putExtra("groupName", group.gname)
            intent.putExtra("groupImage", group.gphotourl)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        button_group_plan_add.setOnClickListener {
            val intent = Intent(this, GroupPlanAddActivity::class.java)
            intent.putExtra("gno", group.gno)
            startActivity(intent)
        }

        button_group_chat.setOnClickListener {
            val intent = Intent(this, GroupChatActivity::class.java)
            intent.putExtra("group", group)
            startActivity(intent)
        }
        loadPlanList(group.gno!!)
    }
//그룹 목록 출력
    private fun loadPlanList(gno : Int){
        val dateFormat = SimpleDateFormat("yyyy-MM-dd");
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this, myAPI.loadPlanList(gno),
            { result ->
                val jsonArray = JSONArray(result)

                var cur_date : String? = null
                var new_date : String? = null
                // 첫번째 일정의 날짜 세팅
                var jsonObject = jsonArray.getJSONObject(0)
                var plan = Plan().parsePlan(jsonObject)
                cur_date = dateFormat.format(plan.start_date)

                for(i in 0 until jsonArray.length()){
                    jsonObject = jsonArray.getJSONObject(i)
                    plan = Plan().parsePlan(jsonObject)
                    new_date = dateFormat.format(plan.start_date)

                    if(cur_date.equals(new_date)) planList.add(plan)
                    else {
                        cur_date = new_date
                        allPlanList.add(planList)

                        planList = ArrayList<Plan>()
                        planList.add(plan)
                    }
                }
                allPlanList.add(planList)

                //레이아웃매니저를 설정해줍니다.
                planAdapter = AllPlanRcyAdapter(this, allPlanList)
                recyclerView_all_plan.adapter = planAdapter

                val lm = LinearLayoutManager(this)
                recyclerView_all_plan.layoutManager = lm
                recyclerView_all_plan.setHasFixedSize(true)
            },
            { error ->
                Log.e("PlanList error", error)
                return@call true
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myBroadcastReceiver);
    }

    override fun onResume() {
        super.onResume()
        group_chat_alarm.visibility=View.GONE
        val unconfirmedMessage = ChatDBHelper(this).unconfirmedMessage(group.gno!!)
        Log.e("result",unconfirmedMessage.toString())
        if (unconfirmedMessage)
            group_chat_alarm.visibility=View.VISIBLE
    }

    //새로운 채팅 수신 브로드케스트 리시버
    class MyBroadcastReceiver(view: View, group:Group) : BroadcastReceiver() {
        private val view = view
        private val group = group
        override fun onReceive(context: Context, intent: Intent) {
            var gno = intent.getIntExtra("gno", -1)
            if (gno == group.gno) view.visibility = View.VISIBLE
        }
    }
}
