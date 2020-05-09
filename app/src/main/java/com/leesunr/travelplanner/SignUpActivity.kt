package com.leesunr.travelplanner

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import java.io.File


class SignUpActivity : AppCompatActivity() {

    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()
    var fileUri = ""
    private var photoURL = "http://baka.kr:9009/"

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

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

        signup_btn.setOnClickListener {
            var user_id = signup_edt_userid.text.toString()
            var password = signup_edt_pwd.text.toString()
            var passwordCheck = signup_edt_pwd_chk.text.toString()
            var email = signup_edt_email.text.toString()
            var nickname = signup_edt_nickname.text.toString()

            if(user_id.isNotEmpty() && password.isNotEmpty() && passwordCheck.isNotEmpty() && nickname.isNotEmpty() && email.isNotEmpty()){
                if(password.equals(passwordCheck))
                    uploadStorage(fileUri, user_id, password, nickname, email)
                else
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "회원정보를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadStorage(path: String, user_id: String, password: String, nickname: String, email: String) {
        val file = File(path)
        var requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
        var body: MultipartBody.Part = MultipartBody.Part.createFormData("imagefile", file.name, requestBody)
        val userID: RequestBody = RequestBody.create(MediaType.parse("text/plain"), signup_edt_userid.text.toString())

        // 파일, 사용자 아이디, 파일이름
        compositeDisposable.add(myAPI.uploadProfile(body, userID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { message ->
                    photoURL += message
                    signup(user_id, password, nickname, email, photoURL)
                    Log.d("upload", photoURL)
                },
                { error ->
                    Toast.makeText(this, "프로필 사진 업로드 실패", Toast.LENGTH_SHORT).show()
                    Log.d("upload error", error.message)
                }
            )
        )
    }

    private fun signup(user_id: String, password: String, nickname: String, email: String, photourl: String) {
        compositeDisposable.add(myAPI.signupUser(user_id, password, nickname, email, photourl)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { success ->
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    Log.d("sign up success", success)
                    myStartActivity(LoginActivity::class.java)
                },
                { error ->
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                    Log.d("sign up error", error.message)
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
            Glide.with(this).load(bitmap).centerCrop().override(500).into(signup_profile_photo)

            fileUri = source
        }
    }

    private fun myStartActivity(c: Class<*>) {
        val intent = Intent(this, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
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
