package com.leesunr.travelplanner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.activity.GroupMainActivity.Companion.is_writable
import com.leesunr.travelplanner.listener.OnExpenseListener
import com.leesunr.travelplanner.model.Expenses

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

//        val optionBtn = itemView.findViewById<Button>(R.id.plan_list_option_button)
//        val fold = itemView.findViewById<Button>(R.id.plan_list_fold_button)
//        val hidden_layout = itemView.findViewById<LinearLayout>(R.id.plan_list_layout2)

        fun bind(expense: Expenses, context: Context, position: Int) {
            etitle.text = expense.etitle
            cost.text = expense.cost.toString()
//            start_time.text = SimpleDateFormat("HH:mm").format(plan.start_time)

            if (is_writable == 0) {
//                optionBtn.visibility = View.GONE
//                val params: RelativeLayout.LayoutParams =
//                    pname.layoutParams as RelativeLayout.LayoutParams
//                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
//                pname.layoutParams = params
            }

            /*fold.setOnClickListener {
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
                                deletePlan(plan.pno!!, plan, position)
                        }
                        false
                    }
                    pop.show()
            }*/
        }

        /*private fun deletePlan(pno : Int, plan : Plan, position: Int){
            val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
            MyServerAPI.call(context as Activity, myAPI.deletePlan(pno),
                { result ->
                    Log.d("deletePlan", result)
                    planList.remove(plan)
                    this@ExpenseRcyAdapter.notifyItemRemoved(position)
                    onPlanListener.onDelete(planList)
                },
                { error ->
                    Log.e("deletePlan error", error)
                    return@call true
                }
            )
        }*/

    }
}