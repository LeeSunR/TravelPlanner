package com.leesunr.travelplanner.model

import android.util.Log
import java.util.*

class Weather (
    var date:Date? = null,
    var tempNow:Int? = null,
    var timezone:String? = null,
    var weatherName:String? = null,
    var weatherIcon:String? = null,
    var tempMin:Int? = null,
    var tempMax:Int? = null
)
{
    fun getDayOfWeek():Int{
        var cal = Calendar.getInstance()
        cal.time = date;
        return cal[Calendar.DAY_OF_WEEK]
    }
}