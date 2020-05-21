package com.leesunr.travelplanner.adapter

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.model.Message
import com.leesunr.travelplanner.model.Plan
import com.leesunr.travelplanner.model.User
import com.leesunr.travelplanner.util.App
import com.leesunr.travelplanner.util.JWT
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MessageRcyAdapter(val context: Context, val messages: ArrayList<Message>) :
    RecyclerView.Adapter<MessageRcyAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageRcyAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_item_chat_message, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageRcyAdapter.Holder, position: Int) {
        holder?.bind(messages, context, position)
    }
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val linearLayout: LinearLayout = itemView.findViewById<LinearLayout>(R.id.linear_layout_message)
        private val tvNickname: TextView = itemView.findViewById<TextView>(R.id.tv_message_nickname)
        private val tvDate: TextView = itemView.findViewById<TextView>(R.id.tv_message_date)
        private val tvText: TextView = itemView.findViewById<TextView>(R.id.tv_message_text)
        private val imProfile: ImageView = itemView.findViewById<ImageView>(R.id.im_message_profile)
        private val user: User = User().parseUser(JWT.decoded(App.prefs_access.myAccessToken)!!)

        fun bind(messages: ArrayList<Message>, context: Context, position: Int){

            val date = Date(messages[position].timestamp!!)
            val format = SimpleDateFormat("MM월 dd일 HH시 mm분")
            val marginDP = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 128f, context.resources.displayMetrics).toInt()
            tvNickname.text = messages[position].nickname
            tvDate.text = format.format(date)
            tvText.text = messages[position].message
            tvNickname.text = messages[position].nickname
            Glide.with(context).load(context.getString(R.string.server_base_url)+messages[position].photourl).apply(
                RequestOptions().circleCrop()).into(imProfile)


            if(user.id==messages[position].id){ //내가 보낸 메세지
                linearLayout.gravity = Gravity.RIGHT
                val newParam = (tvText.layoutParams as ViewGroup.MarginLayoutParams)
                newParam.rightMargin = 0
                newParam.leftMargin = marginDP
                tvText.layoutParams = newParam
                tvText.backgroundTintList = ContextCompat.getColorStateList(context,R.color.colorMessageMe)
            }
            else{ //다른 사람이 보낸 메세지
                linearLayout.gravity = Gravity.LEFT
                val newParam = (tvText.layoutParams as ViewGroup.MarginLayoutParams)
                newParam.rightMargin = marginDP
                newParam.leftMargin = 0
                tvText.layoutParams = newParam
                tvText.backgroundTintList = ContextCompat.getColorStateList(context,R.color.colorMessageOthers)
            }

            //중복 넥네임 프로필 감추기
            if (position>0 && messages[position].id == messages[position-1].id){
                tvNickname.visibility = View.GONE
                imProfile.visibility = View.GONE
            } else {
                tvNickname.visibility = View.VISIBLE
                imProfile.visibility = View.VISIBLE
            }

            //중복 날짜 감추기
            if (position<messages.size-1 && format.format(Date(messages[position].timestamp!!)) == format.format(Date(messages[position+1].timestamp!!)))
                tvDate.visibility = View.GONE
            else
                tvDate.visibility = View.VISIBLE
        }
    }
}