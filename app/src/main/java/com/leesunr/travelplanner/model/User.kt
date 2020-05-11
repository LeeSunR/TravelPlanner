package com.leesunr.travelplanner.model

import org.json.JSONObject

class User (
    var id:String,
    var email:String,
    var photourl:String,
    var nickname:String,
    var regdate:String
)
{
    fun parseUser(jsonObject: JSONObject){
        this.id = jsonObject.getString("userid")
        this.email = jsonObject.getString("email")
        this.nickname = jsonObject.getString("nickname")
        this.regdate = jsonObject.getString("regdate")
        this.photourl = jsonObject.getString("photourl")
    }

    fun parseUser(string: String){
        val jsonObject:JSONObject = JSONObject(string)
        this.id = jsonObject.getString("userid")
        this.email = jsonObject.getString("email")
        this.nickname = jsonObject.getString("nickname")
        this.regdate = jsonObject.getString("regdate")
        this.photourl = jsonObject.getString("photourl")
    }
}