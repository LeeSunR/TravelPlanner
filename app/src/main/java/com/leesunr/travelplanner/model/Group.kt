package com.leesunr.travelplanner.model

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class Group (
    var gno : Int? = null,
    var gname : String? = null,
    var gregdate : Date? = null,
    var gphotourl : String? = null,
    var gmember_count : Int? = null
)
{
    fun parseGroup(jsonObject: JSONObject): Group{
        this.gno = jsonObject.getInt("GNO")
        this.gname = jsonObject.getString("GNAME")
        this.gregdate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(jsonObject.getString("REGDATE"))
        this.gphotourl = "http://baka.kr:9009/" + jsonObject.getString("PHOTO_URL") //getString 사용 어떻게?
        this.gmember_count = jsonObject.getInt("MEMBER_COUNT")

        return this
    }
}