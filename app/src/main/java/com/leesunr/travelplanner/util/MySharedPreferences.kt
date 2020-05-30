package com.leesunr.travelplanner.util

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {
    val PREFS_FILENAME1 = "prefs_access_token"
    val PREFS_FILENAME2 = "prefs_refresh_token"
    val PREFS_FILENAME3 = "weather_json_string"
    val PREFS_FILENAME4 = "group_confirmed"
    val PREFS_FILENAME5 = "main_group_number"

    val PREF_KEY_MY_EDITTEXT = "myEditText"
    val prefs_access: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME1, 0)
    val prefs_refresh: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME2, 0)
    val prefs_weather: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME3, 0)
    val prefs_group_confirmed: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME4, 0)
    val prefs_main_group_number: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME5, 0)
    /* 파일 이름과 EditText를 저장할 Key 값을 만들고 prefs 인스턴스 초기화 */

    var myAccessToken: String?
        get() = prefs_access.getString(PREF_KEY_MY_EDITTEXT, "")
        set(value) = prefs_access.edit().putString(PREF_KEY_MY_EDITTEXT, value).apply()

    var myRefreshToken: String?
        get() = prefs_refresh.getString(PREF_KEY_MY_EDITTEXT, "")
        set(value) = prefs_refresh.edit().putString(PREF_KEY_MY_EDITTEXT, value).apply()

    var weatherJsonString: String?
        get() = prefs_weather.getString(PREF_KEY_MY_EDITTEXT, null)
        set(value) = prefs_weather.edit().putString(PREF_KEY_MY_EDITTEXT, value).apply()

    var groupConfirmed: String?
        get() = prefs_group_confirmed.getString(PREF_KEY_MY_EDITTEXT, "{}")
        set(value) = prefs_group_confirmed.edit().putString(PREF_KEY_MY_EDITTEXT, value).apply()

    var mainGroupNumber: Int
        get() = prefs_main_group_number.getInt(PREF_KEY_MY_EDITTEXT, -1)
        set(value) = prefs_main_group_number.edit().putInt(PREF_KEY_MY_EDITTEXT, value).apply()
    /* get/set 함수 임의 설정. get 실행 시 저장된 값을 반환하며 default 값은 ""
     * set(value) 실행 시 value로 값을 대체한 후 저장 */
}