package com.leesunr.travelplanner.util

import android.app.Application
import android.content.Context

class App : Application() {

    companion object {
        lateinit var context: Context
        lateinit var prefs_access : MySharedPreferences
        lateinit var prefs_refresh : MySharedPreferences
        lateinit var prefs_weather : MySharedPreferences
        lateinit var groupConfirmed : MySharedPreferences
    }
    /* prefs라는 이름의 MySharedPreferences 하나만 생성할 수 있도록 설정. */

    override fun onCreate() {
        prefs_access =
            MySharedPreferences(applicationContext)
        prefs_refresh =
            MySharedPreferences(applicationContext)
        prefs_weather =
            MySharedPreferences(applicationContext)
        groupConfirmed =
            MySharedPreferences(applicationContext)

        context = applicationContext
        super.onCreate()
    }
}