package com.leesunr.travelplanner.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.fragment.GroupListFragment
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClient
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_group_create.*
import kotlinx.android.synthetic.main.activity_group_main.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream

class GroupCreateActivity : AppCompatActivity() {

    val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
    var compositeDisposable = CompositeDisposable()
    var fileUri = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_create)

        //토큰 검사
        MyServerAPI.call(this, myAPI.checkAccessToken(), {}, {return@call true})

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 0)

        button_group_create_back.setOnClickListener { finish() }

        group_create_image.setOnClickListener { view ->
            var intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, 1)
        }

        group_create_btn.setOnClickListener { view ->
            var gname = group_create_title_edit.text.toString()

            if(gname.isNotEmpty()){
                uploadStorage(fileUri, gname)
            } else {
                Toast.makeText(this, "그룹 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            var c = contentResolver.query(data?.data, null, null, null, null)
            c.moveToNext()

            var index = c.getColumnIndex(MediaStore.Images.Media.DATA)
            var source = c.getString(index)

            var bitmap = BitmapFactory.decodeFile(source)
            Glide.with(this).load(bitmap).centerCrop().override(500).into(group_create_image
            )
            fileUri = source
        }
    }

    private fun uploadStorage(path: String, gname : String) {

        val bitmap = BitmapFactory.decodeFile(path) //이미지 비트맵으로 열기
        val newBitmap = ThumbnailUtils.extractThumbnail(bitmap, 128, 128); //비트맵 크기 및 비율 변경

        //PNG로 변경하여 임시 저장
        val file = File(getExternalFilesDir("tmp").path+"group.png")
        val fileStream = FileOutputStream(file)
        newBitmap.compress(Bitmap.CompressFormat.PNG, 0, FileOutputStream(file))
        fileStream.close()

        //임시 저장된 이미지 전송
        val newFile = File(getExternalFilesDir("tmp").path+"group.png")

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
                    createGroup(gname, message)
                    Log.d("upload", message)
                },
                { error ->
                    Toast.makeText(this, "사진 업로드 실패", Toast.LENGTH_SHORT).show()
                    Log.d("upload error", error.message)
                }
            )
        )
    }

    private fun createGroup(gname : String, photourl : String) {
        compositeDisposable.add(myAPI.createGroup(gname, photourl)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Toast.makeText(this, "그룹 만들기 성공", Toast.LENGTH_SHORT).show()
                    Log.d("group create: ", result)
                    finish()
                },
                { error ->
                    Toast.makeText(this, "그룹 만들기 실패", Toast.LENGTH_SHORT).show()
                    Log.d("group create error", error.message)
                }
            )
        )
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
