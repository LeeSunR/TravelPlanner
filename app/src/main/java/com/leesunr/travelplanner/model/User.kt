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
    var gno: Int? = null,
    var is_writable : Int? = null
)
{
    fun parseUser(jsonObject: JSONObject): User{
        this.id = jsonObject.getString("userid")
        this.email = jsonObject.getString("email")
        this.nickname = jsonObject.getString("nickname")
        this.regdate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject.getString("regdate"))
        this.photourl = App.context.resources.getString(R.string.server_base_url) + jsonObject.getString("photourl")
        return this
    }

    fun parseUserOfGroup(jsonObject: JSONObject): User{
        this.id = jsonObject.getString("LOGIN_ID")
        this.email = jsonObject.getString("EMAIL")
        this.nickname = jsonObject.getString("NICKNAME")
        this.regdate = SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getString("REGDATE"))
        this.photourl = App.context.resources.getString(R.string.server_base_url) + jsonObject.getString("PHOTO_URL")
        return this
    }

    fun parseUserOfGroup(jsonObject: JSONObject, gno: Int): User{
        this.id = jsonObject.getString("LOGIN_ID")
        this.email = jsonObject.getString("EMAIL")
        this.nickname = jsonObject.getString("NICKNAME")
        this.regdate = SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getString("REGDATE"))
        this.photourl = App.context.resources.getString(R.string.server_base_url) + jsonObject.getString("PHOTO_URL")
        this.gno = gno
        this.is_writable = jsonObject.getInt("IS_WRITABLE")
        return this
    }
}