package com.leesunr.travelplanner.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.adapter.MemberListRcyAdapter
import com.leesunr.travelplanner.fragment.ProfileFragment.Companion.PHOTO_CHANGE_REQUEST_CODE
import com.leesunr.travelplanner.model.Group
import com.leesunr.travelplanner.model.User
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import kotlinx.android.synthetic.main.activity_group_setting.*
import kotlinx.android.synthetic.main.item_list_group_setting_member.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import kotlin.properties.Delegates

class GroupSettingActivity : AppCompatActivity() {
    lateinit var group : Group
    lateinit var memberListAdapter: MemberListRcyAdapter
    lateinit var memberList : ArrayList<User>
    var gno by Delegates.notNull<Int>()
    var change by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_setting)

        change = false

        gno = intent.getIntExtra("gno", 0)
        val group_name = intent.getStringExtra("groupName")
        val group_image = intent.getStringExtra("groupImage")

        group_setting_gname.setText(group_name)

        Glide.with(this).load(group_image)
            .override(100, 100)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(group_setting_image)

        button_group_setting_back.setOnClickListener {
            finish()
        }

        memberList(gno)
//        그룹 대표 이미지 변경
        group_setting_image.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, PHOTO_CHANGE_REQUEST_CODE)
        }
//        그룹 이름 변경
        group_setting_gname_edit.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val linearLayout = LinearLayout(this)
            val newGroupName = EditText(this)
            val dpAsPixels = (20 * this!!.resources.displayMetrics.density + 0.5f).toInt()
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.setPadding(dpAsPixels, 0, dpAsPixels, 0)

            newGroupName.hint = "변경할 그룹이름을 입력하세요."
            linearLayout.addView(newGroupName)

            dialog.setView(linearLayout)
                .setTitle("그룹이름 변경")
                .setPositiveButton("확인") { dialogInterface, i ->
                    group_setting_gname.text = newGroupName.text
                }
                .setNegativeButton("취소") { dialogInterface, i -> }
                .show()
        }
//  그룹삭제 버튼 리스너
        group_setting_delete.setOnClickListener {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("그룹 삭제")
            builder.setMessage("이 그룹을 삭제하시겠습니까?")

            var listener = object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when(which){
                        DialogInterface.BUTTON_POSITIVE ->
                            deleteGroup(gno)
                    }
                }
            }
            builder.setPositiveButton("확인", listener)
            builder.setNegativeButton("취소", listener)
            builder.show()
        }
//  그룹정보 변경 버튼리스너
        group_setting_ok_button.setOnClickListener {
            //임시 저장된 이미지 전송
            val newFile = File(getExternalFilesDir("tmp").path+"group.png")

            var requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), newFile)
            var body: MultipartBody.Part = MultipartBody.Part.createFormData("imagefile", newFile.name, requestBody)
            val gno = RequestBody.create(MediaType.parse("text/plain"), gno.toString())
            val newGroupName = RequestBody.create(MediaType.parse("text/plain"), group_setting_gname.text.toString())

            val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
            MyServerAPI.call(this, myAPI.groupInfoChange(body, gno, newGroupName),
                { result ->
                    val jsonObject = JSONObject(result)
                    group = Group().parseEditGroup(jsonObject)
                    val intent = Intent(this, GroupMainActivity::class.java)
                    intent.putExtra("group", group)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                },{ error ->
                    Log.e("Group PhotoChange Error", error)
                    return@call false
                })
        }
//  멤버초대 버튼 리스너
        group_setting_member_invite.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val linearLayout = LinearLayout(this)
            val findUserId = EditText(this)
            val dpAsPixels = (20 * this!!.resources.displayMetrics.density + 0.5f).toInt()
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.setPadding(dpAsPixels, 0, dpAsPixels, 0)

            findUserId.hint = "아이디를 입력하세요."
            linearLayout.addView(findUserId)

            dialog.setView(linearLayout)
                .setTitle("멤버 초대")
                .setPositiveButton("초대") { dialogInterface, i ->
                    groupInvite(gno, findUserId.text.toString())
                }
                .setNegativeButton("취소") { dialogInterface, i -> }
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PHOTO_CHANGE_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            var bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data!!.data);
            val newBitmap = ThumbnailUtils.extractThumbnail(bitmap, 128, 128); //비트맵 크기 및 비율 변경

            //PNG로 변경하여 임시 저장
            val file = File(getExternalFilesDir("tmp").path+"group.png")
            val fileStream = FileOutputStream(file)
            newBitmap.compress(Bitmap.CompressFormat.PNG, 0, FileOutputStream(file))
            fileStream.close()

            Glide.with(this)
                .load(newBitmap)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.ic_error_black_24dp)
                .override(100, 100)
                .into(group_setting_image)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
//  그룹 삭제
    fun deleteGroup(gno: Int) {
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this, myAPI.deleteGroup(gno),
            { result ->
                Log.d("deleteGroup", result)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("groupFragment", "")
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            },
            { error ->
                Log.e("deleteGroup error", error)
                return@call true
            }
        )
    }

//    멤버 초대
    fun groupInvite(gno: Int, userid: String) {
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this, myAPI.groupInvite(gno, userid),
            { result ->
                val jsonObject = JSONObject(result)
                val user = User().parseUser(jsonObject)
                memberList.add(user)
                memberListAdapter.notifyDataSetChanged()
                Log.d("groupInvite", result)
            },
            { error ->
                Log.e("groupInvite error", error)
                return@call true
            }
        )
    }
//  그룹멤버 초기화
    fun memberList(gno: Int){
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this, myAPI.memberList(gno),
            { result ->
                val jsonArray = JSONArray(result)
                memberList = ArrayList<User>()
                for(i in 0 until jsonArray.length()){
                    val jsonObject = jsonArray.getJSONObject(i)
                    val user = User().parseUserOfGroup(jsonObject, gno)
                    memberList.add(user)
                }

                memberListAdapter = MemberListRcyAdapter(this, memberList)
                group_setting_memberList.adapter = memberListAdapter
                val lm = LinearLayoutManager(this)
                group_setting_memberList.layoutManager = lm
                group_setting_memberList.setHasFixedSize(true)
            },
            { error ->
                Log.e("MemberList Error", error)
                return@call true
            })
    }
}
