package com.leesunr.travelplanner.Retrofit

import android.content.ContentValues.TAG
import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.*
import retrofit2.Call
import retrofit2.http.*
import java.io.File
import java.io.IOException


interface INodeJS {
    @POST("signup")
    @FormUrlEncoded
    fun signupUser(
        @Field("userid") id: String,
        @Field("email") email: String,
        @Field("nickname") nickname: String,
        @Field("password") password: String,
        @Field("photourl") photourl: String
    ): Observable<String>

    @POST("login")
    @FormUrlEncoded
    fun loginUser(
        @Field("userid") userid: String,
        @Field("password") password: String
    ): Observable<String>

    @Multipart
    @POST("/uploadProfile")
    fun uploadProfile(
        @Part imagefile : MultipartBody.Part,
        @Part("profile_userid") userid: RequestBody
    ): Observable<String>
}
