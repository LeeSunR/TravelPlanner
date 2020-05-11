package com.leesunr.travelplanner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.leesunr.travelplanner.R
import kotlinx.android.synthetic.main.activity_group_main.*

class GroupMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_main)
        button_group_back.setOnClickListener { finish() }
        button_group_setting.setOnClickListener { startActivity(Intent(this,
            GroupSettingActivity::class.java)) }
        button_group_plan_add.setOnClickListener { startActivity(Intent(this,
            GroupPlanAddActivity::class.java)) }
        button_group_chat.setOnClickListener { startActivity(Intent(this,
            GroupChatActivity::class.java)) }
    }
}
