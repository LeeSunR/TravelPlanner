package com.leesunr.travelplanner.fragment

import android.app.Activity
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
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_find_id.*


open class FindIdFragment : Fragment() {

    var email : String? = null
    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_find_id, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        myAPI = RetrofitClient.instance.create(INodeJS::class.java)

        button_find_id.setOnClickListener { view ->
            email = edit_find_email.text.toString()
            if(email!!.isNotEmpty()){

                compositeDisposable.add(myAPI.emailAuth(email!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result ->
                            val authNum = result

                            var builder = AlertDialog.Builder(this.requireContext())
                            builder.setTitle("이메일 인증")
                            builder.setIcon(R.mipmap.ic_launcher)

                            var v1 = layoutInflater.inflate(R.layout.auth_dialog, null)
                            builder.setView(v1)

                            var alert = builder.create()
                            countDownTimer(alert, authNum)
                        },
                        { error ->
                            Toast.makeText(context,"이메일 인증 실패", Toast.LENGTH_SHORT).show()
                            Log.d("emailAuth error", error.message)
                        }
                    )
                )
//                val myAPI = RetrofitClient.instance.create(INodeJS::class.java)
//                MyServerAPI.call(context as Activity, myAPI.emailAuth(email!!),
//                    { result ->
//                        // 1. 서버에서 랜덤 생성한 6자리 숫자 받아옴
//                        // 2. 입력한 인증번호와 일치한지 검사 후 bool 변수에 true 또는 false 대입
//                        // 3. OK 버튼 클릭이벤트 처리하기
//                        Log.e("test", "test02030")
//                        val authNum = result.toInt()
//                        Log.e("authNum : ", authNum.toString())
//
//                        var builder = AlertDialog.Builder(this.requireContext())
//                        builder.setTitle("이메일 인증")
//                        builder.setIcon(R.mipmap.ic_launcher)
//
//                        var v1 = layoutInflater.inflate(R.layout.auth_dialog, null)
//                        builder.setView(v1)
//
//                        var alert = builder.create()
//                        countDownTimer(alert, authNum)
//                    },
//                    { error ->
//                        return@call true
//                    })
            } else
                Toast.makeText(context, "이메일을 입력해주세요!", Toast.LENGTH_SHORT).show()
        }
    }

    val MILLISINFUTURE = 300 * 1000 //총 시간 (300초 = 5분)
    val COUNT_DOWN_INTERVAL = 1000 //onTick 메소드를 호출할 간격 (1초)
    var countDownTimer: CountDownTimer? = null

    fun countDownTimer(dialog : AlertDialog, authNum : String){
        dialog.show()
        var input_num : EditText? = null
        var count : TextView? = dialog.findViewById(R.id.auth_dialog_time_counter)
        var okBtn : Button? = dialog.findViewById(R.id.auth_dialog_ok_button)

        countDownTimer = object : CountDownTimer(
            MILLISINFUTURE.toLong(),
            COUNT_DOWN_INTERVAL.toLong()
        ) {
            override fun onTick(millisUntilFinished: Long) { //(300초에서 1초 마다 계속 줄어듬)
                val emailAuthCount = millisUntilFinished / 1000
                Log.d("EmailAuthCount", emailAuthCount.toString() + "")
                if (emailAuthCount - emailAuthCount / 60 * 60 >= 10) { //초가 10보다 크면 그냥 출력
                    count!!.text = "${emailAuthCount / 60} : ${emailAuthCount % 60}"
                } else { //초가 10보다 작으면 앞에 '0' 붙여서 같이 출력. ex) 02,03,04...
                    count!!.text = "${emailAuthCount / 60} : 0${emailAuthCount % 60}"
                }
            }

            override fun onFinish() { //시간이 다 되면 다이얼로그 종료
                dialog.cancel()
                countDownTimer!!.cancel()
            }
        }.start()

        okBtn!!.setOnClickListener {
            input_num = dialog.findViewById(R.id.auth_dialog_input_number)
            if(input_num!!.text.toString().equals(authNum)) {
                myAPI = RetrofitClient.instance.create(INodeJS::class.java)

                compositeDisposable.add(myAPI.emailAuthSuccess(email!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result ->
                            Toast.makeText(context, "아이디 : ${result}", Toast.LENGTH_LONG).show()
                            dialog.cancel()
                            countDownTimer!!.cancel()
                        },
                        { error ->
                            Toast.makeText(context,"아이디를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                            Log.d("emailAuth error", error.message)
                        }
                    )
                )
            } else {
                Toast.makeText(context, "인증번호가 틀립니다!", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setOnDismissListener{
            countDownTimer!!.cancel()
        }
    }
}
