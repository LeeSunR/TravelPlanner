package com.leesunr.travelplanner.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.model.Group
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import kotlinx.android.synthetic.main.activity_group_expense.*
import org.json.JSONObject
import java.sql.Date

class GroupExpenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_expense)

        button_group_expense_back.setOnClickListener { finish() }

        button_group_expense_add.setOnClickListener {
            // Dialog 생성, 날짜, 내용, 금액

        }
    }

    // 가계부 내역 생성
    private fun createExpenses(etitle: String, cost: Int, regdate: Date, gno: Int)
    {
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this, myAPI.createExpenses(etitle, cost, regdate, gno),
            { result ->
                val jsonObject = JSONObject(result)
                val group = Group().parseEditGroup(jsonObject)

                Toast.makeText(this, "가계부 내역을 등록하였습니다.", Toast.LENGTH_SHORT).show()
                Log.d("expenses create", "success")
            },
            { error ->
                Toast.makeText(this, "가계부 내역을 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.d("expenses create error", error)
                return@call true
            })
    }
}