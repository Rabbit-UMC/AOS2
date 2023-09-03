package com.example.myo_jib_sa.mypage

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myo_jib_sa.mypage.Tab.TabMyCommentFragment
import com.example.myo_jib_sa.mypage.Tab.TabMyPostFragment
import com.example.myo_jib_sa.mypage.Tab.TabProfileFragment

class MypageViewPagerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int=2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TabMyPostFragment()
            1 -> TabMyCommentFragment()
            else -> TabMyPostFragment()
        }
    }
}