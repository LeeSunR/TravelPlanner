package com.leesunr.travelplanner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.adapter.AllPlanRcyAdapter
import com.leesunr.travelplanner.model.Group
import com.leesunr.travelplanner.model.Plan
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import kotlinx.android.synthetic.main.activity_group_main.*
import org.json.JSONArray

class GroupMainActivity : AppCompatActivity() {

    lateinit var group: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_main)

        if(intent.hasExtra("group")){
            group = intent.getParcelableExtra<Group>("group")
            button_group_title.text = group.gname
        }

        group.gno?.let { loadPlanList(it) }

        button_group_back.setOnClickListener { finish() }
        button_group_setting.setOnClickListener {
            startActivity(Intent(this, GroupSettingActivity::class.java))
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
    }

    fun loadPlanList(gno : Int){
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this, myAPI.loadPlanList(gno),
            { result ->
                val jsonArray = JSONArray(result)
                var allPlanList = ArrayList<ArrayList<Plan>>()
                var planList = ArrayList<Plan>()

                Log.e("length: ", jsonArray.length().toString())
                for(i in 0 until jsonArray.length()){
                    val jsonObject = jsonArray.getJSONObject(i)
                    val plan = Plan().parsePlan(jsonObject)
                    planList.add(plan)
                }
                allPlanList.add(planList)
                Log.e("planList: ", planList.toString())
                Log.e("allPlanList: ", allPlanList.toString())
                //레이아웃매니저를 설정해줍니다.
                val mAdapter = AllPlanRcyAdapter(this, allPlanList)
//                val mAdapter = PlanRcyAdapter(this, planList)
                recyclerView_plan.adapter = mAdapter

                val lm = LinearLayoutManager(this)
                recyclerView_plan.layoutManager = lm
                recyclerView_plan.setHasFixedSize(true)
            },
            { error ->
                Log.e("PlanList error", error)
                return@call true
            }
        )
    }
}
