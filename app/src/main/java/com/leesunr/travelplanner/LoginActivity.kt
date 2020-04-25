package com.leesunr.travelplanner

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_signup.setOnClickListener { view ->
                startActivity(Intent(this,SignUpActivity::class.java))
        }

        login_find_uid.setOnClickListener { view ->
            startActivity(Intent(this, FindIdPwActivity::class.java))
        }

        login_find_password.setOnClickListener { view ->
            startActivity(Intent(this, FindIdPwActivity::class.java))
        }
    }
}