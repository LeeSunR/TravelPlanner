package com.leesunr.travelplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_group_main.*
import kotlinx.android.synthetic.main.activity_main.*

class GroupMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_main)
        button_group_back.setOnClickListener { finish() }

    }
}