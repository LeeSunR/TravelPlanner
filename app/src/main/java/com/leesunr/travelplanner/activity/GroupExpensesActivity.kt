package com.leesunr.travelplanner.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.adapter.AllExpensesRcyAdapter
import com.leesunr.travelplanner.model.Expenses
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import kotlinx.android.synthetic.main.activity_group_expense.*
import org.json.JSONArray
import org.json.JSONException
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class GroupExpensesActivity : AppCompatActivity() {
    var gno: Int? = null
    var totalCost : Int? = 0

    lateinit var allExpensesAdapter : AllExpensesRcyAdapter
    lateinit var allExpensesList : ArrayList<ArrayList<Expenses>>
    lateinit var expensesList : ArrayList<Expenses>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_expense)

        if(intent.hasExtra("gno")) {
            gno = intent.getIntExtra("gno", 0)
        }

        expensesList = ArrayList<Expenses>()
        allExpensesList = ArrayList<ArrayList<Expenses>>()

        button_group_expense_back.setOnClickListener { finish() }

        button_group_expense_add.setOnClickListener {
            val intent = Intent(this, GroupExpensesAddActivity::class.java)
            intent.putExtra("gno", gno)
            startActivity(intent)
        }

        Log.e("gno : ", gno.toString())
        loadExpensesList(gno!!)
    }

    private fun loadExpensesList(gno : Int){

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this, myAPI.loadExpensesList(gno),
            { result ->
                try {
                    val jsonArray = JSONArray(result)
                    var cur_date : String? = null
                    var new_date : String? = null

                    var jsonObject = jsonArray.getJSONObject(0)
                    var expenses = Expenses().parseExpense(jsonObject)
                    cur_date = dateFormat.format(expenses.date)

                    for(i in 0 until jsonArray.length()){
                        jsonObject = jsonArray.getJSONObject(i)
                        expenses = Expenses().parseExpense(jsonObject)
                        new_date = dateFormat.format(expenses.date)
                        totalCost = totalCost?.plus(expenses.cost!!)

                        if(cur_date.equals(new_date)) expensesList.add(expenses)
                        else {
                            cur_date = new_date
                            allExpensesList.add(expensesList)

                            expensesList = ArrayList<Expenses>()
                            expensesList.add(expenses)
                        }
                    }
                    allExpensesList.add(expensesList)

                    group_expense_totalAmount.text = "${makeCommaNumber(totalCost!!)}원"

                    //레이아웃매니저를 설정해줍니다.
                    allExpensesAdapter = AllExpensesRcyAdapter(this, allExpensesList)
                    recyclerView_all_expenses.adapter = allExpensesAdapter

                    val lm = LinearLayoutManager(this)
                    recyclerView_all_expenses.layoutManager = lm
                    recyclerView_all_expenses.setHasFixedSize(true)

                } catch (e : JSONException){
                    if(JSONArray(result).length() == 0)
                        expenses_no_item.visibility = View.VISIBLE
                }
            },
            { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT)
                Log.e("Load Expenses List Error", error)
                return@call true
            })
    }

    fun makeCommaNumber(input:Int): String {
        val formatter = DecimalFormat("###,###")
        return formatter.format(input)
    }
}