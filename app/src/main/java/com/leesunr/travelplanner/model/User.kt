package com.leesunr.travelplanner.model

import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.util.App
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class User (
    var id:String? = null,
    var email:String? = null,
    var photourl:String? = null,
    var nickname:String? = null,
    var regdate:Date? = null,
    var gno: Int? = null
)
{
    fun parseUser(jsonObject: JSONObject): User{
        this.id = jsonObject.getString("LOGIN_ID")
        this.email = jsonObject.getString("EMAIL")
        this.nickname = jsonObject.getString("NICKNAME")
        this.regdate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(jsonObject.getString("REGDATE"))
        this.photourl = App.context.resources.getString(R.string.server_base_url) + jsonObject.getString("PHOTO_URL")
        return this
    }

    fun parseUserOfGroup(jsonObject: JSONObject, gno: Int): User{
        this.id = jsonObject.getString("LOGIN_ID")
        this.email = jsonObject.getString("EMAIL")
        this.nickname = jsonObject.getString("NICKNAME")
        this.regdate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(jsonObject.getString("REGDATE"))
        this.photourl = App.context.resources.getString(R.string.server_base_url) + jsonObject.getString("PHOTO_URL")
        this.gno = gno
        return this
    }
}