package com.leesunr.travelplanner.model

import com.leesunr.travelplanner.R
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class Group (
    var gname : String? = null,
    var gregdate : Date? = null,
    var gphotourl : String? = null
)
{
    fun parseGroup(jsonObject: JSONObject): Group{
        this.gname = jsonObject.getString("GNAME")
        this.gregdate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(jsonObject.getString("REGDATE"))
        this.gphotourl = "http://baka.kr:9009/" + jsonObject.getString("PHOTO_URL")

        return this
    }
}