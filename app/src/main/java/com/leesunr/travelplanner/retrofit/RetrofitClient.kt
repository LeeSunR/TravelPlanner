package com.leesunr.travelplanner.retrofit

import android.util.Log
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.util.App
import com.leesunr.travelplanner.util.JWT
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object RetrofitClient {
    private var ourInstance: Retrofit? = null
    val instance: Retrofit
        get() {
            if (ourInstance == null)
                ourInstance = Retrofit.Builder()
                    .baseUrl(App.context.resources.getString(R.string.server_base_url))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
            return ourInstance!!
        }
}

//엑세스 토큰 클라이언트
object RetrofitClientWithAccessToken {
    private var ourInstance: Retrofit? = null
    val instance: Retrofit
        get() {
            App.context.resources
            if (ourInstance == null)
                ourInstance = Retrofit.Builder()
                    .client(OkHttpClient().newBuilder().addInterceptor(AccessTokenInterceptor()).authenticator(AccessTokenAuthenticator()).build())
                    .baseUrl(App.context.resources.getString(R.string.server_base_url))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
            return ourInstance!!
        }
}

//리프레시 토큰 클라이언트
object RetrofitClientWithRefreshToken {
    private var ourInstance: Retrofit? = null
    val instance: Retrofit
        get() {
            if (ourInstance == null)
                ourInstance = Retrofit.Builder()
                    .client(OkHttpClient().newBuilder().addInterceptor(RefreshTokenInterceptor()).build())
                    .baseUrl(App.context.resources.getString(R.string.server_base_url))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
            return ourInstance!!
        }
}

//엑세스 토큰 인터셉터
class AccessTokenInterceptor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val newRequest:Request = request.newBuilder().addHeader("Authorization", App.prefs_access.myAccessToken).build()

        val response = chain.proceed(newRequest)
        val contentType = response.body()!!.contentType()
        val content = response.body()!!.string()

        //결과에 토큰이 있으면 저장
        try {
            val jsonObject = JSONObject(content)
            val access_token = jsonObject.getString("access_token")
            val refresh_token = jsonObject.getString("refresh_token")
            App.prefs_access.myAccessToken = access_token
            App.prefs_refresh.myRefreshToken = refresh_token
            Log.d("[token renewal]","ok")
        }catch (e:Exception){
        }

        val wrappedBody = ResponseBody.create(contentType, content)
        return response.newBuilder().body(wrappedBody).build()
    }
}

//리프레시 토큰 인터셉터
class RefreshTokenInterceptor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val newRequest:Request = request.newBuilder().addHeader("Authorization", App.prefs_refresh.myRefreshToken).build()
        return chain.proceed(newRequest)
    }
}

//엑세스토큰 재발급
class AccessTokenAuthenticator () : Authenticator {
    override fun authenticate(route: Route, response: Response): Request? {
        if(response.code()==401){ //엑세스 토큰이 만료됨

            Log.i("[Authenticator]","trying renewal accessToken")

            val newAccessToken = runBlocking {
                GlobalScope.async(Dispatchers.Default) {
                    renewalAccessToken()
                }.await()
            }

            if(newAccessToken==null) return null
            else {
                App.prefs_access.myAccessToken = newAccessToken
                return response.request().newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", newAccessToken)
                    .build() //재요청
            }
        }
        return null
    }

    private suspend inline fun renewalAccessToken(): String? = suspendCoroutine {continuation ->

        val now = System.currentTimeMillis() / 1000
        val exp = JWT.decoded(App.prefs_refresh.myRefreshToken!!)?.getString("exp")?.toLong()

        if(exp!=null && exp!! > now){ //클라이언트에서 만료시간 계산
            val myAPI = RetrofitClientWithRefreshToken.instance.create(INodeJS::class.java)
            CompositeDisposable().add(myAPI.renewalAccessToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { ok ->
                        Log.i("[Authenticator]","엑세스 토큰이 재발급 되었습니다. : $ok")
                        continuation.resume(JSONObject(ok).getString("access_token"))
                    },
                    {
                        continuation.resume(null)
                    }
                )
            )
        }
        else{
            continuation.resume(null)
        }
    }
}