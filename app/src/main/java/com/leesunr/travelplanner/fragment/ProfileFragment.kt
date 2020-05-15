package com.leesunr.travelplanner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.activity.LoginActivity
import com.leesunr.travelplanner.model.User
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import com.leesunr.travelplanner.util.App
import com.leesunr.travelplanner.util.JWT
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat

class ProfileFragment : Fragment() {

    companion object {
        const val PHOTO_CHANGE_REQUEST_CODE = 0xDAA1
    }

    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //유저 정보 출력
        val payload = JWT.decoded(App.prefs_access.myAccessToken)
        val user = User().parseUser(payload!!)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd");
        Glide.with(this)
            .load(getString(R.string.server_base_url)+user.photourl)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(profile_image)
        profile_nickname.text = user.nickname
        profile_id.text = user.id
        profile_regdate.text = "가입일 : ${dateFormat.format(user.regdate)}"


        profile_nickname_change.setOnClickListener {
            val dialog = AlertDialog.Builder(mContext)
            val linearLayout = LinearLayout(mContext)
            val newNickname = EditText(mContext)
            val dpAsPixels =(20 * mContext!!.resources.displayMetrics.density + 0.5f).toInt()
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.setPadding(dpAsPixels,0,dpAsPixels,0)

            newNickname.hint="변경할 닉네임을 입력하세요"
            linearLayout.addView(newNickname)

            dialog.setView(linearLayout)
                .setTitle("닉네임 변경")
                .setPositiveButton("확인"){ dialogInterface, i->
                    val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
                    MyServerAPI.call(mContext as Activity,myAPI.nicknameChange(newNickname.text.toString()),{
                        result->
                        val jsonObject = JSONObject(result)
                        App.prefs_access.myAccessToken = jsonObject.getString("access_token")
                        App.prefs_refresh.myRefreshToken = jsonObject.getString("refresh_token")

                        val payload = JWT.decoded(App.prefs_access.myAccessToken)
                        val user = User().parseUser(payload!!)
                        profile_nickname.text = user.nickname
                    },{
                        return@call true
                    })
                }
                .setNegativeButton("취소") { dialogInterface, i-> }
                .show()
        }

        profile_password_change.setOnClickListener {
            val dialog = AlertDialog.Builder(mContext)
            val pwNow = EditText(mContext)
            val pwNew = EditText(mContext)
            val pwNewConfirm = EditText(mContext)
            val linearLayout = LinearLayout(mContext)
            val dpAsPixels =(20 * mContext!!.resources.displayMetrics.density + 0.5f).toInt()

            pwNow.hint="기존 비밀번호"
            pwNew.hint="새로운 비밀번호"
            pwNewConfirm.hint="새로운 비밀번호 확인"
            pwNow.transformationMethod = PasswordTransformationMethod.getInstance()
            pwNew.transformationMethod = PasswordTransformationMethod.getInstance()
            pwNewConfirm.transformationMethod = PasswordTransformationMethod.getInstance()
            linearLayout.setPadding(dpAsPixels,0,dpAsPixels,0)
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.addView(pwNow)
            linearLayout.addView(pwNew)
            linearLayout.addView(pwNewConfirm)

            dialog.setView(linearLayout)
                .setTitle("비밀번호 변경")
                .setPositiveButton("확인"){ dialogInterface, i-> }
                .setNegativeButton("취소") { dialogInterface, i-> }

            var alertDialog = dialog.create();
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (pwNew.text.toString().equals(pwNewConfirm.text.toString())){

                    val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
                    MyServerAPI.call(mContext as Activity,myAPI.passwordChange(pwNow.text.toString(),pwNew.text.toString()),{
                            result->
                        Toast.makeText(mContext,"비밀번호 변경 성공\n다시 로그인 해야합니다.",Toast.LENGTH_LONG).show()
                        App.prefs_access.myAccessToken = null
                        App.prefs_refresh.myRefreshToken = null
                        val intent = Intent(mContext, LoginActivity::class.java)
                        mContext.startActivity(intent)
                        ActivityCompat.finishAffinity(mContext as Activity);
                        alertDialog.dismiss();
                    },{
                        Toast.makeText(mContext,"비밀번호 변경에 실패했습니다.",Toast.LENGTH_SHORT).show()
                        return@call true
                    })

                }
                else{
                    Toast.makeText(mContext,"새로운 비밀번호 확인이 일치하지 않습니다.",Toast.LENGTH_SHORT).show()
                }
            }
        }

        //로그아웃 버튼
        profile_logout.setOnClickListener {
            val dialog = AlertDialog.Builder(mContext)
            dialog.setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("예") { dialogInterface, i->
                    App.prefs_access.myAccessToken = null
                    App.prefs_refresh.myRefreshToken = null
                    val intent = Intent(mContext, LoginActivity::class.java)
                    mContext.startActivity(intent)
                    ActivityCompat.finishAffinity(mContext as Activity);
                }
                .setNegativeButton("아니오") { dialogInterface, i-> }
                .show()
        }


        profile_image_change.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, PHOTO_CHANGE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PHOTO_CHANGE_REQUEST_CODE && resultCode==Activity.RESULT_OK){
            var bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), data!!.data);
            val newBitmap = ThumbnailUtils.extractThumbnail(bitmap, 128, 128); //비트맵 크기 및 비율 변경

            //PNG로 변경하여 임시 저장
            val file = File(mContext.getExternalFilesDir("tmp").path+"profile.png")
            val fileStream = FileOutputStream(file)
            newBitmap.compress(Bitmap.CompressFormat.PNG, 0, FileOutputStream(file))
            fileStream.close()

            //임시 저장된 이미지 전송
            val newFile = File(mContext.getExternalFilesDir("tmp").path+"profile.png")

            var requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), newFile)
            var body: MultipartBody.Part = MultipartBody.Part.createFormData("imagefile", newFile.name, requestBody)

            val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
            MyServerAPI.call(mContext as Activity,myAPI.userPhotoChange(body),{

                val payload = JWT.decoded(App.prefs_access.myAccessToken)
                val user = User().parseUser(payload!!)
                Glide.with(this)
                    .load(getString(R.string.server_base_url)+user.photourl)
                    .signature(ObjectKey(System.currentTimeMillis()))
                    .into(profile_image)
            },{
                return@call false
            })


        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

}
