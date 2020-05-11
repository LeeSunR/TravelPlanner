package com.leesunr.travelplanner.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.activity.GroupMainActivity
import kotlinx.android.synthetic.main.fragment_group_list.*


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
        btn_goto_group_main.setOnClickListener {
            val nextIntent = Intent(mContext, GroupMainActivity::class.java)
            startActivity(nextIntent)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}
