package com.leesunr.travelplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.leesunr.travelplanner.Retrofit.INodeJS
import com.leesunr.travelplanner.Retrofit.MyServerAPI
import com.leesunr.travelplanner.Retrofit.RetrofitClientWithAccessToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit: Retrofit = RetrofitClientWithAccessToken.instance
        myAPI = retrofit.create(INodeJS::class.java)

        MyServerAPI.call(this,myAPI.checkAccessToken(),{},{return@call true}) //토큰 검사

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
