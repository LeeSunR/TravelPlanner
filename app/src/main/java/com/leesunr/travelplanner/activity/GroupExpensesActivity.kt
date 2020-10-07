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

class GroupExpensesActivity : AppCompatActivity() {

    var gno : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_expense)

        if(intent.hasExtra("gno")) {
            gno = intent.getIntExtra("gno", 0)
        }

        button_group_expense_back.setOnClickListener { finish() }

        button_group_expense_add.setOnClickListener {
            val intent = Intent(this, GroupExpensesAddActivity::class.java)
            intent.putExtra("gno", gno)
            startActivity(intent)
        }

        // 리사이클러뷰 구현 필요
    }


}