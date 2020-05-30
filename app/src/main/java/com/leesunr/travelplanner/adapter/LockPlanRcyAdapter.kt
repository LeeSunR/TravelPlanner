package com.leesunr.travelplanner.adapter

import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.model.Plan
import java.text.SimpleDateFormat


class LockPlanRcyAdapter(val context: Context, val plans: ArrayList<Plan>) :
    RecyclerView.Adapter<LockPlanRcyAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LockPlanRcyAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.lock_screen_rcy_item_plan, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return plans.size
    }

    override fun onBindViewHolder(holder: LockPlanRcyAdapter.Holder, position: Int) {
        holder?.bind(plans, context, position)
    }
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val detailLayout: LinearLayout = itemView.findViewById<LinearLayout>(R.id.layout_lock_plan_detail)
        private val btnDetail: Button = itemView.findViewById<Button>(R.id.btn_lock_plan_detail)
        private val btnDetailClose: TextView = itemView.findViewById<TextView>(R.id.btn_lock_plan_detail_close)
        private val backLayout: ConstraintLayout = itemView.findViewById<ConstraintLayout>(R.id.layout_lock_plan_back)
        private val tvPlanTitle = itemView.findViewById<TextView>(R.id.tv_lock_plan_title)
        private val tvPlanInfo = itemView.findViewById<TextView>(R.id.tv_lock_plan_info)
        private val tvPlanComment = itemView.findViewById<TextView>(R.id.tv_lock_plan_comment)
        private val tvPlanDate = itemView.findViewById<TextView>(R.id.tv_lock_plan_date)
        private val btnPlanDetail = itemView.findViewById<TextView>(R.id.btn_lock_plan_detail)

        fun bind(plans: ArrayList<Plan>, context: Context, position: Int){
            tvPlanDate.text = SimpleDateFormat("MM월 dd일 (E)").format(plans[position]!!.start_date)
            tvPlanTitle.text = plans[position]!!.pname
            tvPlanInfo.text = plans[position]!!.pinfo
            tvPlanComment.text = plans[position]!!.pcomment
            detailLayout.visibility=View.GONE

            btnDetail.setOnClickListener {
                backLayout.backgroundTintList = context.applicationContext.resources.getColorStateList(R.color.lockBackgroundBold,null)
                detailLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                val maxHeight = detailLayout.measuredHeight
                val valueAnimator = ValueAnimator.ofInt(1, maxHeight)
                detailLayout.layoutParams.height = 1
                detailLayout.requestLayout()
                valueAnimator.duration = 100
                valueAnimator.addUpdateListener { animation ->
                    detailLayout.layoutParams.height = animation.animatedValue as Int
                    detailLayout.requestLayout()
                    if(maxHeight==animation.animatedValue){
                        tvPlanInfo.text = plans[position]!!.pinfo
                        tvPlanComment.text = plans[position]!!.pcomment
                        btnPlanDetail.visibility = View.GONE
                    }
                }
                detailLayout.visibility=View.VISIBLE
                valueAnimator.start()
            }
            btnDetailClose.setOnClickListener {
                backLayout.backgroundTintList = context.applicationContext.resources.getColorStateList(R.color.lockBackgroundLight,null)
                detailLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                val valueAnimator = ValueAnimator.ofInt(detailLayout.measuredHeight, 1)
                valueAnimator.duration = 100
                valueAnimator.addUpdateListener { animation ->
                    detailLayout.layoutParams.height = animation.animatedValue as Int
                    detailLayout.requestLayout()
                    if(animation.animatedValue as Int == 1){
                        detailLayout.visibility=View.GONE
                        btnPlanDetail.visibility = View.VISIBLE
                    }
                }
                valueAnimator.start()
            }

            if(position>0 && SimpleDateFormat("yy년 MM월 dd일 (E)").format(plans[position]!!.start_date) == SimpleDateFormat("yy년 MM월 dd일 (E)").format(plans[position-1]!!.start_date)){
                tvPlanDate.visibility = View.GONE
            }


        }


    }

}