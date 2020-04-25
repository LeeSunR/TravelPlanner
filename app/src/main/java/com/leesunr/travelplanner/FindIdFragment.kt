package com.leesunr.travelplanner

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.auth_dialog.*
import kotlinx.android.synthetic.main.fragment_find_id.*


open class FindIdFragment : Fragment() {

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
        button_find_id.setOnClickListener { view ->
            var builder = AlertDialog.Builder(this.requireContext())
            builder.setTitle("이메일 인증")
            builder.setIcon(R.mipmap.ic_launcher)

            var v1 = layoutInflater.inflate(R.layout.auth_dialog, null)
            builder.setView(v1)

            var listener = object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    var alert = dialog as AlertDialog
                    var edit1: EditText? = alert.findViewById<EditText>(R.id.auth_dialog_input_number)
                }
            }

//            builder.setPositiveButton("확인", listener)
//            builder.setNegativeButton("취소", null)

            builder.show()
        }

    }


}
