package com.leesunr.travelplanner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.leesunr.travelplanner.adapter.GroupListAdapter
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.activity.GroupCreateActivity
import com.leesunr.travelplanner.activity.GroupMainActivity
import com.leesunr.travelplanner.model.Group
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import com.leesunr.travelplanner.util.App
import kotlinx.android.synthetic.main.fragment_group_list.*
import org.json.JSONArray

class GroupListFragment : Fragment() {
    private var mContext: Context? = null
    lateinit var planBroadcastReceiver: PlanBroadcastReceiver
    lateinit var groupAdapter:GroupListAdapter
    lateinit var groupList:ArrayList<Group>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_group_list, null)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        planBroadcastReceiver = PlanBroadcastReceiver(this)
        val planFilter = IntentFilter()
        planFilter.addAction("planReceived")
        mContext!!.registerReceiver(planBroadcastReceiver, planFilter)

        btn_goto_group_create.setOnClickListener { view ->
            val nextIntent = Intent(mContext, GroupCreateActivity::class.java)
            startActivity(nextIntent)
        }
        listView_group.setOnItemLongClickListener { parent, view, position, id ->
            val dialog = AlertDialog.Builder(mContext)
            dialog.setTitle("안내")
                .setMessage("이 그룹을 주그룹으로 설정하시겠습니까?")
                .setPositiveButton("예") { dialog, which ->
                    App.mainGroupNumber.mainGroup = Gson().toJson(groupList[position])
                }
                .setNegativeButton("아니요"){ dialog, which ->

                }
                .show()

            return@setOnItemLongClickListener true
        }
//        loadGroupList()
    }

    override fun onResume() {
        super.onResume()
        loadGroupList()
    }

    fun loadGroupList(){
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(mContext as Activity, myAPI.loadGroupList(),
            { result ->
                val jsonArray = JSONArray(result)
                groupList = ArrayList<Group>()
                for(i in 0 until jsonArray.length()){
                    val jsonObject = jsonArray.getJSONObject(i)
                    val group = Group().parseGroup(jsonObject)
                    groupList.add(group)
                }

                groupAdapter = GroupListAdapter(mContext as Activity, groupList)
                listView_group.adapter = groupAdapter

                listView_group.setOnItemClickListener { parent, view, position, id ->
                    val intent = Intent(mContext, GroupMainActivity::class.java)
                    intent.putExtra("group", groupList[position])
                    startActivity(intent)
                }
                Log.d("GroupList: ", "success")
            },
            { error ->
                Toast.makeText(mContext, "그룹 리스트를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                Log.d("group list error", error)
                return@call true
            }
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDestroy() {
        super.onDestroy()
        mContext!!.unregisterReceiver(planBroadcastReceiver)
    }

    //일정 변경 수신 브로드케스트 리시버
    class PlanBroadcastReceiver(groupListFragment: GroupListFragment) : BroadcastReceiver() {
        private val groupListFragment = groupListFragment
        override fun onReceive(context: Context, intent: Intent) {
            groupListFragment.groupAdapter.notifyDataSetChanged()
        }
    }
}
