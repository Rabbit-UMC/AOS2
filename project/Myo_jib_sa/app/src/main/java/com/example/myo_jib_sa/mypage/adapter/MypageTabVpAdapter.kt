package com.example.myo_jib_sa.mypage.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myo_jib_sa.mypage.tab.MypageTabCommentFragment
import com.example.myo_jib_sa.mypage.tab.MypageTabPostFragment

class MypageTabVpAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int=2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MypageTabPostFragment()
            1 -> MypageTabCommentFragment()
            else -> MypageTabPostFragment()
        }
    }
}