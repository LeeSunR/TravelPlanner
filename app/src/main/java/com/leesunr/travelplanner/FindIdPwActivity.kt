package com.leesunr.travelplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class FindIdPwActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_id_pw)

        val pagerAdapter = PagerAdapter(supportFragmentManager)
        val pager = findViewById<ViewPager>(R.id.find_pager)
        pager.adapter = pagerAdapter

        val tab = findViewById<TabLayout>(R.id.tab_find_idpw)
        tab.setupWithViewPager(pager)
    }

    inner class PagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm)
    {
        val PAGE_MAX_CNT = 2

        override fun getCount(): Int {
            return PAGE_MAX_CNT
        }

        override fun getItem(position: Int): Fragment {
            val fragment = when(position)
            {
                0 -> FindIdFragment()
                1 -> FindPwFragment()
                else -> FindIdFragment()
            }
            return fragment
        }

        override fun getPageTitle(position: Int): CharSequence? {
            val title = when(position)
            {
                0 -> "아이디 찾기"
                1 -> "비밀번호 찾기"
                else -> "아이디 찾기"
            }
            return title
        }
    }




}
