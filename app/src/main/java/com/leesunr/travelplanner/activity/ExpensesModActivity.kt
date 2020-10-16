package com.leesunr.travelplanner.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.model.Expenses
import kotlinx.android.synthetic.main.activity_expenses_mod.*
import java.text.SimpleDateFormat
import java.util.*

// 가계부 수정 액티비티
class ExpensesModActivity : AppCompatActivity() {

    lateinit var expensesInfo : Expenses

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses_mod)

        if(intent.hasExtra("expensesInfo")){
            expensesInfo = intent.getParcelableExtra("expensesInfo")
            expensesInit()
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
            expenses_mod_priceEdit.setText(cost)
        }
    }
}
