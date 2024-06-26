package com.example.myo_jib_sa.community.manager

import android.R
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.adapter.ManagerPageViewpagerAdapter
import com.example.myo_jib_sa.community.api.manager.ManagerRetrofitManager
import com.example.myo_jib_sa.databinding.ActivityManagerPageBinding


class ManagerPageActivity : AppCompatActivity() {

    private lateinit var binding:ActivityManagerPageBinding

    private var missionId:Long=0
    private var boardId:Long=0

    private val REQUEST_CODE = 1


    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityManagerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //관리자 페이지 이름 설정
        boardId= intent.getLongExtra("boardId",0L)
        missionId=intent.getLongExtra("missionId", 0)

        Log.d("관리자 페이지 미션 아이디", missionId.toString())

        //관리자 화면 조회
        join(boardId)


        //게시판 이름
        when(boardId){
            Constance.ART_ID -> {
                binding.managerPageNameTxt.text="예술 묘집사"
            }
            Constance.FREE_ID -> {
                binding.managerPageNameTxt.text="자유 묘집사"
            }
            Constance.EXERCISE_ID -> {
                binding.managerPageNameTxt.text="운동 묘집사"
            }

        }

        //이미지 수정
        binding.managerImgConst.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }

        //미션 생성 페이지 이동
        binding.managerCreateMission.setOnClickListener {
            val intent=Intent(this, ManagerMissionCreateActivity2::class.java)
            intent.putExtra("boardId", boardId)
            startActivity(intent)
        }

        //뒤로가기
        binding.managerPageBackBtn.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        binding.managerMissionImg.setImageResource(0)
        join(missionId)
    }


    //이미지 설정
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            // 선택한 이미지를 이미지 엑티비티에 전달
            selectedImageUri?.let { uri ->
                val intent=Intent(this, ManagerImgActivity::class.java)
                intent.putExtra("imgUri", uri.toString())
                intent.putExtra("boardId", boardId)
                startActivity(intent)
            }
        }

        if (requestCode === REQUEST_CODE) {
            if (resultCode === Activity.RESULT_OK) {
                // 관리자 이미지 바뀐 거 업데이트
                binding.managerMissionImg.setImageResource(0)
                join(missionId)
            }
        }
    }

    //관리자 화면 조회
    private fun join(boardId:Long){
        val retrofitManager = ManagerRetrofitManager.getInstance(this)

            retrofitManager.joinMission(boardId){ response ->
                if(response.isSuccess){
                    binding.managerNameTxt.text=response.result[0].nowHostUserName

                    Glide.with(this)
                        .load(response.result[0].missionImageUrl)
                        .into(binding.managerMissionImg)


                    //뷰페이저 프레그먼트 연결
                    val mAdapter=ManagerPageViewpagerAdapter(this, response.result)
                    binding.managerMissionVp2.adapter=mAdapter
                    val lastItemIndex = mAdapter.itemCount - 1
                    binding.managerMissionVp2.setCurrentItem(lastItemIndex, false) //제일 최근 미션

                    Log.d("관리자 페이지 불러오기", "성공")
                } else {
                    Log.d("관리자 페이지 불러오기", "실패")
                }
            }

    }
}

