package com.leesunr.travelplanner.model

import android.os.Parcel
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.util.App
import org.json.JSONObject
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Group(
    var gno : Int? = null,
    var gname : String? = null,
    var gregdate : Date? = null,
    var gphotourl : String? = null,
    var gmember_count : Int? = null
): Parcelable
{

    fun parseGroup(jsonObject: JSONObject): Group{
        this.gno = jsonObject.getInt("GNO")
        this.gname = jsonObject.getString("GNAME")
        this.gregdate = SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getString("REGDATE"))
        this.gphotourl = App.context.resources.getString(R.string.server_base_url) + jsonObject.getString("PHOTO_URL")
        this.gmember_count = jsonObject.getInt("MEMBER_COUNT")
        return this
    }

    fun parseEditGroup(jsonObject: JSONObject) : Group {
        this.gno = jsonObject.getInt("GNO")
        this.gname = jsonObject.getString("GNAME")
        this.gregdate = SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getString("REGDATE"))
        this.gphotourl = App.context.resources.getString(R.string.server_base_url) + jsonObject.getString("PHOTO_URL")
        return this
    }

}