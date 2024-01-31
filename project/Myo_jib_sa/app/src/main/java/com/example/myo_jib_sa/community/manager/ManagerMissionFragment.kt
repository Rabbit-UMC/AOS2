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
import com.example.myo_jib_sa.community.api.manager.ManagerMissionJoinRequest
import com.example.myo_jib_sa.databinding.FragmentManagerMissionBinding


class ManagerMissionFragment : Fragment() {

    private lateinit var binding:FragmentManagerMissionBinding
    private var data: ManagerMissionJoinRequest? =null
    private var selectedImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentManagerMissionBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data?.let { updateView(it) }
    }

    //주어진 데이터에 따라 뷰를 업데이트
    fun updateView(dataa:ManagerMissionJoinRequest){
        Log.d("updateView 실행", "실행함")
        if(::binding.isInitialized){
            Log.d("updateView 실행", "정보 설정 실행함")

            //todo:이미지 설정하기
            binding.managerMissionTxt.text=dataa.missionTitle
            binding.managerMissionEndTxt.text=dataa.missionEndTime
            binding.managerMissionStartTxt.text=dataa.missionStartTime
            binding.managerMissionMemoTxt.text=dataa.missionContent

            }

    }

}