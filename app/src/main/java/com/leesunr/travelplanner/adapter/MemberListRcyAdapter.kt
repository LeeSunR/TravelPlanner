package com.leesunr.travelplanner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.model.User

class MemberListRcyAdapter (val context: Context, val memberList: ArrayList<User>) :
    RecyclerView.Adapter<MemberListRcyAdapter.Holder>(){
    override fun getItemCount(): Int {
        return memberList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberListRcyAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_list_group_setting_member, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(memberList[position], context)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val member_img = itemView.findViewById<ImageView>(R.id.group_setting_member_image)
        val member_name = itemView.findViewById<TextView>(R.id.group_setting_member_name)
        val permission_sw = itemView.findViewById<Switch>(R.id.group_setting_member_permission)
        val kick_button = itemView.findViewById<Button>(R.id.group_setting_member_kick)

        fun bind(member: User, context: Context){
            Glide.with(context).load(member.photourl)
                .signature(ObjectKey(System.currentTimeMillis()))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.ic_error_black_24dp)
                .override(48, 48)
                .into(member_img)

            member_name.text = member.nickname
            permission_sw.setOnCheckedChangeListener { compoundButton, b ->
                if(b){
                    // 활성화 처리
                } else{
                    // 비활성화 처리
                }
            }
            kick_button.setOnClickListener {
                // 멤버 추방 처리
            }
        }
    }
}