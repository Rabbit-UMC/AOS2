package com.example.myo_jib_sa.community

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.post.ImageList
import com.example.myo_jib_sa.community.Retrofit.post.ImageListC
import com.example.myo_jib_sa.community.Retrofit.post.PostCreateRequest
import com.example.myo_jib_sa.community.Retrofit.post.PostEditRequest
import com.example.myo_jib_sa.community.Retrofit.post.PostRetrofitManager
import com.example.myo_jib_sa.community.adapter.WritePostImgAdapter
import com.example.myo_jib_sa.databinding.ActivityWritePostingBinding

class EditPostingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWritePostingBinding
    private var imgLists: List<ImageList> = listOf(ImageList(0,""), ImageList(0,""))


    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWritePostingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //게시물 수정일때

        binding.postWritePostTextEtxt.setText(intent.getStringExtra("postText"))
        binding.writePostTitleEtxt.setText(intent.getStringExtra("title"))
        //사진 설정 코드 넣어줘야 함
        val postId:Long=intent.getLongExtra("postId",0)
        val list1=ImageList(intent.getLongExtra("imgList1_id",0), intent.getStringExtra("imgList1_path").toString())
        val list2=ImageList(intent.getLongExtra("imgList2_id",0), intent.getStringExtra("imgList2_path").toString())
        val imgList: List<ImageListC> = listOf(ImageListC(list1.filePath), ImageListC(list2.filePath))
        val list= listOf(list1,list2)
        imgLists=list


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

        //게시글 수정
        binding.postWriteCompleteBtn.setOnClickListener {
            val title = binding.writePostTitleEtxt.text
            val content = binding.postWritePostTextEtxt.text
            //이미지 리스트도 받아와야 함

            val request= PostEditRequest(title.toString(), content.toString(), imgLists)
            //api로 콜 보냄
            editing(Constance.jwt, request, postId)
            finish() //엑티비티 종료

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



    private fun editing(author:String, request: PostEditRequest, postId:Long){
        val retrofitManager = PostRetrofitManager.getInstance(this)

        //게시물 생성 api 연결
        retrofitManager.postEdit(author,request, postId, postId){response ->
            if(response){
                //로그
                Log.d("게시물 수정", "${response.toString()}")


            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                Log.d("게시물 수정 isSuccess가 false", "${response.toString()}")
                //토스트 메시지 띄우기
            }
        }
    }


}