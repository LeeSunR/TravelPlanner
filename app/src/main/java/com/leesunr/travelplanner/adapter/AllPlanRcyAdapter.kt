package com.leesunr.travelplanner.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.listener.OnPlanListener
import com.leesunr.travelplanner.model.Plan
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AllPlanRcyAdapter (val context: Context, val allPlanList: ArrayList<ArrayList<Plan>>) :
    RecyclerView.Adapter<AllPlanRcyAdapter.Holder>(){

    val onPlanListener = object : OnPlanListener {
        override fun onDelete(planList : ArrayList<Plan>) {
            this@AllPlanRcyAdapter.notifyItemRangeChanged(allPlanList.indexOf(planList), allPlanList.size)
            Log.d("PlanList", "onDelete Success")
        }
    }

    override fun getItemCount(): Int {
        return allPlanList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllPlanRcyAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_item_all_plan, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(allPlanList[position], context, position)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var start_date = itemView.findViewById<TextView>(R.id.all_plan_list_title)
        var allPlanRcycler = itemView.findViewById<RecyclerView>(R.id.recyclerView_plan)
        var line = itemView.findViewById<RelativeLayout>(R.id.all_expense_list_line)

        fun bind(plan: ArrayList<Plan>, context: Context, position: Int){
            val adapter = PlanRcyAdapter(context, plan, onPlanListener)

            if(!plan.isEmpty()) {
                allPlanRcycler.setHasFixedSize(true)
                val lm = LinearLayoutManager(context)
                allPlanRcycler.layoutManager = lm
                allPlanRcycler.adapter = adapter
                start_date.text = SimpleDateFormat("yyyy/MM/dd (EEE)", Locale.KOREAN).format(plan[0].start_date)
            } else {
                start_date.visibility = View.GONE
                allPlanRcycler.visibility = View.GONE
                line.visibility= View.GONE
            }
        }
    }
}