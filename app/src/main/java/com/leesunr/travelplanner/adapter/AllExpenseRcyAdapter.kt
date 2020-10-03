package com.leesunr.travelplanner.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.listener.OnExpenseListener
import com.leesunr.travelplanner.model.Expenses

// 수정 필요
class AllExpenseRcyAdapter (val context: Context, val allExpensesList: ArrayList<ArrayList<Expenses>>) :
    RecyclerView.Adapter<AllExpenseRcyAdapter.Holder>(){

    val onExpenseListener = object : OnExpenseListener {
        override fun onDelete(planList : ArrayList<Expenses>) {
            this@AllExpenseRcyAdapter.notifyItemRangeChanged(allExpensesList.indexOf(planList), allExpensesList.size)
            Log.d("ExpenseList", "onDelete Success")
        }
    }

    override fun getItemCount(): Int {
        return allExpensesList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllExpenseRcyAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_item_all_plan, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(allExpensesList[position], context, position)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        var start_date = itemView.findViewById<TextView>(R.id.all_plan_list_title)
//        var allPlanRcycler = itemView.findViewById<RecyclerView>(R.id.recyclerView_plan)
//        var line = itemView.findViewById<RelativeLayout>(R.id.all_expense_list_line)

        fun bind(expenses: ArrayList<Expenses>, context: Context, position: Int){
//            val adapter = PlanRcyAdapter(context, expense, onExpenseListener)
//
//            if(!expense.isEmpty()) {
//                allPlanRcycler.setHasFixedSize(true)
//                val lm = LinearLayoutManager(context)
//                allPlanRcycler.layoutManager = lm
//                allPlanRcycler.adapter = adapter
//                start_date.text = SimpleDateFormat("yyyy/MM/dd (EEE)").format(expense[0].start_date)
//            } else {
//                start_date.visibility = View.GONE
//                allPlanRcycler.visibility = View.GONE
//                line.visibility= View.GONE
//            }
        }
    }
}