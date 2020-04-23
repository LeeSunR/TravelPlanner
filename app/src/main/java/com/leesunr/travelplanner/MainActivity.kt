package com.leesunr.travelplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.fragmentView, HomeFragment()).commit()
        bottomNavi.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId){
                R.id.tab_home ->{
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentView, HomeFragment()).commit()
                }
                R.id.tab_group ->{
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentView, GroupListFragment()).commit()
                }
                R.id.tab_profile ->{
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentView, ProfileFragment()).commit()
                }
            }
            true
        }
    }
}
