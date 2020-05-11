package com.leesunr.travelplanner.model

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class User (
    var id:String? = null,
    var email:String? = null,
    var photourl:String? = null,
    var nickname:String? = null,
    var regdate:Date? = null
)
{
    fun parseUser(jsonObject: JSONObject): User{
        this.id = jsonObject.getString("userid")
        this.email = jsonObject.getString("email")
        this.nickname = jsonObject.getString("nickname")
        this.regdate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(jsonObject.getString("regdate"))
        this.photourl = jsonObject.getString("photourl")
        return this
    }

    fun parseUser(string: String): User{
        val jsonObject:JSONObject = JSONObject(string)
        this.id = jsonObject.getString("userid")
        this.email = jsonObject.getString("email")
        this.nickname = jsonObject.getString("nickname")
        this.regdate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(jsonObject.getString("regdate"))
        this.photourl = jsonObject.getString("photourl")
        return this
    }
}