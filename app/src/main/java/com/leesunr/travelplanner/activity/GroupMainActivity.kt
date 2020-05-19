package com.leesunr.travelplanner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.model.Group
import kotlinx.android.synthetic.main.activity_group_main.*

class GroupMainActivity : AppCompatActivity() {

    lateinit var group: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_main)


        if(intent.hasExtra("group")){
            group = intent.getParcelableExtra<Group>("group")
            button_group_title.text = group.gname
        }

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
}
