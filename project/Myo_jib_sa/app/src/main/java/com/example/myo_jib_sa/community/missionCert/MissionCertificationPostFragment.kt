package com.example.myo_jib_sa.community.missionCert

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.community.Retrofit.communityHome.MainMission
import com.example.myo_jib_sa.community.Retrofit.missionCert.MCrecyclrImg
import com.example.myo_jib_sa.community.Retrofit.missionCert.MissionProofImages
import com.example.myo_jib_sa.community.Retrofit.missionCert.MissionResponse
import com.example.myo_jib_sa.community.adapter.HomeMissionAdapter
import com.example.myo_jib_sa.community.adapter.MissionCertAdapter
import com.example.myo_jib_sa.databinding.FragmentCommunityBinding
import com.example.myo_jib_sa.databinding.FragmentMissionCertificationPostBinding

class MissionCertificationPostFragment : Fragment() {

    private lateinit var binding:FragmentMissionCertificationPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMissionCertificationPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    //주어진 데이터에 따라 뷰를 업데이트
    fun updateView(data:List<MissionProofImages>, day:Int, lastDay:Int){
        //텍스트 설정
        binding.missionCertDay.text=day.toString()
        binding.missionCertLeftDay.text=(day-1).toString()
        binding.missionCertRightDay.text=(day+1).toString()
        if(day==1){
            binding.missionCertLeftBtn.visibility=View.GONE
            binding.missionCertLeftDay.text=""
        }
        if(day==lastDay){
            binding.missionCertRightDay.visibility=View.GONE
            binding.missionCertRightDay.text=""
        }


        //리사이클러 뷰 설정
        var rlist: MutableList<MCrecyclrImg> = mutableListOf()
        var tempList:MutableList<MissionProofImages> = mutableListOf()
        for(i in 1..data.size){ //데이터 3개씩 나눠 담기
            tempList.add(data[i])
            if ((i + 1) % 3 == 0 || i == data.size - 1) {
                val item = MCrecyclrImg(tempList[0], tempList[1], tempList[2])
                rlist.add(item)
                tempList.clear()
            }
        }
        val Madapter = MissionCertAdapter(requireContext(),rlist)
        val MlayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.missionCertRecyclr.layoutManager = MlayoutManager
        Log.d("리사이클러뷰","binding.missionCertRecyclr.layoutManager 시작")
        binding.missionCertRecyclr.adapter = Madapter
        Log.d("리사이클러뷰","binding.missionCertRecyclr.adapter 시작")

        //Madapter.setItemSpacing(binding.missionCertRecyclr, 15)

    }
}