package com.leesunr.travelplanner.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.model.User
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken

class MemberListRcyAdapter (val context: Context, val memberList: ArrayList<User>) :
    RecyclerView.Adapter<MemberListRcyAdapter.Holder>(){
    override fun getItemCount(): Int {
        return memberList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberListRcyAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_item_group_setting_member, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(memberList[position], context, position)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val member_img = itemView.findViewById<ImageView>(R.id.group_setting_member_image)
        val member_name = itemView.findViewById<TextView>(R.id.group_setting_member_name)
        val permission_sw = itemView.findViewById<Switch>(R.id.group_setting_member_permission)
        val kick_button = itemView.findViewById<Button>(R.id.group_setting_member_kick)

        fun bind(member: User, context: Context, position : Int){
            Glide.with(context).load(member.photourl)
                .signature(ObjectKey(System.currentTimeMillis()))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.ic_error_black_24dp)
                .override(48, 48)
                .into(member_img)

            member_name.text = member.nickname

            permission_sw.isChecked = member.is_writable != 0
            permission_sw.setOnCheckedChangeListener { compoundButton, b ->
                val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
                MyServerAPI.call(context as Activity, myAPI.modifyPermission(member.gno!!, member.nickname!!),
                    { result ->
                        member.is_writable = member.is_writable!!.xor(1)
                        Toast.makeText(context, "${member.nickname}의 권한을 수정하였습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("modifyPermission", "success")
                        this@MemberListRcyAdapter.notifyItemChanged(position)
                    },
                    { error ->
                        Toast.makeText(context, "권한이 없습니다.", Toast.LENGTH_LONG).show()
                        Log.e("modifyPermission error", error)
                        return@call true
                    })
            }

            kick_button.setOnClickListener {
                var builder = AlertDialog.Builder(context)
                builder.setTitle("멤버 추방")
                builder.setMessage("이 멤버를 추방하시겠습니까?")

                var listener = object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        when(which){
                            DialogInterface.BUTTON_POSITIVE ->
                                kickMember(member.gno!!, member.id!!, member, position)
                        }
                    }
                }
                builder.setPositiveButton("확인", listener)
                builder.setNegativeButton("취소", listener)
                builder.show()
            }
        }

        fun kickMember(gno: Int, login_id: String, user: User, position: Int){
            val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
            MyServerAPI.call(context as Activity, myAPI.kickMember(gno, login_id),
                { result ->
                    memberList.remove(user)
                    this@MemberListRcyAdapter.notifyItemRemoved(position)
                    Log.d("kickMember", result)
                },
                { error ->
                    Toast.makeText(context, "권한이 없습니다.", Toast.LENGTH_LONG).show()
                    Log.e("kickMember Error", error)
                    return@call true
                })
        }
    }
}