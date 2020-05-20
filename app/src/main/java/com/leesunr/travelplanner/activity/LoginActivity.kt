package com.leesunr.travelplanner.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.leesunr.travelplanner.util.App
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.model.ChatDBHelper
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import retrofit2.Retrofit
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {

    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Init API
        val retrofit: Retrofit = RetrofitClient.instance
        myAPI = retrofit.create(INodeJS::class.java)

        login_signup.setOnClickListener {
            myStartActivity(SignUpActivity::class.java)
        }

        login_find_id.setOnClickListener {
            myStartActivity(FindIdPwActivity::class.java)
        }

        login_find_pwd.setOnClickListener {
            myStartActivity(FindIdPwActivity::class.java)
        }

        login_btn.setOnClickListener {
            var user_id = login_id.text.toString()
            var password = login_pwd.text.toString()
            if(user_id.length > 0 && password.length > 0)
                login(user_id, password)
            else
                Toast.makeText(this, "아이디 또는 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun login(userid: String, password: String) {
        thread {
            FirebaseInstanceId.getInstance().deleteInstanceId()
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                val fcmtoken = task.result?.token
                compositeDisposable.add(myAPI.loginUser(userid, password, fcmtoken!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { ok ->
                            Toast.makeText(this,"로그인 성공", Toast.LENGTH_SHORT).show()
                            myStartActivity(MainActivity::class.java)
                            Log.d("login completed", ok)

                            val jsonObject = JSONObject(ok)
                            val access_token = jsonObject.getString("access_token")
                            val refresh_token = jsonObject.getString("refresh_token")
                            //SharedPreferences 사용하여 access_token 디바이스에 저장
                            App.prefs_access.myAccessToken = access_token
                            App.prefs_refresh.myRefreshToken = refresh_token

                            ChatDBHelper(this).deleteAll()
                        },
                        { error ->
                            Toast.makeText(this,"로그인 실패", Toast.LENGTH_SHORT).show()
                            Log.d("login error", error.message)
                        }
                    )
                )
            })
        }
    }

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}