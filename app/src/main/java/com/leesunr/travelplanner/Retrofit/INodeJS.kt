package com.leesunr.travelplanner.Retrofit

import io.reactivex.Observable
import okhttp3.*
import retrofit2.http.*

interface INodeJS {
    @POST("signup")
    @FormUrlEncoded
    fun signupUser(
        @Field("userid") id: String,
        @Field("password") password: String,
        @Field("nickname") nickname: String,
        @Field("email") email: String,
        @Field("photourl") photourl: String
    ): Observable<String>

    @POST("login")
    @FormUrlEncoded
    fun loginUser(
        @Field("userid") userid: String,
        @Field("password") password: String
    ): Observable<String>

    @POST("checkAccessToken")
    fun checkAccessToken(): Observable<String>

    @POST("renewalAccessToken")
    fun renewalAccessToken(): Observable<String>

    @Multipart
    @POST("/uploadProfile")
    fun uploadProfile(
        @Part imagefile : MultipartBody.Part
    ): Observable<String>
}
