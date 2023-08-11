package com.example.myo_jib_sa.community.adapter

import android.content.Context
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myo_jib_sa.community.Retrofit.missionCert.MissionResponse
import com.example.myo_jib_sa.community.missionCert.MissionCertificationPostFragment
import com.example.myo_jib_sa.databinding.ActivityMissionCertificationBinding

class MissionCertViewpagerAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val dataList:MutableList<MissionResponse> = mutableListOf()
    private lateinit var bindingActivity:ActivityMissionCertificationBinding

    override fun getItemCount(): Int = dataList.size

    override fun createFragment(position: Int): Fragment {


        val fragment = MissionCertificationPostFragment()
        val missionResponse = dataList[position]
        val day = position + 1
        val lastDay = dataList.size

        Log.d("day 확인", day.toString())
        Log.d("lastDay 확인", lastDay.toString())

        //텍스트 설정
        /*
        bindingActivity.missionCertDay.text=day.toString()
        bindingActivity.missionCertLeftDay.text=(day-1).toString()
        bindingActivity.missionCertRightDay.text=(day+1).toString()
        if(day==1){
            bindingActivity.missionCertLeftBtn.visibility= View.GONE
            bindingActivity.missionCertLeftDay.text=""
            Log.d("버튼 상태", "오른쪽 버튼 안보임")
        }else{
            bindingActivity.missionCertLeftBtn.visibility= View.VISIBLE
        }
        if(day==lastDay){
            bindingActivity.missionCertRightDay.visibility= View.GONE
            bindingActivity.missionCertRightDay.text=""
            Log.d("버튼 상태", "왼쪽 버튼 안보임")
        }else{
            bindingActivity.missionCertRightDay.visibility= View.VISIBLE
        }
        */


        fragment.updateView(missionResponse.result.missionProofImages)
        return fragment
    }

    fun setBinding(binding: ActivityMissionCertificationBinding) {
        bindingActivity=binding
    }

    fun addData(data: MissionResponse) {
        dataList.add(data)
        notifyDataSetChanged() // 뷰페이저에 데이터가 변경되었음을 알립니다.
    }
}