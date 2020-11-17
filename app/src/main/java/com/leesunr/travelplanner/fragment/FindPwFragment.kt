package com.leesunr.travelplanner.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_find_pw.*

open class FindPwFragment : Fragment() {

    var email : String? = null
    var uid : String? = null
    lateinit var myAPI : INodeJS
    var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_pw, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        myAPI = RetrofitClient.instance.create(INodeJS::class.java)

        button_find_pw.setOnClickListener { view ->
            uid = edit_find_uid.text.toString()
            email = edit_find_email.text.toString()

            if(email!!.isNotEmpty() && uid!!.isNotEmpty()){
                compositeDisposable.add(myAPI.emailAuthPasswd(uid!!, email!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result ->
                            Toast.makeText(context, "이메일로 임시 비밀번호를 보냈습니다.", Toast.LENGTH_LONG).show()
                            activity!!.onBackPressed()
                        },
                        { error ->
                            Toast.makeText(context,"이메일 인증 실패", Toast.LENGTH_SHORT).show()
                            Log.d("emailAuth error", error.message)
                        }
                    )
                )
            } else
                Toast.makeText(context, "아이디와 이메일을 입력해주세요!", Toast.LENGTH_SHORT).show()
        }
    }
}
