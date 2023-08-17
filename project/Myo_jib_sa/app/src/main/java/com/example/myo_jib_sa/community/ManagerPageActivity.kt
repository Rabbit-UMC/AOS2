package com.example.myo_jib_sa.community

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.adapter.WritePostImgAdapter
import com.example.myo_jib_sa.databinding.ActivityManagerPageBinding

class ManagerPageActivity : AppCompatActivity() {

    private lateinit var binding:ActivityManagerPageBinding
    private val REQUEST_CODE=1
    private var missionImg:String=""

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityManagerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //관리자 페이지 이름 설정
        val boardId= intent.getIntExtra("boardId",0)

        //이미지 설정
        missionImg=intent.getStringExtra("missionImg").toString()
        if(!missionImg.isNullOrBlank()){
            binding.constraintLayout.backgroundTintList=
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
            Glide.with(this)
                .load(missionImg)
                .into(binding.managerPageImg)
        }

        //게시판 이름
        when(boardId){
            Constance.ART_ID-> {
                binding.managerPageNameTxt.text="예술 묘집사"
            }
            Constance.FREE_ID-> {
                binding.managerPageNameTxt.text="자유 묘집사"
            }
            Constance.EXERCISE_ID-> {
                binding.managerPageNameTxt.text="운동 묘집사"
            }

        }

        //이미지 수정 페이지 이동
        binding.managerPageImgEditBtn.setOnClickListener{
            val intent= Intent(this, ManagerPageEditActivity::class.java)
            intent.putExtra("boardId", boardId.toLong())
            intent.putExtra("missionImg", missionImg)
            startActivityForResult(intent, REQUEST_CODE)
        }

        //묘방생 페이지로 이동
        binding.managerPageByeBtn.setOnClickListener {
            val intent=Intent(this, ManagerMissionCreateActivity::class.java)
            Log.d("묘방생 페이지로 이동", "묘방생 페이지로 이동")
            intent.putExtra("boardId", boardId)
            intent.putExtra("isBye", true)
            startActivity(intent)
        }

        //미션 생성 페이지로 이동
        //todo: 진행중인 미션이 없다면 미션 생성 페이지로 이동
        binding.managerPageMissionBtn.setOnClickListener {
            Log.d("미션 생성 페이지로 이동", "미션 생성 페이지로 이동")
            val intent=Intent(this, ManagerMissionCreateActivity::class.java)
            intent.putExtra("boardId", boardId)
            startActivity(intent)
        }

        //뒤로가기
        binding.managerPageBackBtn.setOnClickListener {
            finish()
        }

    }

    //이미지 설정
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imgPath = data?.getStringExtra("imgPath")
            val imageUri = Uri.parse(imgPath)

            binding.managerPageImg.setImageURI(imageUri)

        }
    }




}