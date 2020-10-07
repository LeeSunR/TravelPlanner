package com.leesunr.travelplanner.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.leesunr.travelplanner.DBHelper.ChatDBHelper
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.adapter.AllPlanRcyAdapter
import com.leesunr.travelplanner.model.Group
import com.leesunr.travelplanner.model.Plan
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import com.leesunr.travelplanner.util.App
import kotlinx.android.synthetic.main.activity_group_main.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat

class GroupMainActivity : AppCompatActivity() {
    lateinit var group: Group
    lateinit var myBroadcastReceiver:MyBroadcastReceiver
    lateinit var planBroadcastReceiver:PlanBroadcastReceiver

    lateinit var planAdapter : AllPlanRcyAdapter
    lateinit var allPlanList : ArrayList<ArrayList<Plan>>
    lateinit var planList : ArrayList<Plan>

    companion object {
        var is_writable : Int? = 0
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_main)

        if(intent.hasExtra("group")){
            group = intent.getParcelableExtra<Group>("group")
            button_group_title.text = group.gname
        }

        myBroadcastReceiver = MyBroadcastReceiver(group_chat_alarm,group)
        planBroadcastReceiver = PlanBroadcastReceiver(this, group)

        val chatFilter = IntentFilter()
        chatFilter.addAction("chatReceived")
        registerReceiver(myBroadcastReceiver, chatFilter)

        val planFilter = IntentFilter()
        planFilter.addAction("planReceived")
        registerReceiver(planBroadcastReceiver, planFilter)

        planList = ArrayList<Plan>()
        allPlanList = ArrayList<ArrayList<Plan>>()

        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this, myAPI.checkPermission(group.gno),
            { result ->
                val jsonArray = JSONArray(result)
                val jsonObject = jsonArray.getJSONObject(0)
                val owner = jsonObject.getInt("IS_OWNER")
                is_writable = jsonObject.getInt("IS_WRITABLE")

                if(owner == 0) {
                    button_group_setting.visibility = View.GONE
//                    setMarginsInDp(button_group_chat, 0, 0, 0, 0)
                }
                if(is_writable == 0){
                    button_group_plan_add.visibility = View.GONE
                }
            },
            { error ->
                Log.e("checkPermission Error", error)
                return@call true
            })

        button_group_back.setOnClickListener {
            if(intent.hasExtra("group")){
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("groupFragment", "")
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            } else finish()
        }

        button_group_setting.setOnClickListener {
            val intent = Intent(this, GroupSettingActivity::class.java)
            intent.putExtra("gno", group.gno)
            intent.putExtra("groupName", group.gname)
            intent.putExtra("groupImage", group.gphotourl)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        button_group_plan_add.setOnClickListener {
            val intent = Intent(this, GroupPlanAddActivity::class.java)
            intent.putExtra("gno", group.gno)
            startActivity(intent)
        }

        button_group_chat.setOnClickListener {
            val intent = Intent(this, GroupChatActivity::class.java)
            intent.putExtra("group", group)
            startActivity(intent)
        }

        button_group_more.setOnClickListener {
            var menu = PopupMenu(this, button_group_more)
            menu.inflate(R.menu.group_main_more_menu)
            menu.setOnMenuItemClickListener { item->
                when(item.itemId){
                    R.id.group_main_more_share->{
                        val bitmap = getRecyclerViewScreenshot(recyclerView_all_plan)

                        val file = File(getExternalFilesDir("tmp").path+"plan.png")
                        val fileStream = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0, FileOutputStream(file))
                        fileStream.close()

                        //임시 저장된 이미지 전송
                        var uri = Uri.parse(getExternalFilesDir("tmp").path+"plan.png")

                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        startActivity(intent)
                    }
                    R.id.group_main_more_main_group->{
                        val dialog = AlertDialog.Builder(this)
                        dialog.setTitle("안내")
                            .setMessage("이 그룹을 주그룹으로 설정하시겠습니까?")
                            .setPositiveButton("예") { dialog, which ->
                                App.mainGroupNumber.mainGroup = Gson().toJson(group)
                            }
                            .setNegativeButton("아니요"){ dialog, which -> }
                            .show()
                    }
                }

                return@setOnMenuItemClickListener true
            }
            menu.show()

        }

        button_group_expense.setOnClickListener {
            val intent = Intent(this, GroupExpensesActivity::class.java)
            intent.putExtra("gno", group.gno)
            startActivity(intent)
        }

        loadPlanList(group.gno!!)
    }

    fun setMarginsInDp(view: View, left: Int, top: Int, right: Int, bottom: Int){
        if(view.layoutParams is ViewGroup.MarginLayoutParams){
            val screenDesity: Float = view.context.resources.displayMetrics.density
            val params: ViewGroup.MarginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(left*screenDesity.toInt(), top*screenDesity.toInt(), right*screenDesity.toInt(), bottom*screenDesity.toInt())
            view.requestLayout()
        }
    }

