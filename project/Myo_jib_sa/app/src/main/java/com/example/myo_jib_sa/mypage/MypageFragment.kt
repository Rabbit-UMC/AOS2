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
            val badgeDrawable = tab.orCreateBadge
            badgeDrawable.backgroundColor = Color.parseColor("#FF2D4AD0")
            when (position) {
                0 -> {
                    tab.text = "프로필"
                    badgeDrawable.isVisible = false // 초기에는 뱃지를 보이지 않도록 설정

                }
                1 -> {
                    tab.text = "작성한 글"
                    badgeDrawable.isVisible = false // 초기에는 뱃지를 보이지 않도록 설정
                }
                2 -> {
                    tab.text = "댓글단 글"
                    badgeDrawable.isVisible = false // 초기에는 뱃지를 보이지 않도록 설정
                }
            }
        }.attach()

        // 탭 뱃지 설정
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val badgeDrawable = it.orCreateBadge
                    badgeDrawable.isVisible = true // 탭이 선택되면 뱃지를 보이도록 설정

                    // 뱃지 크기 조절
                    val badgeSize = resources.getDimensionPixelSize(R.dimen.custom_badge_size) // 원하는 크기로 변경
                    val layoutParams = badgeDrawable.bounds // 기존 뱃지의 bounds 가져오기
                    layoutParams.right = layoutParams.left + badgeSize // 너비 조절
                    layoutParams.bottom = layoutParams.top + badgeSize // 높이 조절
                    badgeDrawable.bounds = layoutParams // 크기 조절된 bounds 적용
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    val badgeDrawable = it.orCreateBadge
                    badgeDrawable.isVisible = false // 탭이 선택 해제되면 뱃지를 숨기도록 설정
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Do nothing
            }
        })



        return binding.root
    }



}