package com.leesunr.travelplanner.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import kotlinx.android.synthetic.main.activity_group_expenses_add.*
import org.json.JSONObject
import java.sql.Date

import java.sql.Time
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class GroupExpensesAddActivity : AppCompatActivity() {

    var gno : Int? = null
    var dateStr : String? = null
    var timeStr : String? = null

    var set_date : Date? = null
    var set_time : Time? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_expenses_add)

        var localDateTime = LocalDateTime.now()
        val cur_date: java.util.Date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())

        set_date = Date.valueOf(SimpleDateFormat("yyyy-MM-dd").format(cur_date))
        set_time = Time.valueOf(SimpleDateFormat("HH:mm:ss").format(cur_date))

        expenses_add_dateTextView.setText(SimpleDateFormat("yy/M/d (EEE)", Locale.KOREAN).format(cur_date))
        expenses_add_timeTextView.setText(SimpleDateFormat("a h:mm", Locale.KOREAN).format(cur_date))

        expenses_add_cancelBtn.setOnClickListener { finish() }

        expenses_add_okBtn.setOnClickListener {
            var etitle : String = expenses_add_contentsEdit.text.toString()
            var cost : Int = Integer.parseInt(expenses_add_priceEdit.text.toString().replace(",", ""))

            if(intent.hasExtra("gno")){
                val gno = intent.getIntExtra("gno", 0)
                Log.e("gno", gno.toString())
                createExpenses(etitle, cost, set_date!!, set_time!!, gno!!)
            } else {
//                modifyExpenses()
            }
        }

        // 날짜 선택하는 부분 수정 필요
        expenses_add_dateTextView.setOnClickListener {
            var calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            var date_listener = object : DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    dateStr = "${year}-${month+1}-${dayOfMonth}"
                    set_date = Date.valueOf(dateStr)
                    var formattedDate = SimpleDateFormat("yy/M/d (EEE)", Locale.KOREAN).format(set_date)
                    expenses_add_dateTextView.setText(formattedDate)
                }
            }
            var picker = DatePickerDialog(this, date_listener, year, month, day)
            picker.show()
        }

        expenses_add_timeTextView.setOnClickListener {
            var calendar = Calendar.getInstance()
            var hour = calendar.get(Calendar.HOUR)
            var minute = calendar.get(Calendar.MINUTE)

            var time_listener = object : TimePickerDialog.OnTimeSetListener{
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    timeStr = "${hourOfDay}:${minute}:00"
                    set_time = Time.valueOf(timeStr)
                    var formattedTime = SimpleDateFormat("a h:mm", Locale.KOREAN).format(set_time)
                    expenses_add_timeTextView.setText(formattedTime)
                }
            }
            var picker = TimePickerDialog(this, time_listener, hour, minute, true)
            picker.show()
        }

        var pointNumStr = ""
        expenses_add_priceEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(pointNumStr)) {
                    pointNumStr = makeCommaNumber(Integer.parseInt(s.toString().replace(",","")))
                    expenses_add_priceEdit.setText(pointNumStr)
                    expenses_add_priceEdit.setSelection(pointNumStr.length)  //커서를 오른쪽 끝으로 보냄
                }
            }
        })
    }

    // 가계부 내역 생성
    private fun createExpenses(etitle: String, cost: Int, date: Date, time: Time, gno: Int)
    {
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this, myAPI.createExpenses(etitle, cost, date, time, gno),
            { result ->
                val jsonObject = JSONObject(result)

                Toast.makeText(this, "가계부 내역을 등록하였습니다.", Toast.LENGTH_SHORT).show()
                Log.d("expenses create", "success")

                val intent = Intent(this, GroupExpensesActivity::class.java)
                intent.putExtra("gno", gno)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            },
            { error ->
                Toast.makeText(this, "가계부 내역 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.d("expenses create error", error)
                return@call true
            })
    }

    // 화페단위 콤마 표시
    fun makeCommaNumber(input:Int): String{
        val formatter = DecimalFormat("###,###")
        return formatter.format(input)
    }
}
