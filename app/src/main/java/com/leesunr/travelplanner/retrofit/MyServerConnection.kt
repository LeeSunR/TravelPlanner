package com.leesunr.travelplanner.retrofit

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat
import com.leesunr.travelplanner.util.App
import com.leesunr.travelplanner.activity.LoginActivity
import com.leesunr.travelplanner.util.JWT
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

object MyServerAPI {
    fun call(activity: Activity, myAPI:Observable<String>, ok: ((String)->Unit), error: ((String)->Boolean)) {

        val now = System.currentTimeMillis() / 1000
        val accessExp = JWT.decoded(App.prefs_access.myAccessToken!!)?.getString("exp")?.toLong()
        val refreshExp = JWT.decoded(App.prefs_refresh.myRefreshToken!!)?.getString("exp")?.toLong()
        Log.e("time","ac : $accessExp rf : $refreshExp now : $now")

        if ( (refreshExp==null || accessExp==null) || (refreshExp!! < now && accessExp!! < now) ){
            if (error.invoke("invalid_token")) {
                App.prefs_access.myAccessToken = null
                App.prefs_refresh.myRefreshToken = null
                val intent = Intent(activity, LoginActivity::class.java)
                activity.startActivity(intent)
                ActivityCompat.finishAffinity(activity)
            }
        }else {
            val compositeDisposable = CompositeDisposable()
            compositeDisposable.add(myAPI
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { it ->
                        ok?.invoke(it)
                    },
                    { it ->
                        if(error.invoke(it.toString()) && it.message!!.contains("401")){ //만료된 토큰이라면
                            App.prefs_access.myAccessToken = null
                            App.prefs_refresh.myRefreshToken = null
                            val intent = Intent(activity, LoginActivity::class.java)
                            activity.startActivity(intent)
                            ActivityCompat.finishAffinity(activity);
                        }
                    }
                )
            )
        }
    }
}
