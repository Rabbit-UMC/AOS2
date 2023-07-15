package com.example.myo_jib_sa.community

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.post.PostCreateRequest
import com.example.myo_jib_sa.community.Retrofit.post.PostRetrofitManager
import com.example.myo_jib_sa.community.adapter.PostImgAdapter
import com.example.myo_jib_sa.community.adapter.WritePostImgAdapter
import com.example.myo_jib_sa.databinding.ActivityWritePostingBinding

class WritePostingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWritePostingBinding


    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }

    private val writePostImgRecycr: RecyclerView by lazy {
        // 리사이클러뷰 초기화 코드
        findViewById(R.id.writrPost_img_recycr)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWritePostingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //게시물 수정일때
        if(intent.hasExtra("postId")){
            binding.postWritePostTextEtxt.setText(intent.getStringExtra("postText"))
            binding.writePostTitleEtxt.setText(intent.getStringExtra("title"))
            //사진 설정
        }


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

        //게시글 쓰기, 수정
        binding.postWriteCompleteBtn.setOnClickListener {
            val title = binding.writePostTitleEtxt.text
            val content = binding.postWritePostTextEtxt.text
            //이미지 리스트 나중에 받아와
            val img:List<String> =  listOf("sss", "ssss")
            val request=PostCreateRequest(title.toString(), content.toString(), img)
            //posting(author, request, boardId)
            //포스팅인지 수정인지 나눠 (캡슐화 ㄲ)

        }

        //어댑터 연결
        val adapter = WritePostImgAdapter(this)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.writrPostImgRecycr.layoutManager = layoutManager
        binding.writrPostImgRecycr.adapter = adapter

        adapter.setItemSpacing(binding.writrPostImgRecycr, 15)

        //뒤로가기 버튼
        binding.postWriteBackBtn.setOnClickListener {
            finish()
        }

        binding.postWriteCompleteBtn.setOnClickListener {
            //posting()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            val imagePath: String? = getRealPathFromURI(selectedImageUri)

            // imagePath를 WritePostImgAdapter로 전달하는 작업 수행
            val adapter = writePostImgRecycr.adapter as WritePostImgAdapter
            adapter.setImagePath(imagePath)
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