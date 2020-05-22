package com.leesunr.travelplanner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.leesunr.travelplanner.adapter.GroupListAdapter
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.activity.GroupCreateActivity
import com.leesunr.travelplanner.activity.GroupMainActivity
import com.leesunr.travelplanner.model.Group
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import kotlinx.android.synthetic.main.fragment_group_list.*
import org.json.JSONArray

class GroupListFragment : Fragment() {
    private var mContext: Context? = null

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

        btn_goto_group_create.setOnClickListener { view ->
            val nextIntent = Intent(mContext, GroupCreateActivity::class.java)
            startActivity(nextIntent)
        }
        loadGroupList()
    }

    fun loadGroupList(){
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(mContext as Activity, myAPI.loadGroupList(),
            { result ->
                val jsonArray = JSONArray(result)
                var groupList = arrayListOf<Group>()

                for(i in 0 until jsonArray.length()){
                    val jsonObject = jsonArray.getJSONObject(i)
                    val group = Group().parseGroup(jsonObject)
                    groupList.add(group)
                }

                var groupAdapter = GroupListAdapter(mContext as Activity, groupList)
                listView_group.adapter = groupAdapter

                listView_group.setOnItemClickListener { parent, view, position, id ->
                    val intent = Intent(mContext, GroupMainActivity::class.java)
                    intent.putExtra("group", groupList[position])
                    startActivity(intent)
                }
                Log.e("GroupList: ", "success")
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
}
