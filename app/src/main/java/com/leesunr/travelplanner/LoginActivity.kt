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
                val intent: Intent? = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
        }
    }
}