package com.example.myo_jib_sa.community.manager

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myo_jib_sa.community.api.manager.JoinManagerMissions
import com.example.myo_jib_sa.community.api.manager.ManagerMissionJoinRequest
import com.example.myo_jib_sa.databinding.FragmentManagerMissionBinding


class ManagerMissionFragment : Fragment() {

    private lateinit var binding:FragmentManagerMissionBinding
    private var data: JoinManagerMissions? =null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentManagerMissionBinding.inflate(inflater, container, false)

        binding.managerMissionTxt.text= data?.missionTitle
        binding.managerMissionEndTxt.text=data?.missionEndDay
        binding.managerMissionStartTxt.text=data?.missionStartDay
        binding.managerMissionMemoTxt.text=data?.memo

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data?.let { updateView(it) }
        binding.managerMissionTxt.text= data?.missionTitle
        binding.managerMissionEndTxt.text=data?.missionEndDay
        binding.managerMissionStartTxt.text=data?.missionStartDay
        binding.managerMissionMemoTxt.text=data?.memo
    }

    //주어진 데이터에 따라 뷰를 업데이트
    fun updateView(dataa: JoinManagerMissions){
        Log.d("updateView 실행", "실행함")
        data=dataa

    }

}