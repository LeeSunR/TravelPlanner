package com.leesunr.travelplanner.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import kotlinx.android.synthetic.main.activity_group_plan_add.*
import java.sql.Date
import java.sql.Time

class GroupPlanAddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_plan_add)

        button_plan_add_back.setOnClickListener { finish() }
        button_plan_add_submit.setOnClickListener {
            val year = plan_add_startDate.year
            val month = plan_add_startDate.month + 1
            val day = plan_add_startDate.dayOfMonth +1
            val date_str = "$year-$month-$day"

            val hour = plan_add_startTime.hour
            val min = plan_add_startTime.minute
            val time_str = "$hour:$min:00"

            /* 해당 날짜의 요일 구하는 방법
            val dateFormat = SimpleDateFormat("EEE")
            var dayOfWeek = dateFormat.format(start_date)
            Log.e("dayOfWeek: ", dayOfWeek)
            */

            val gno = intent.getIntExtra("gno", 0)
            val pname = plan_add_title.text.toString()
            val pcomment = plan_add_comment.text.toString()
            val pinfo = plan_add_info.text.toString()
            val ptype = plan_add_type.selectedItem.toString()
            val start_date = Date.valueOf(date_str)
            val start_time = Time.valueOf(time_str)

            createPlan(gno, pname, pcomment, pinfo, ptype, start_date, start_time)
        }
    }

    private fun createPlan(gno: Int, pname: String, pcomment: String, pinfo: String, ptype: String,
                           start_date: Date, start_time: Time){
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this, myAPI.createPlan(gno, pname, pcomment, pinfo, ptype, start_date, start_time),
            { result ->
                Toast.makeText(this, "일정을 등록하였습니다.", Toast.LENGTH_SHORT).show()
                Log.d("plan create", result)
                setResult(Activity.RESULT_OK)
                finish()
            },
            { error ->
                Toast.makeText(this, "일정 등록을 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.d("plan create error", error)
                return@call true
            })
    }
}
