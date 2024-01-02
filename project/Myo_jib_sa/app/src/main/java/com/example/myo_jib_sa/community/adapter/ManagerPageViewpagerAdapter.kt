package com.example.myo_jib_sa.community.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myo_jib_sa.community.ManagerMissionFragment
import com.example.myo_jib_sa.community.api.manager.ManagerMissionJoinRequest

class ManagerPageViewpagerAdapter (fragmentActivity: FragmentActivity, val dataList:List<ManagerMissionJoinRequest>) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = dataList.size

    override fun createFragment(position: Int): Fragment {
        Log.d("뷰페이저 어댑터 실행", "뷰페이저 어댑터 fragment.updateView(dataList[position]) 실행")
        val fragment = ManagerMissionFragment()
        fragment.updateView(dataList[position])

        return fragment
    }


}