package com.leesunr.travelplanner.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Expenses(
    var eno : Int? = null,
    var etitle : String? = null,
    var cost : Int? = null,
    var regdate : Date? = null,
    var gno : Int? = null
): Parcelable
{
    fun parseExpense(jsonObject: JSONObject): Expenses{
        this.eno = jsonObject.getInt("ENO")
        this.etitle = jsonObject.getString("ETITLE")
        this.cost = jsonObject.getInt("COST")
        this.regdate = SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getString("REGDATE"))
        this.gno = jsonObject.getInt("GNO")
        return this
    }
}