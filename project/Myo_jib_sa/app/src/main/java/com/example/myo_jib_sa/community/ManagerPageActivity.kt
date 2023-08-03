package com.example.myo_jib_sa.community

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.adapter.WritePostImgAdapter
import com.example.myo_jib_sa.databinding.ActivityManagerPageBinding

class ManagerPageActivity : AppCompatActivity() {

    private lateinit var binding:ActivityManagerPageBinding
    private val REQUEST_CODE=1

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityManagerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //todo: 게시판 이미지 설정해주기

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
            intent.putExtra("boardId", boardId)
            startActivity(intent)
        }

    }

    //이미지 설정
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            val imagePath: String? = getRealPathFromURI(selectedImageUri)

            // imagePath로 이미지를 설정
            // 이제 선택한 이미지를 "managerPageImg" ImageView에 설정
            Glide.with(this)
                .load(imagePath)
                .into(binding.managerPageImg)
        }
    }

    //fun getRealPathFromURI() 이미지 url을 실제 파일 경로로 변환
    private fun getRealPathFromURI(uri: Uri?): String? {
        if (uri == null) return null

        var realPath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = this.contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                realPath = it.getString(columnIndex)
            }
            it.close()
        }
        return realPath
    }


}