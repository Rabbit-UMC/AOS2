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
    private var data:MutableList<MissionProofImages> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMissionCertificationPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateView(data)
    }

    //주어진 데이터에 따라 뷰를 업데이트
    fun updateView(dataa:List<MissionProofImages>){
        Log.d("updateView 실행", "실행함")

        data= dataa.toMutableList()

        if(::binding.isInitialized){

            if(data.isNotEmpty()){
                //리사이클러 뷰 설정
                var rlist: MutableList<MCrecyclrImg> = mutableListOf()
                var tempList:MutableList<MissionProofImages> = mutableListOf()

                val empty=MissionProofImages(-1, -1, "empty")

                for (i in 1..data.size) { // 데이터 3개씩 나눠 담기
                    tempList.add(data[i - 1])
                    Log.d("updateView 데이터", i.toString() + "번째 데이터 : " + data[i - 1].filePath)

                    if (i % 3 == 0 || i == data.size) { // 3개씩 추가하거나 마지막 데이터인 경우
                        val item = when ((i % 3)) {
                            0 -> MCrecyclrImg(tempList[0], tempList[1], tempList[2])
                            2 -> MCrecyclrImg(tempList[0], tempList[1], empty)
                            1 -> MCrecyclrImg(tempList[0], empty, empty)
                            else -> MCrecyclrImg(empty, empty, empty) // 예외 처리
                        }
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
    }



}