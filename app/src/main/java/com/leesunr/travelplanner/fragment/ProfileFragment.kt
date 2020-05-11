package com.leesunr.travelplanner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.activity.LoginActivity
import com.leesunr.travelplanner.model.User
import com.leesunr.travelplanner.util.App
import com.leesunr.travelplanner.util.JWT
import kotlinx.android.synthetic.main.fragment_profile.*
import java.text.SimpleDateFormat

class ProfileFragment : Fragment() {
    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //유저 정보 출력
        val payload = JWT.decoded(App.prefs_access.myAccessToken)
        val user = User().parseUser(payload!!)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd");
        profile_nickname.text = user.nickname
        profile_id.text = user.id
        profile_regdate.text = "가입일 : ${dateFormat.format(user.regdate)}"

        //로그아웃 버튼
        profile_logout.setOnClickListener {
            val dialog = AlertDialog.Builder(mContext)
            dialog.setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("예") { dialogInterface, i->
                    App.prefs_access.myAccessToken = null
                    App.prefs_refresh.myRefreshToken = null
                    val intent = Intent(mContext, LoginActivity::class.java)
                    mContext.startActivity(intent)
                    ActivityCompat.finishAffinity(mContext as Activity);
                }
                .setNegativeButton("아니오") { dialogInterface, i-> }
                .show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

}
