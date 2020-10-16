package com.leesunr.travelplanner.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
import java.sql.Time
import java.sql.Date

@Parcelize
data class Expenses(
    var eno : Int? = null,
    var etitle : String? = null,
    var cost : Int? = null,
    var date : Date? = null,
    var time : Time? = null,
    var gno : Int? = null
): Parcelable
{
    fun parseExpense(jsonObject: JSONObject): Expenses{
        this.eno = jsonObject.getInt("ENO")
        this.etitle = jsonObject.getString("ETITLE")
        this.cost = jsonObject.getInt("COST")
        this.date = Date.valueOf(jsonObject.getString("DATE"))
        this.time = Time.valueOf(jsonObject.getString("TIME"))
        this.gno = jsonObject.getInt("GNO")
        return this
    }
}