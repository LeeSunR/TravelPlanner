package com.leesunr.travelplanner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.model.Plan
import java.text.SimpleDateFormat

class AllPlanRcyAdapter (val context: Context, val allPlanList: ArrayList<ArrayList<Plan>>) :
    RecyclerView.Adapter<AllPlanRcyAdapter.Holder>(){
    override fun getItemCount(): Int {
        return allPlanList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllPlanRcyAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_all_plan_list, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(allPlanList[position], context, position)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val start_date = itemView.findViewById<TextView>(R.id.all_plan_list_title)
        val allPlanRcycler = itemView.findViewById<RecyclerView>(R.id.recyclerView_plan)

        fun bind(plan: ArrayList<Plan>, context: Context, position: Int){
            val adapter = PlanRcyAdapter(context, plan)
            allPlanRcycler.setHasFixedSize(true)
            val lm = LinearLayoutManager(context)
            allPlanRcycler.layoutManager = lm
            allPlanRcycler.adapter = adapter

            start_date.text = SimpleDateFormat("yyyy/MM/dd (EEE)").format(plan[0].start_date)
        }
    }
}