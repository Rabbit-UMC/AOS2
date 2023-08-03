package com.example.myo_jib_sa.community

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.post.ImageList
import com.example.myo_jib_sa.community.Retrofit.post.ImageListC
import com.example.myo_jib_sa.community.Retrofit.post.PostCreateRequest
import com.example.myo_jib_sa.community.Retrofit.post.PostRetrofitManager
import com.example.myo_jib_sa.community.adapter.PostImgAdapter
import com.example.myo_jib_sa.community.adapter.WritePostImgAdapter
import com.example.myo_jib_sa.databinding.ActivityWritePostingBinding

class WritePostingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWritePostingBinding
    private var imgList: List<ImageListC> = listOf(ImageListC(""), ImageListC(""))


    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWritePostingBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val boardId=intent.getIntExtra("boardId", 0)
        //게시판 이름
        when(boardId){
            Constance.ART_ID-> {
                binding.postWriteNameTxt.text="예술 게시판"
            }
            Constance.FREE_ID-> {
                binding.postWriteNameTxt.text="자유 게시판"
            }
            Constance.EXERCISE_ID-> {
                binding.postWriteNameTxt.text="운동 게시판"
            }

        }



        //게시글 쓰기
        binding.postWriteCompleteBtn.setOnClickListener {
            val request=PostCreateRequest(
                binding.postWriteNameTxt.text.toString(),
                binding.postWritePostTextEtxt.text.toString(),
                imgList //이미지 리스트 넣음
            )
            posting(Constance.jwt,request, boardId.toLong())

            finish()//엑티비티 종료

        }

        //이미지뷰 터치시 갤러리로 가서 사진 선택 후 해당 이미지 뷰에 뷰 설정
        binding.missionCertImg.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }

        binding.missionCertImg1.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }
        //뒤로가기 버튼
        binding.postWriteBackBtn.setOnClickListener {
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            // 선택한 이미지를 해당 이미지뷰에 표시
            selectedImageUri?.let { uri ->
                val imageView: ImageView = binding.missionCertImg
                imageView.setImageURI(uri)
                // todo:이미지를 서버로 전송하거나 파일 경로로 변환하여 api에 전달할 수 있습니다.
                // 선택한 이미지를 서버로 전송하는 부분은 retrofitManager.postImageUpload(author, uri) 등의 메소드를 활용하여 구현하면 됩니다.
            }
        }
    }



    private fun posting(author:String,request: PostCreateRequest, categoryId:Long){
        val retrofitManager = PostRetrofitManager.getInstance(this)

        //게시물 생성 api 연결
        retrofitManager.postCreate(author,request, categoryId){response ->
            if(response){
                //로그
                Log.d("게시물 생성", "${response.toString()}")


            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                Log.d("게시물 생성 isSuccess가 false", "${response.toString()}")
                //토스트 메시지 띄우기
            }
        }
    }



}