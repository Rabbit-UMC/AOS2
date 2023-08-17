package com.example.myo_jib_sa.mypage

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.FragmentMypageBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MypageFragment : Fragment() {
    lateinit var binding: FragmentMypageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMypageBinding.inflate(layoutInflater)

        val tabLayout = binding.myPageTab
        val viewPager = binding.myPageViewpager

        // 어댑터 생성
        val adapter = MypageViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "프로필"

                }
                1 -> {
                    tab.text = "작성한 글"
                }
                2 -> {
                    tab.text = "댓글단 글"
                }
            }
        }.attach()

        // 탭 뱃지 설정
      /*  tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {

                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Do nothing
            }
        })*/



        return binding.root
    }



}