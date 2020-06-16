package com.leesunr.travelplanner.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream


class SignUpActivity : AppCompatActivity() {

    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()
    var fileUri = ""
    var chkNickname = false

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

        signup_nickname_chk.setOnClickListener {
            compositeDisposable.add(myAPI.checkNickname(signup_edt_nickname.text.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if(result.equals("success")){
                            chkNickname = true
                            Toast.makeText(this, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show()
                            Log.d("check nickname", "ok")
                        } else if(result.equals("overlap")){
                            chkNickname = false
                            Toast.makeText(this, "이미 사용 중인 닉네임입니다.", Toast.LENGTH_SHORT).show()
                            Log.d("check nickname", "fail")
                        }
                    },
                    { error ->
                        Log.e("check nickname error", error.message)
                    }
                )
            )
        }

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

            if(user_id.isNotEmpty() && password.isNotEmpty() && passwordCheck.isNotEmpty()
                && nickname.isNotEmpty() && email.isNotEmpty()){
                if(chkNickname){
                    if(password.equals(passwordCheck))
                        uploadStorage(fileUri, user_id, password, nickname, email)
                    else
                        Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "닉네임 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "회원정보를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadStorage(path: String, user_id: String, password: String, nickname: String, email: String) {

        val bitmap = BitmapFactory.decodeFile(path) //이미지 비트맵으로 열기
        val newBitmap = ThumbnailUtils.extractThumbnail(bitmap, 128, 128); //비트맵 크기 및 비율 변경

        //PNG로 변경하여 임시 저장
        val file = File(getExternalFilesDir("tmp").path+"profile.png")
        val fileStream = FileOutputStream(file)
        newBitmap.compress(Bitmap.CompressFormat.PNG, 0, FileOutputStream(file))
        fileStream.close()

        //임시 저장된 이미지 전송
        val newFile = File(getExternalFilesDir("tmp").path+"profile.png")

        var requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), newFile)
        var body: MultipartBody.Part = MultipartBody.Part.createFormData("imagefile", newFile.name, requestBody)
//        val userID: RequestBody = RequestBody.create(MediaType.parse("text/plain"), user_id)
        Log.e("file", file.name)
        // 파일, 사용자 아이디, 파일이름
        compositeDisposable.add(myAPI.uploadProfile(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { message ->
                    signup(user_id, password, nickname, email, message)
                    Log.d("upload", message)
                },
                { error ->
                    Toast.makeText(this, "사진 업로드 실패", Toast.LENGTH_SHORT).show()
                    Log.d("upload error", error.message)
                }
            )
        )
    }

    private fun signup(user_id: String, password: String, nickname: String, email: String, photoUrl: String) {
        compositeDisposable.add(myAPI.signupUser(user_id, password, nickname, email, photoUrl)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { success ->
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    Log.d("sign up success", success)
                    myStartActivity(LoginActivity::class.java)
                },
                { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
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
