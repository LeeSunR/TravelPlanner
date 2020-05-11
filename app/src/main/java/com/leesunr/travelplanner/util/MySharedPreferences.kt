package com.leesunr.travelplanner.util

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {
    val PREFS_FILENAME1 = "prefs_access_token"
    val PREFS_FILENAME2 = "prefs_refresh_token"
    val PREF_KEY_MY_EDITTEXT = "myEditText"
    val prefs_access: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME1, 0)
    val prefs_refresh: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME2, 0)
    /* 파일 이름과 EditText를 저장할 Key 값을 만들고 prefs 인스턴스 초기화 */

    var myAccessToken: String?
        get() = prefs_access.getString(PREF_KEY_MY_EDITTEXT, "")
        set(value) = prefs_access.edit().putString(PREF_KEY_MY_EDITTEXT, value).apply()

    var myRefreshToken: String?
        get() = prefs_refresh.getString(PREF_KEY_MY_EDITTEXT, "")
        set(value) = prefs_refresh.edit().putString(PREF_KEY_MY_EDITTEXT, value).apply()
    /* get/set 함수 임의 설정. get 실행 시 저장된 값을 반환하며 default 값은 ""
     * set(value) 실행 시 value로 값을 대체한 후 저장 */
}