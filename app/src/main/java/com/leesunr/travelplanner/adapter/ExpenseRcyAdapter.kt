package com.leesunr.travelplanner.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.activity.ExpensesModActivity
import com.leesunr.travelplanner.activity.GroupExpensesAddActivity
import com.leesunr.travelplanner.activity.GroupMainActivity.Companion.is_writable
import com.leesunr.travelplanner.listener.OnExpenseListener
import com.leesunr.travelplanner.model.Expenses
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import java.text.DecimalFormat

// 수정 필요
class ExpenseRcyAdapter(val context: Context, val expensesList: ArrayList<Expenses>, val onExpensesListener: OnExpenseListener) :
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
        var const_layout =
            itemView.findViewById<ConstraintLayout>(R.id.rv_item_expenses_constLayout)

//        val optionBtn = itemView.findViewById<Button>(R.id.plan_list_option_button)
//        val fold = itemView.findViewById<Button>(R.id.plan_list_fold_button)
//        val hidden_layout = itemView.findViewById<LinearLayout>(R.id.plan_list_layout2)

        fun bind(expenses: Expenses, context: Context, position: Int) {
            etitle.text = expenses.etitle
            cost.text = "${makeCommaNumber(expenses.cost!!)}원"
//            start_time.text = SimpleDateFormat("HH:mm").format(plan.start_time)

            if (is_writable == 0) {
//                optionBtn.visibility = View.GONE
//                val params: RelativeLayout.LayoutParams =
//                    pname.layoutParams as RelativeLayout.LayoutParams
//                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
//                pname.layoutParams = params
            }

            const_layout.setOnClickListener {

                val intent = Intent(context, ExpensesModActivity::class.java)
                intent.putExtra("expensesInfo", expenses)
                context.startActivity(intent)
//                var pop = PopupMenu(context, const_layout)
//
//                pop.inflate(R.menu.popup_menu_plan)
//                pop.setOnMenuItemClickListener { item ->
//                    when (item?.itemId) {
//                        R.id.plan_modify -> {
//                            val intent = Intent(context, GroupExpensesAddActivity::class.java)
//                            intent.putExtra("expensesInfo", expenses)
//                            context.startActivity(intent)
//                        }
//                        R.id.plan_delete ->
//                            deleteExpenses(expenses.eno!!, expenses, position)
//                    }
//                    false
//                }
//                pop.show()
            }
        }

        private fun deleteExpenses(eno: Int, expenses: Expenses, position: Int) {
            val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
            MyServerAPI.call(context as Activity, myAPI.deleteExpenses(eno),
                { result ->
                    Log.d("Delete Expenses Success", result)
                    expensesList.remove(expenses)
                    this@ExpenseRcyAdapter.notifyItemRemoved(position)
                    onExpensesListener.onDelete(expensesList)
                },
                { error ->
                    Log.e("Delete Expenses Error!", error)
                    return@call true
                }
            )
        }

        fun makeCommaNumber(input: Int): String {
            val formatter = DecimalFormat("###,###")
            return formatter.format(input)
        }
    }
}