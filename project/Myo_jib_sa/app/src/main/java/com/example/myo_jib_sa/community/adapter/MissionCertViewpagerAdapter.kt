package com.example.myo_jib_sa.community.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myo_jib_sa.community.Retrofit.missionCert.MissionResponse
import com.example.myo_jib_sa.community.missionCert.MissionCertificationPostFragment

class MissionCertViewpagerAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val dataList:MutableList<MissionResponse> = mutableListOf()

    override fun getItemCount(): Int = dataList.size

    override fun createFragment(position: Int): Fragment {
        val fragment = MissionCertificationPostFragment()
        fragment.updateView(dataList[position].result.missionProofImages, position+1, dataList.size)
        return fragment
    }
    fun addData(data: MissionResponse) {
        dataList.add(data)
        notifyDataSetChanged() // 뷰페이저에 데이터가 변경되었음을 알립니다.
    }
}