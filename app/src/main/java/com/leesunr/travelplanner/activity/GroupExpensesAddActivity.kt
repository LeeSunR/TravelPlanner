package com.leesunr.travelplanner.activity

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.model.Group
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import kotlinx.android.synthetic.main.activity_group_expenses_add.*
import org.json.JSONObject
import java.sql.Date
import java.util.*

class GroupExpensesAddActivity : AppCompatActivity() {

    var gno : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_expenses_add)

        gno = intent.getIntExtra("gno", 0)

        expenses_add_cancelBtn.setOnClickListener { finish() }

        expenses_add_okBtn.setOnClickListener {
            var etitle : String = expenses_add_contentsEdit.text.toString()
            var cost : Int = Integer.parseInt(expenses_add_priceEdit.text.toString())
            var regdate : Date = Date.valueOf(expenses_add_dateEdit.text.toString())

            // 날짜, 금액, 내용을 가지고서 createExpenses 함수 호출
            createExpenses(etitle, cost, regdate, gno!!)
        }

        // 날짜 선택하는 부분 수정 필요
        expenses_add_dateEdit.setOnClickListener {
            var calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            var listener = object : DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    var date : String? = "${year}-${month+1}-${dayOfMonth}"
                    expenses_add_dateEdit.setText(date)
                }
            }
            var picker = DatePickerDialog(this, listener, year, month, day)
            picker.show()
        }
    }

    // 가계부 내역 생성
    private fun createExpenses(etitle: String, cost: Int, regdate: Date, gno: Int)
    {
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this, myAPI.createExpenses(etitle, cost, regdate, gno),
            { result ->
                val jsonObject = JSONObject(result)
                val group = Group().parseEditGroup(jsonObject)

                Toast.makeText(this, "가계부 내역을 등록하였습니다.", Toast.LENGTH_SHORT).show()
                Log.d("expenses create", "success")

                val intent = Intent(this, GroupExpenseActivity::class.java)
                intent.putExtra("group", group)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            },
            { error ->
                Toast.makeText(this, "가계부 내역을 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.d("expenses create error", error)
                return@call true
            })
    }
}
