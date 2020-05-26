package com.leesunr.travelplanner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.model.Group
import java.text.SimpleDateFormat

class GroupListAdapter (val context: Context, val groupList: ArrayList<Group>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder : ViewHolder

        if(convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_item_group, null)
            holder =
                ViewHolder()
            holder.groupName = view.findViewById(R.id.travel_group_title)
            holder.groupPhoto = view.findViewById(R.id.travel_group_image)
            holder.groupRegdate = view.findViewById(R.id.travel_group_date)
            holder.groupMemberCount = view.findViewById(R.id.travel_group_member_count)

            view.tag = holder
            /* convertView가 null, 즉 최초로 화면을 실행할 때에
           ViewHolder에 각각의 TextView와 ImageView를 findVidwById로 설정.
           마지막에 태그를 holder로 설정한다. */
        } else {
            holder = convertView.tag as ViewHolder
            view = convertView
            /* 이미 만들어진 View가 있으므로, tag를 통해 불러와서 대체한다. */
        }

        val group = groupList[position]

        Glide.with(context).load(group.gphotourl)
            .signature(ObjectKey(System.currentTimeMillis()))
            .placeholder(R.mipmap.ic_launcher)
            .error(R.drawable.ic_error_black_24dp)
            .override(48, 48)
            .into(holder.groupPhoto!!)
        holder.groupName?.text = group.gname
        holder.groupRegdate?.text = "생성일 : ${SimpleDateFormat("yyyy-MM-dd").format(group.gregdate)}"
        holder.groupMemberCount?.text = "참가자 : ${group.gmember_count}명"

        return view
    }

    override fun getItem(position: Int): Any {
        return groupList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return groupList.size
    }

    private class ViewHolder {
        var groupName : TextView? = null
        var groupRegdate: TextView? = null
        var groupPhoto : ImageView? = null
        var groupMemberCount : TextView? = null
    }

}