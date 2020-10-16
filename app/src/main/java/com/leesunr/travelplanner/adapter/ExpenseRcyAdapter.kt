package com.leesunr.travelplanner.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.activity.ExpensesModActivity
import com.leesunr.travelplanner.activity.GroupMainActivity.Companion.is_writable
import com.leesunr.travelplanner.model.Expenses
import java.text.DecimalFormat

class ExpenseRcyAdapter(val context: Context, val expensesList: ArrayList<Expenses>) :
    RecyclerView.Adapter<ExpenseRcyAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recycler_item_expense, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return expensesList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(expensesList[position], context, position)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val etitle = itemView.findViewById<TextView>(R.id.expenses_list_contents)
        val cost = itemView.findViewById<TextView>(R.id.expenses_list_cost)
        var const_layout = itemView.findViewById<ConstraintLayout>(R.id.rv_item_expenses_constLayout)

        fun bind(expenses: Expenses, context: Context, position: Int) {
            etitle.text = expenses.etitle
            cost.text = "${makeCommaNumber(expenses.cost!!)}원"
//            time.text = SimpleDateFormat("HH:mm").format(plan.start_time)

            if (is_writable == 0) {
                const_layout.isClickable = false
            } else {
                const_layout.setOnClickListener {
                    val intent = Intent(context, ExpensesModActivity::class.java)
                    intent.putExtra("expensesInfo", expenses)
                    context.startActivity(intent)
                }
            }
        }

        fun makeCommaNumber(input: Int): String {
            val formatter = DecimalFormat("###,###")
            return formatter.format(input)
        }
    }
}