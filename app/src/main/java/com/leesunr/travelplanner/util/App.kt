package com.leesunr.travelplanner.util

import android.app.Application

class App : Application() {
    companion object {
        lateinit var prefs_access : MySharedPreferences
        lateinit var prefs_refresh : MySharedPreferences
    }
    /* prefs라는 이름의 MySharedPreferences 하나만 생성할 수 있도록 설정. */

    override fun onCreate() {
        prefs_access =
            MySharedPreferences(applicationContext)
        prefs_refresh =
            MySharedPreferences(applicationContext)
        super.onCreate()
    }
}