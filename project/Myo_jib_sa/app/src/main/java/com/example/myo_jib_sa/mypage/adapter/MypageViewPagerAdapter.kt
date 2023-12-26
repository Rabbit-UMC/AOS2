package com.example.myo_jib_sa.mypage.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myo_jib_sa.mypage.tab.MyPageTabCommentFragment
import com.example.myo_jib_sa.mypage.tab.MyPageTabPostFragment

class MypageViewPagerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int=2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyPageTabPostFragment()
            1 -> MyPageTabCommentFragment()
            else -> MyPageTabPostFragment()
        }
    }
}