package com.leesunr.travelplanner

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.leesunr.travelplanner.Retrofit.INodeJS
import com.leesunr.travelplanner.Retrofit.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Retrofit

class SignUpActivity : AppCompatActivity() {

    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //Init API
        val retrofit: Retrofit = RetrofitClient.instance
        myAPI = retrofit.create(INodeJS::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 0)

        signup_close.setOnClickListener { this.finish() }

        signup_pwd.isCounterEnabled = true
        signup_pwd_chk.isCounterEnabled = true
        signup_nickname.isCounterEnabled = true

        signup_nickname.counterMaxLength = 10

        signup_nickname.isErrorEnabled = true

        signup_edt_nickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (signup_edt_nickname.length() > 10) {
                    signup_nickname.error = "닉네임의 글자 수 최대 허용치를 초과하였습니다."
                } else {
                    signup_nickname.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        signup_profile_photo.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, 1)
        }

        signup_btn.setOnClickListener{
            signup(signup_edt_email.text.toString(), signup_edt_nickname.text.toString(), signup_edt_pwd.text.toString(), "photourl")
        }
    }

    private fun signup(userid: String, password: String, nickname: String, email: String, photourl: String?) {
        compositeDisposable.add(myAPI.signupUser(userid, password, email, nickname, "photourl")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { message ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                },
                { error ->
                    Toast.makeText(this,"회원가입 실패", Toast.LENGTH_SHORT).show()
                    Log.d("signup error", error.message)
                }
            )
        )

    }

    // 프로필 사진 뷰에 삽입
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            var c = contentResolver.query(data?.data, null, null, null, null)
            c.moveToNext()

            var index = c.getColumnIndex(MediaStore.Images.Media.DATA)
            var source = c.getString(index)

            var bitmap = BitmapFactory.decodeFile(source)
//            signup_profile_photo.setImageBitmap(bitmap)

            Glide.with(this).load(bitmap).centerCrop().override(500).into(signup_profile_photo)

        }
    }
}
