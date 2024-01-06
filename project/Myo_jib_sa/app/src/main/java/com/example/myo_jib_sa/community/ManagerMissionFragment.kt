package com.example.myo_jib_sa.community

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

    companion object {
        private const val GALLERY_REQUEST_CODE1 = 1001
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentManagerMissionBinding.inflate(inflater, container, false)

        //사진 추가하기 버튼 누르면
        binding.managerImgAddBtn.setOnClickListener {
            addImg()
        }

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

    //이미지 첨부
    fun addImg(){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)

    }

    //갤러리
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = result.data?.data
            val intent=Intent(requireContext(), ManagerImgActivity::class.java)
            intent.putExtra("ingPath",selectedImageUri.toString())
            startActivity(intent)
        }
    }

}