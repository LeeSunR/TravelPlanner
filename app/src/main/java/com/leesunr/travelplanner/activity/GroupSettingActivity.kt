package com.leesunr.travelplanner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import kotlinx.android.synthetic.main.activity_group_setting.*

class GroupSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_setting)

        group_setting_delete.setOnClickListener {
            Log.e("group.gno", intent.getIntExtra("gno", 0).toString())
            deleteGroup(intent.getIntExtra("gno", 0))
        }
    }

    fun deleteGroup(gno: Int){
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this, myAPI.deleteGroup(gno),
            { result ->
                Log.d("deleteGroup", result)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("groupFragment", "")
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            },
            { error ->
                Log.e("deleteGroup error", error)
                return@call true
            }
        )
    }
}
