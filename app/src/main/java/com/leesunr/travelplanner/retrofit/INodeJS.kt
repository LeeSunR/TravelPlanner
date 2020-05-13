package com.leesunr.travelplanner.retrofit

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
    @POST("uploadProfile")
    fun uploadProfile(
        @Part imagefile : MultipartBody.Part
    ): Observable<String>

    @POST("createGroup")
    @FormUrlEncoded
    fun createGroup(
        @Field("gname") gname : String,
        @Field("photourl") photourl : String
    ) : Observable<String>

    @POST("groupList")
    fun loadGroupList() : Observable<String>

    @POST("passwordChange")
    @FormUrlEncoded
    fun passwordChange(
        @Field("nowpw") nowpw : String,
        @Field("newpw") newpw : String
    ) : Observable<String>

    @POST("nicknameChange")
    @FormUrlEncoded
    fun nicknameChange(
        @Field("newnickname") newnickname : String
    ) : Observable<String>

    @Multipart
    @POST("userPhotoChange")
    fun userPhotoChange(
        @Part imagefile : MultipartBody.Part
    ) : Observable<String>
}
