package com.leesunr.travelplanner.activity

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.model.Group
import com.leesunr.travelplanner.model.Plan
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import kotlinx.android.synthetic.main.activity_group_plan_add.*
import org.json.JSONObject
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat

class GroupPlanAddActivity : AppCompatActivity() {

    lateinit var planInfo : Plan
    private val locationRequestCode = 0x0011

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_plan_add)

        if(intent.hasExtra("planInfo")){
            planInfo = intent.getParcelableExtra("planInfo")
            planInit()
        }

        button_plan_add_back.setOnClickListener { finish() }

        button_plan_add_submit.setOnClickListener {
            val year = plan_add_startDate.year
            val month = plan_add_startDate.month + 1
            val day = plan_add_startDate.dayOfMonth
            val date_str = "$year-$month-$day"

            val hour = plan_add_startTime.hour
            val min = plan_add_startTime.minute
            val time_str = "$hour:$min:00"

            val pname = plan_add_title.text.toString()
            val pcomment = plan_add_comment.text.toString()
            val pinfo = plan_add_info.text.toString()
            val ptype = plan_add_type.selectedItem.toString()
            val start_date = Date.valueOf(date_str)
            val start_time = Time.valueOf(time_str)

            if(intent.hasExtra("gno")){
                val gno = intent.getIntExtra("gno", 0)
                Log.e("gno", gno.toString())
                createPlan(gno, pname, pcomment, pinfo, ptype, start_date, start_time)
            } else {
                modifyPlan(planInfo.pno!!, pname, pcomment, pinfo, ptype, start_date, start_time, planInfo.gno!!)
            }
        }

        plan_add_location.setOnClickListener {
            startActivityForResult(Intent(this,GroupPlanSelectLocationActivity::class.java),locationRequestCode)
        }
    }

    private fun createPlan(gno: Int, pname: String, pcomment: String, pinfo: String, ptype: String,
                           start_date: Date, start_time: Time)
    {
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this, myAPI.createPlan(gno, pname, pcomment, pinfo, ptype, start_date, start_time),
            { result ->
                val jsonObject = JSONObject(result)
                val group = Group().parseEditGroup(jsonObject)

                Toast.makeText(this, "일정을 등록하였습니다.", Toast.LENGTH_SHORT).show()
                Log.d("plan create", "success")

                val intent = Intent(this, GroupMainActivity::class.java)
                intent.putExtra("group", group)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            },
            { error ->
                Toast.makeText(this, "일정 등록을 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.d("plan create error", error)
                return@call true
            })
    }

    private fun planInit(){
        if(planInfo != null){
            val date = planInfo.start_date
            Log.e("date", date.toString())
            val year = SimpleDateFormat("yyyy").format(date)
            val month = SimpleDateFormat("MM").format(date)
            val day = SimpleDateFormat("dd").format(date)

            plan_add_startDate.init(year.toInt(), month.toInt()-1, day.toInt(), null)

            plan_add_startTime.hour = planInfo.start_time!!.hours
            plan_add_startTime.minute = planInfo.start_time!!.minutes

            val typeArray = resources.getStringArray(R.array.아이콘)
            plan_add_type.setSelection(typeArray.indexOf(planInfo.ptype))

            plan_add_title.setText(planInfo.pname)
            plan_add_info.setText(planInfo.pinfo)
            plan_add_comment.setText(planInfo.pcomment)
        }
    }

    private fun modifyPlan(pno: Int, pname: String, pcomment: String, pinfo: String, ptype: String,
                           start_date: Date, start_time: Time, gno: Int)
    {
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this, myAPI.modifyPlan(pno, pname, pcomment, pinfo, ptype, start_date, start_time, gno),
            { result ->
                val jsonObject = JSONObject(result)
                val group = Group().parseEditGroup(jsonObject)

                val intent = Intent(this, GroupMainActivity::class.java)
                intent.putExtra("group", group)
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)

                Toast.makeText(this, "일정을 수정하였습니다.", Toast.LENGTH_SHORT).show()
                Log.d("plan modify", result)
                startActivity(intent)
            },
            { error ->
                Toast.makeText(this, "일정 수정을 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.d("plan modify error", error)
                return@call true
            })
    }
}