//  일정 목록 출력
    private fun loadPlanList(gno : Int){
        val dateFormat = SimpleDateFormat("yyyy-MM-dd");
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(this, myAPI.loadPlanList(gno),
            { result ->
                try {
                    val jsonArray = JSONArray(result)
                    var cur_date : String? = null
                    var new_date : String? = null
                    // 첫번째 일정의 날짜 세팅
                    var jsonObject = jsonArray.getJSONObject(0)
                    var plan = Plan().parsePlan(jsonObject)
                    cur_date = dateFormat.format(plan.start_date)

                    for(i in 0 until jsonArray.length()){
                        jsonObject = jsonArray.getJSONObject(i)
                        plan = Plan().parsePlan(jsonObject)
                        new_date = dateFormat.format(plan.start_date)

                        if(cur_date.equals(new_date)) planList.add(plan)
                        else {
                            cur_date = new_date
                            allPlanList.add(planList)

                            planList = ArrayList<Plan>()
                            planList.add(plan)
                        }
                    }
                    allPlanList.add(planList)

                    //레이아웃매니저를 설정해줍니다.
                    planAdapter = AllPlanRcyAdapter(this, allPlanList)
                    recyclerView_all_plan.adapter = planAdapter

                    val lm = LinearLayoutManager(this)
                    recyclerView_all_plan.layoutManager = lm
                    recyclerView_all_plan.setHasFixedSize(true)

                    val json = JSONObject(App.groupConfirmed.groupConfirmed)
                    App.groupConfirmed.groupConfirmed = json.put(group.gno.toString(),1).toString()

                    Log.d("PlanList", "success")
                } catch (e : JSONException){
                    if(JSONArray(result).length() == 0)
                        group_main_no_plan.visibility = View.VISIBLE
                }
            },
            { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT)
                Log.e("PlanList error", error)
                return@call true
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myBroadcastReceiver)
        unregisterReceiver(planBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        group_chat_alarm.visibility=View.GONE
        val unconfirmedMessage = ChatDBHelper(
            this
        ).unconfirmedMessage(group.gno!!)
        Log.e("result",unconfirmedMessage.toString())
        if (unconfirmedMessage)
            group_chat_alarm.visibility=View.VISIBLE
    }

    private fun getRecyclerViewScreenshot(view: RecyclerView): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val bitmap = Bitmap.createBitmap(view.width, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE);
        view.draw(canvas)
        return bitmap
    }

    //새로운 채팅 수신 브로드케스트 리시버
    class MyBroadcastReceiver(view: View, group:Group) : BroadcastReceiver() {
        private val view = view
        private val group = group
        override fun onReceive(context: Context, intent: Intent) {
            var gno = intent.getIntExtra("gno", -1)
            if (gno == group.gno) view.visibility = View.VISIBLE
        }
    }

    //일정 변경 수신 브로드케스트 리시버
    class PlanBroadcastReceiver(activity: GroupMainActivity, group:Group) : BroadcastReceiver() {
        private val activity = activity
        private val group = group
        override fun onReceive(context: Context, intent: Intent) {
            var gno = intent.getIntExtra("gno", -1)
            if (gno == group.gno) {
                activity.planList = ArrayList<Plan>()
                activity.allPlanList = ArrayList<ArrayList<Plan>>()
                activity.loadPlanList(group.gno!!)
            }
        }
    }
}
