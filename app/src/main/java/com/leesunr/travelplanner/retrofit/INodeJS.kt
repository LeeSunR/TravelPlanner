package com.leesunr.travelplanner.retrofit

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

    @POST("deleteAccount")
    fun deleteAccount() : Observable<String>

    @POST("checkNickname")
    @FormUrlEncoded
    fun checkNickname(
        @Field("input_nickname") input_nickname : String
    ): Observable<String>

    @POST("checkAccessToken")
    fun checkAccessToken(): Observable<String>

    @POST("renewalAccessToken")
    fun renewalAccessToken(): Observable<String>

    @POST("emailAuthId")
    @FormUrlEncoded
    fun emailAuthId(
        @Field("email") email : String
    ) : Observable<String>

    @POST("emailAuthSuccessId")
    @FormUrlEncoded
    fun emailAuthSuccessId(
        @Field("email") email : String
    ) : Observable<String>

    @POST("emailAuthPasswd")
    @FormUrlEncoded
    fun emailAuthPasswd(
        @Field("uid") uid : String,
        @Field("email") email : String
    ) : Observable<String>

    @POST("emailAuthSuccessPasswd")
    @FormUrlEncoded
    fun emailAuthSuccessPasswd(
        @Field("email") email : String
    ) : Observable<String>

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

    @POST("groupNameChange")
    @FormUrlEncoded
    fun groupNameChange(
        @Field("newGroupName") newGroupName : String,
        @Field("gno") gno : Int
    ) : Observable<String>

    @Multipart
    @POST("groupPhotoChange")
    fun groupPhotoChange(
        @Part imagefile : MultipartBody.Part,
        @Part("gno") gno : RequestBody
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

    @POST("modifyPermission")
    @FormUrlEncoded
    fun modifyPermission(
        @Field("gno") gno : Int,
        @Field("nickname") nickname: String
    ) : Observable<String>

    @POST("checkPermission")
    @FormUrlEncoded
    fun checkPermission(
        @Field("gno") gno : Int?
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
        @Field("start_time") start_time : Time,
        @Field("latitude") latitude : Double?,
        @Field("longitude") longitude : Double?

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
        @Field("gno") gno : Int,
        @Field("latitude") latitude : Double?,
        @Field("longitude") longitude : Double?
    ) : Observable<String>

    /******************** 가계부 ********************/
    @POST("createExpenses")
    @FormUrlEncoded
    fun createExpenses(
        @Field("etitle") etitle : String,
        @Field("cost") cost : Int,
        @Field("date") date : Date,
        @Field("time") time : Time,
        @Field("gno") gno: Int
    ) : Observable<String>

    @POST("loadExpensesList")
    @FormUrlEncoded
    fun loadExpensesList(
        @Field("gno") gno : Int
    ) : Observable<String>

    @POST("modifyExpenses")
    @FormUrlEncoded
    fun modifyExpenses(
        @Field("eno") eno : Int,
        @Field("etitle") etitle : String,
        @Field("cost") cost : Int,
        @Field("date") date : Date,
        @Field("time") time : Time,
        @Field("gno") gno : Int
    ) : Observable<String>

    @POST("deleteExpenses")
    @FormUrlEncoded
    fun deleteExpenses(
        @Field("eno") eno : Int
    ) : Observable<String>
}
