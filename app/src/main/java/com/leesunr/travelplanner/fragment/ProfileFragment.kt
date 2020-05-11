package com.leesunr.travelplanner.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.leesunr.travelplanner.R

class ProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        //if (context is OnFragmentInteractionListener) {
        //    listener = context
        //} else {
        //    throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        //}
    }

}
