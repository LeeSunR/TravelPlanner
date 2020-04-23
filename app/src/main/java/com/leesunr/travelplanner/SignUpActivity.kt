package com.leesunr.travelplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signup_close.setOnClickListener { view ->
            this.finish()
        }

        signup_uid.isCounterEnabled = true
        signup_password.isCounterEnabled = true
        signup_password_check.isCounterEnabled = true
        signup_nickname.isCounterEnabled = true

        signup_uid.counterMaxLength = 15
        signup_nickname.counterMaxLength = 10

        signup_uid.isErrorEnabled = true
        signup_nickname.isErrorEnabled = true

        edit_uid.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(edit_uid.length() > 15){
                    signup_uid.error = "아이디의 글자 수 최대 허용치를 초과하였습니다."
                } else {
                    signup_uid.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        edit_nickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(edit_nickname.length() > 10){
                    signup_nickname.error = "닉네임의 글자 수 최대 허용치를 초과하였습니다."
                } else {
                    signup_nickname.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }
}
