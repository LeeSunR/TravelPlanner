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
import com.leesunr.travelplanner.listener.OnExpenseListener
import com.leesunr.travelplanner.model.Expenses
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AllExpensesRcyAdapter(val context: Context, val allExpensesList: ArrayList<ArrayList<Expenses>>) :
    RecyclerView.Adapter<AllExpensesRcyAdapter.Holder>() {

    val onExpensesListener = object : OnExpenseListener {
        override fun onDelete(expensesList: ArrayList<Expenses>) {
            this@AllExpensesRcyAdapter.notifyItemRangeChanged(
                allExpensesList.indexOf(expensesList),
                allExpensesList.size
            )
            Log.d("PlanList", "onDelete Success")
        }
    }
    override fun getItemCount(): Int {
        return allExpensesList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllExpensesRcyAdapter.Holder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recycler_item_all_expense, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(allExpensesList[position], context, position)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var date = itemView.findViewById<TextView>(R.id.all_expenses_list_title)
        var expensesRcyAdapter = itemView.findViewById<RecyclerView>(R.id.recyclerView_expenses)
        var line = itemView.findViewById<RelativeLayout>(R.id.all_expenses_list_line)

        fun bind(expenses: ArrayList<Expenses>, context: Context, position: Int){
            val expensesAdapter = ExpenseRcyAdapter(context, expenses, onExpensesListener)

            if(!expenses.isEmpty()){
                expensesRcyAdapter.setHasFixedSize(true)
                val lm = LinearLayoutManager(context)
                expensesRcyAdapter.layoutManager = lm
                expensesRcyAdapter.adapter = expensesAdapter

                date.text =
                    SimpleDateFormat("yyyy/MM/dd (EEE)", Locale.KOREAN).format(expenses[0].date)
            } else {
                date.visibility = View.GONE
                expensesRcyAdapter.visibility = View.GONE
                line.visibility = View.GONE
            }

        }
    }
}