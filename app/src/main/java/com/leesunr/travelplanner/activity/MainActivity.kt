package com.leesunr.travelplanner.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.leesunr.travelplanner.fragment.GroupListFragment
import com.leesunr.travelplanner.fragment.HomeFragment
import com.leesunr.travelplanner.fragment.ProfileFragment
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //토큰 검사
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this,myAPI.checkAccessToken(),{},{return@call true})

        supportFragmentManager.beginTransaction().replace(
            R.id.fragmentView,
            HomeFragment()
        ).commit()
        bottomNavi.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId){
                R.id.tab_home ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.fragmentView,
                        HomeFragment()
                    ).commit()
                }
                R.id.tab_group ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.fragmentView,
                        GroupListFragment()
                    ).commit()
                }
                R.id.tab_profile ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.fragmentView,
                        ProfileFragment()
                    ).commit()
                }
            }
            true
        }
    }
}
