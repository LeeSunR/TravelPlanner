package com.leesunr.travelplanner.Retrofit

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface INodeJS {
    @POST("signup")
    @FormUrlEncoded
    fun signupUser(
        @Field("email") email: String,
        @Field("nickname") nickname: String,
        @Field("password") password: String,
        @Field("photourl") photourl: String
    ): Observable<String>

    @POST("login")
    @FormUrlEncoded
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Observable<String>
}