package com.leesunr.travelplanner.activity

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.leesunr.travelplanner.DBHelper.ChatDBHelper
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.adapter.LockPlanRcyAdapter
import com.leesunr.travelplanner.adapter.MessageRcyAdapter
import com.leesunr.travelplanner.model.Message
import com.leesunr.travelplanner.model.Plan
import kotlinx.android.synthetic.main.activity_group_chat.*
import kotlinx.android.synthetic.main.activity_lock_screen.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class LockScreenActivity : AppCompatActivity() {


    private var planListToday:ArrayList<Plan> = ArrayList<Plan>()
    private var planListTomorrow:ArrayList<Plan> = ArrayList<Plan>()
    private var adapterToday: LockPlanRcyAdapter = LockPlanRcyAdapter(this, planListToday)
    private var adapterTomorrow: LockPlanRcyAdapter = LockPlanRcyAdapter(this, planListTomorrow)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val attrib = window.attributes
        attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

        rcv_lock_plan_today.layoutManager = LinearLayoutManager(this)
        rcv_lock_plan_tomorrow.layoutManager = LinearLayoutManager(this)
        rcv_lock_plan_today.adapter = adapterToday
        rcv_lock_plan_tomorrow.adapter = adapterTomorrow

        //TODO 주그룹 플랜 LIST 가져오기

        if(planListToday.isEmpty())
            tv_lock_plan_today_info.visibility = View.VISIBLE
        if(planListTomorrow.isEmpty())
            tv_lock_plan_tomorrow_info.visibility = View.VISIBLE

        if (intent.getBooleanExtra("stop",false)){
            val intent = Intent(this, LockService::class.java)
            stopService(intent)
            finish()
        }


        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // this flag do=Semi-transparent bars temporarily appear and then hide again
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Make Content Appear Behind the status  Bar
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // it Make Content Appear Behind the Navigation Bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (supportActionBar != null) {
            supportActionBar!!.hide();
        }

        tv_lock_screen_date.text = SimpleDateFormat("MM월 dd일 (E)").format(Date())
        tv_lock_screen_time.text = SimpleDateFormat("HH시 mm분").format(Date())
        btn_lock_screen_unlock.setOnClickListener {
            finish()
        }

        setShowWhenLocked(true)
        setTurnScreenOn(true)
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager.requestDismissKeyguard(this, null)
    }

    override fun onPause() {
        super.onPause()
        //finish()
    }
}

class LockReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(Intent.ACTION_SCREEN_ON)) {
            Log.e("ee","ee")
            val i = Intent(context, LockScreenActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(i)
        }
    }
}


class LockService : Service() {
    var receiver : LockReceiver? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        receiver = LockReceiver()
        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_ON)
        registerReceiver(receiver,intentFilter)

        val intent = Intent(applicationContext, LockScreenActivity::class.java)
        intent.putExtra("stop",true)
        val pi = PendingIntent.getActivity(applicationContext,System.currentTimeMillis().toInt(),intent,0)

        val mChannel = NotificationChannel("lockChannel", "잠금화면 사용중 알림", NotificationManager.IMPORTANCE_HIGH)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(mChannel)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "lockChannel")
        builder.setSmallIcon(android.R.drawable.stat_notify_sync_noanim)
        builder.setContentText("잠금화면 표시를 위하여 앱이 실행중입니다")
        builder.addAction(android.R.drawable.ic_menu_share, "STOP", pi)
        startForeground(1, builder.build())
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver)
        }
        super.onDestroy()
    }

}