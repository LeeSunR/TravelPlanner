package com.leesunr.travelplanner.model

import android.os.Parcelable
import android.util.Log
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Plan(
    var pno : Int? = null,
    var pname : String? = null,
    var pcomment : String? = null,
    var pinfo : String? = null,
    var ptype : String? = null,
    var start_date : Date? = null,
    var start_time : Time? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var gno : Int? = null
): Parcelable
{
    fun parsePlan(jsonObject: JSONObject): Plan{
        this.pno = jsonObject.getInt("PNO")
        this.pname = jsonObject.getString("PNAME")
        this.pcomment = jsonObject.getString("PCOMMENT")
        this.pinfo = jsonObject.getString("PINFO")
        this.ptype = jsonObject.getString("PTYPE")
        this.start_date = SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getString("START_DATE"))
        this.start_time = Time.valueOf(jsonObject.getString("START_TIME"))
        this.gno = jsonObject.getInt("GNO")
        this.latitude = jsonObject.getDouble("LATITUDE")
        this.longitude = jsonObject.getDouble("LONGITUDE")
        return this
    }
}