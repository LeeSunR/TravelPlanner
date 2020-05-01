package com.leesunr.travelplanner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.leesunr.travelplanner.Retrofit.INodeJS
import com.leesunr.travelplanner.Retrofit.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Retrofit

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
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        login_find_id.setOnClickListener {
            startActivity(Intent(this, FindIdPwActivity::class.java))
        }

        login_find_pwd.setOnClickListener {
            startActivity(Intent(this, FindIdPwActivity::class.java))
        }

        login_btn.setOnClickListener {
            login(login_id.text.toString(), login_pwd.text.toString())
        }
    }

    private fun login(id: String, pwd: String) {
        compositeDisposable.add(myAPI.loginUser(id, pwd)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{ message ->
                if(message.contains("access_token"))
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            })
    }

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}