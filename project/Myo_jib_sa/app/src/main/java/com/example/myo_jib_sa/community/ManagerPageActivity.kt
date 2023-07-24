package com.example.myo_jib_sa.community

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.databinding.ActivityManagerPageBinding

class ManagerPageActivity : AppCompatActivity() {

    private lateinit var binding:ActivityManagerPageBinding
    private val REQUEST_CODE=1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityManagerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //todo: 게시판 이미지 설정해주기
        //todo: 라이프 사이클 이용해서 수정된 이미지로 뷰 업데이트하기

        //관리자 페이지 이름 설정
        val boardId= intent.getIntExtra("boardId",0)
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
            startActivityForResult(intent, REQUEST_CODE)
        }

        //묘방생 페이지로 이동
        binding.managerPageByeBtn.setOnClickListener {
            val intent=Intent(this, ManagerPageByeActivity::class.java)
            startActivity(intent)
        }

        //미션 생성 페이지로 이동
        //todo: 진행중인 미션이 없다면 미션 생성 페이지로 이동
        binding.managerPageMissionBtn.setOnClickListener {
            val intent=Intent(this, ManagerPageMissionActivity::class.java)
            startActivity(intent)
        }

    }

    //이미지 설정
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imgPath = data?.getStringExtra("imgPath")
            if (imgPath != null) {
                binding.managerPageImg.setImageURI(imgPath.toUri())
            }
        }
    }


}