package com.leesunr.travelplanner.retrofit

import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.*
import retrofit2.http.*
import java.sql.Date
import java.sql.Time

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
        @Field("password") password: String,
        @Field("fcmtoken") fcmtoken: String
    ): Observable<String>

    @POST("checkAccessToken")
    fun checkAccessToken(): Observable<String>

    @POST("renewalAccessToken")
    fun renewalAccessToken(): Observable<String>

    /******************** 프로필 ********************/
    @Multipart
    @POST("uploadProfile")
    fun uploadProfile(
        @Part imagefile : MultipartBody.Part
    ): Observable<String>

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

    /******************** 그룹 ********************/
    @POST("createGroup")
    @FormUrlEncoded
    fun createGroup(
        @Field("gname") gname : String,
        @Field("photourl") photourl : String
    ) : Observable<String>

    @POST("groupList")
    fun loadGroupList() : Observable<String>

    @POST("deleteGroup")
    @FormUrlEncoded
    fun deleteGroup(
        @Field("gno") gno : Int
    ) : Observable<String>

    @Multipart
    @POST("groupInfoChange")
    fun groupInfoChange(
        @Part imagefile : MultipartBody.Part,
        @Part("gno") gno : RequestBody,
        @Part("newGroupName") newGroupName: RequestBody
    ) : Observable<String>

    @POST("groupInvite")
    @FormUrlEncoded
    fun groupInvite(
        @Field("gno") gno : Int,
        @Field("userid") userid : String
    ) : Observable<String>

    @POST("memberList")
    @FormUrlEncoded
    fun memberList(
        @Field("gno") gno : Int
    ) : Observable<String>

    @POST("kickMember")
    @FormUrlEncoded
    fun kickMember(
        @Field("gno") gno : Int,
        @Field("login_id") login_id : String
    ) : Observable<String>

    /******************** 일정 ********************/
    @POST("createPlan")
    @FormUrlEncoded
    fun createPlan(
        @Field("gno") gno : Int,
        @Field("pname") pname : String,
        @Field("pcomment") pcomment : String,
        @Field("pinfo") pinfo : String,
        @Field("ptype") ptype : String,
        @Field("start_date") start_date : Date,
        @Field("start_time") start_time : Time
    ) : Observable<String>

    @POST("planList")
    @FormUrlEncoded
    fun loadPlanList(
        @Field("gno") gno : Int
    ) : Observable<String>

    @POST("deletePlan")
    @FormUrlEncoded
    fun deletePlan(
        @Field("pno") pno : Int
    ) : Observable<String>

    @POST("modifyPlan")
    @FormUrlEncoded
    fun modifyPlan(
        @Field("pno") pno : Int,
        @Field("pname") pname : String,
        @Field("pcomment") pcomment : String,
        @Field("pinfo") pinfo : String,
        @Field("ptype") ptype : String,
        @Field("start_date") start_date : Date,
        @Field("start_time") start_time : Time,
        @Field("gno") gno : Int
    ) : Observable<String>

}
