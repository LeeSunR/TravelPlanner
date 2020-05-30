package com.leesunr.travelplanner.adapter

import android.content.Context
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.model.Plan
import kotlin.collections.ArrayList


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


        fun bind(plans: ArrayList<Plan>, context: Context, position: Int){

        }
    }
}