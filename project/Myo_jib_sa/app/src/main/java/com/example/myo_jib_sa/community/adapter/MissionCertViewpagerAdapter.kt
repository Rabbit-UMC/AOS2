package com.example.myo_jib_sa.community.adapter

import android.content.Context
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myo_jib_sa.community.Retrofit.missionCert.MissionCertRetrofitManager
import com.example.myo_jib_sa.community.Retrofit.missionCert.MissionResponse
import com.example.myo_jib_sa.community.missionCert.MissionCertViewModel
import com.example.myo_jib_sa.community.missionCert.MissionCertificationPostFragment
import com.example.myo_jib_sa.databinding.ActivityMissionCertificationBinding
import kotlin.math.log

class MissionCertViewpagerAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private var jwt:String=""
    private var id:Long=0
    private lateinit var context:Context
    private var size=0

    override fun getItemCount(): Int = size

    override fun createFragment(position: Int): Fragment {


        val fragment = MissionCertificationPostFragment()
        val day = position + 1
        val lastDay = size


        Log.d("day 확인", day.toString())
        Log.d("lastDay 확인", lastDay.toString())

        val retrofitManager = MissionCertRetrofitManager.getInstance(context)
        retrofitManager.mission(jwt, position+1, id){response ->
            Log.d("createFragment 미션 인증 날짜 확인", day.toString())
            if(response.isSuccess=="true"){

                if(response.result != null){
                    if(response.result.missionProofImages.isNotEmpty()){
                        Log.d("뷰페이저 어댑터에서 이미지 확인", response.result.missionProofImages[0].filePath)
                    }
                    fragment.updateView(response.result.missionProofImages)

                }else{
                    Log.d("뷰페이져 어댑터로 리스트 전달", "List가 비었다네요")
                }
            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                val returnCode = response.code
                val returnMsg = response.message

                Log.d("미션 인증 API isSuccess가 false", "${returnCode}  ${returnMsg}")
            }
        }

        return fragment
    }

    fun setData(author:String, maxDay:Int, mainMissionId:Long, conText:Context){
        jwt=author
        size=maxDay
        id=mainMissionId
        context=conText
        notifyDataSetChanged() // 뷰페이저에 데이터가 변경되었음을 알립니다.
    }


}