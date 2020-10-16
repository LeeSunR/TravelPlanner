package com.leesunr.travelplanner.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.model.Expenses
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import kotlinx.android.synthetic.main.activity_expenses_mod.*
import java.sql.Time
import java.sql.Date
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

// 가계부 수정 액티비티
class ExpensesModActivity : AppCompatActivity() {

    lateinit var expensesInfo : Expenses

    var dateStr : String? = null
    var timeStr : String? = null
    var set_date : Date? = null
    var set_time : Time? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses_mod)

        if(intent.hasExtra("expensesInfo")){
            expensesInfo = intent.getParcelableExtra("expensesInfo")
            set_date = expensesInfo.date
            set_time = expensesInfo.time
            expensesInit()
        }

        var pointNumStr = ""
        expenses_mod_priceEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(pointNumStr)) {
                    pointNumStr = makeCommaNumber(Integer.parseInt(s.toString().replace(",","")))
                    expenses_mod_priceEdit.setText(pointNumStr)
                    expenses_mod_priceEdit.setSelection(pointNumStr.length)  //커서를 오른쪽 끝으로 보냄
                }
            }
        })

        expenses_mod_dateTextView.setOnClickListener {
            var calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            var date_listener = object : DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    dateStr = "${year}-${month+1}-${dayOfMonth}"
                    set_date = java.sql.Date.valueOf(dateStr)
                    var formattedDate = SimpleDateFormat("yy/M/d (EEE)", Locale.KOREAN).format(set_date)
                    expenses_mod_dateTextView.setText(formattedDate)
                }
            }
            var picker = DatePickerDialog(this, date_listener, year, month, day)
            picker.show()
        }

        expenses_mod_timeTextView.setOnClickListener {
            var calendar = Calendar.getInstance()
            var hour = calendar.get(Calendar.HOUR)
            var minute = calendar.get(Calendar.MINUTE)

            var time_listener = object : TimePickerDialog.OnTimeSetListener{
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    timeStr = "${hourOfDay}:${minute}:00"
                    set_time = Time.valueOf(timeStr)
                    var formattedTime = SimpleDateFormat("a h:mm", Locale.KOREAN).format(set_time)
                    expenses_mod_timeTextView.setText(formattedTime)
                }
            }
            var picker = TimePickerDialog(this, time_listener, hour, minute, true)
            picker.show()
        }


        expenses_mod_cancelBtn.setOnClickListener {
            finish()
        }

        expenses_mod_deleteBtn.setOnClickListener {
            val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
            MyServerAPI.call(this, myAPI.deleteExpenses(expensesInfo.eno!!),
                { result ->
                    val intent = Intent(this, GroupExpensesActivity::class.java)
                    intent.putExtra("gno", expensesInfo.gno)
                    intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)
                    Log.d("Delete Expenses Success", result)
                    startActivity(intent)
                },
                { error ->
                    Log.e("Delete Expenses Error!", error)
                    return@call true
                }
            )
        }

        expenses_mod_saveBtn.setOnClickListener {
            var etitle = expenses_mod_contentsEdit.text.toString()
            var cost = Integer.parseInt(expenses_mod_priceEdit.text.toString().replace(",", ""))

            modifyExpenses(expensesInfo.eno!!, etitle!!, cost!!, set_date!!, set_time!!, expensesInfo.gno!!)
        }
    }

    private fun expensesInit(){
        if(expensesInfo != null){
            val date = expensesInfo.date
            val time = expensesInfo.time
            val cost = expensesInfo.cost
            val etitle = expensesInfo.etitle

            expenses_mod_dateTextView.setText(SimpleDateFormat("yy/M/d (EEE)", Locale.KOREAN).format(date))
            expenses_mod_timeTextView.setText(SimpleDateFormat("a h:mm", Locale.KOREAN).format(time))
            expenses_mod_priceEdit.setText("${makeCommaNumber(cost!!)}")
            expenses_mod_contentsEdit.setText(etitle)
        }
    }

    private fun modifyExpenses(eno: Int, etitle: String, cost: Int, date: Date, time: Time, gno: Int)
    {
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this, myAPI.modifyExpenses(eno, etitle, cost, date, time, gno),
            { result ->
//                val jsonObject = JSONObject(result)
//                val group = Group().parseEditGroup(jsonObject)

                val intent = Intent(this, GroupExpensesActivity::class.java)
                intent.putExtra("gno", gno)
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)

                Toast.makeText(this, "가계부를 수정하였습니다.", Toast.LENGTH_SHORT).show()
                Log.d("expenses modify", result)
                startActivity(intent)
            },
            { error ->
                Toast.makeText(this, "가계부 수정을 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.d("expenses modify error", error)
                return@call true
            })
    }


    fun makeCommaNumber(input:Int): String {
        val formatter = DecimalFormat("###,###")
        return formatter.format(input)
    }
}
