package com.leesunr.travelplanner.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.activity.GroupPlanAddActivity
import com.leesunr.travelplanner.model.Plan
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import java.text.SimpleDateFormat

class PlanRcyAdapter(val context: Context, val planList: ArrayList<Plan>) :
    RecyclerView.Adapter<PlanRcyAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_plan_list, parent, false)
        return Holder(view)
    }
    override fun getItemCount(): Int {
        return planList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(planList[position], context)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val start_time = itemView.findViewById<TextView>(R.id.plan_list_start_time)
        val ptype = itemView.findViewById<ImageView>(R.id.plan_list_type)
        val pname = itemView.findViewById<TextView>(R.id.plan_list_title)
        val pinfo = itemView.findViewById<TextView>(R.id.plan_list_info_text)
        val pcomment = itemView.findViewById<TextView>(R.id.plan_list_comment_text)
        val optionBtn = itemView.findViewById<Button>(R.id.plan_list_option_button)
        val fold = itemView.findViewById<Button>(R.id.plan_list_fold_button)
        val hidden_layout = itemView.findViewById<LinearLayout>(R.id.plan_list_layout2)

        fun bind (plan: Plan, context: Context) {
            start_time.text = SimpleDateFormat("HH:mm").format(plan.start_time)
            pname.text = plan.pname
            pinfo.text = plan.pinfo
            pcomment.text = plan.pcomment

            fold.setOnClickListener {
                if(hidden_layout.visibility == View.GONE){
                    fold.setBackgroundResource(R.drawable.ic_arrow_drop_up_black_24dp)
                    hidden_layout.visibility = View.VISIBLE
                }
                else {
                    fold.setBackgroundResource(R.drawable.ic_arrow_drop_down_black_24dp)
                    hidden_layout.visibility = View.GONE
                }
            }

            optionBtn.setOnClickListener {
                    var pop = PopupMenu(context, optionBtn)

                    pop.inflate(R.menu.popup_menu_plan)
                    pop.setOnMenuItemClickListener { item ->
                        when(item?.itemId){
                            R.id.plan_modify ->{
                                val intent = Intent(context, GroupPlanAddActivity::class.java)
                                intent.putExtra("planInfo", plan)
                                context.startActivity(intent)
                            }
                            R.id.plan_delete ->
                                deletePlan(plan.pno!!, plan)
                        }
                        false
                    }
                    pop.show()
            }

            when (plan.ptype){
                "비행기" -> {
                    val resourceId = context.resources.getIdentifier("ic_plan_flight", "drawable", context.packageName)
                    ptype?.setImageResource(resourceId)
                }
                "자동차" -> {
                    val resourceId = context.resources.getIdentifier("ic_plan_car", "drawable", context.packageName)
                    ptype?.setImageResource(resourceId)
                }
                "버스" -> {
                    val resourceId = context.resources.getIdentifier("ic_plan_bus", "drawable", context.packageName)
                    ptype?.setImageResource(resourceId)
                }
                "지하철" -> {
                    val resourceId = context.resources.getIdentifier("ic_plan_subway", "drawable", context.packageName)
                    ptype?.setImageResource(resourceId)
                }
                "배" -> {
                    val resourceId = context.resources.getIdentifier("ic_plan_boat", "drawable", context.packageName)
                    ptype?.setImageResource(resourceId)
                }
                "숙소" -> {
                    val resourceId = context.resources.getIdentifier("ic_plan_hotel", "drawable", context.packageName)
                    ptype?.setImageResource(resourceId)
                }
                "관광지" -> {
                    val resourceId = context.resources.getIdentifier("ic_plan_place", "drawable", context.packageName)
                    ptype?.setImageResource(resourceId)
                }
                "카페" -> {
                    val resourceId = context.resources.getIdentifier("ic_plan_cafe", "drawable", context.packageName)
                    ptype?.setImageResource(resourceId)
                }
                "식사" -> {
                    val resourceId = context.resources.getIdentifier("ic_plan_meal", "drawable", context.packageName)
                    ptype?.setImageResource(resourceId)
                }
                "액티비티" -> {
                    val resourceId = context.resources.getIdentifier("ic_plan_activity", "drawable", context.packageName)
                    ptype?.setImageResource(resourceId)
                }
            }
        }

        private fun deletePlan(pno : Int, plan : Plan){
            val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
            MyServerAPI.call(context as Activity, myAPI.deletePlan(pno),
                { result ->
                    Log.d("deletePlan", result)
                    planList.remove(plan)
                    this@PlanRcyAdapter.notifyDataSetChanged()
                },
                { error ->
                    Log.e("deletePlan error", error)
                    return@call true
                }
            )
        }
    }
}