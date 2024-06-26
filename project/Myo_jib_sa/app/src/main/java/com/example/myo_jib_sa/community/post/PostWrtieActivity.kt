package com.example.myo_jib_sa.community.post

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.ImgPath
import com.example.myo_jib_sa.community.adapter.PostEditAdapter
import com.example.myo_jib_sa.community.adapter.PostWriteAdapter
import com.example.myo_jib_sa.community.api.imgUpload.imgUploadRetrofitManager
import com.example.myo_jib_sa.community.api.post.ImageList
import com.example.myo_jib_sa.community.api.post.PostCreateRequest
import com.example.myo_jib_sa.community.api.post.PostEditRequest
import com.example.myo_jib_sa.community.api.post.PostRetrofitManager
import com.example.myo_jib_sa.databinding.ActivityWritePostingBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.stream.Collectors

class PostWrtieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWritePostingBinding

    private var boardId:Long=0

    //이미지 uri 저장
    private var imgUriList:MutableList<Uri> = mutableListOf()

    //이미지가 있는 지, 갤러리에서 첨부한 이미지만 api를 통해 업로드하기 위해 사용하는 변수
    private var isHasNewImg:Boolean=false

    //이미지 포지션 저장
    private var imgPosition=0

    private lateinit var adapter:PostWriteAdapter


    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWritePostingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardId=intent.getLongExtra("boardId", 0L)
        Log.d("게시판 아이디", boardId.toString())

        binding.postWritePostTextEtxt.hint=resources.getString(R.string.post_guid)

        //이미지 어댑터
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.postWriteImgRecy.layoutManager = layoutManager
        adapter = PostWriteAdapter(this, imgUriList)
        binding.postWriteImgRecy.adapter=adapter
        adapter.setItemSpacing(binding.postWriteImgRecy, 15)


        //게시판 이름
        when(boardId){
            Constance.ART_ID -> {
                binding.postWriteNameTxt.text="예술 게시판"
            }
            Constance.FREE_ID -> {
                binding.postWriteNameTxt.text="자유 게시판"
            }
            Constance.EXERCISE_ID -> {
                binding.postWriteNameTxt.text="운동 게시판"
            }

        }

        itemClike()

        //게시글 쓰기, 수정 완료
        complete()

        //뒤로가기 버튼
        binding.postWriteBackBtn.setOnClickListener {
            finish()
        }

    }

    private fun itemClike(){
        Log.d("아이템 클릭 함수", "position.toString()")


        adapter.setOnItemClickListener(object : PostWriteAdapter.OnItemClickListener {
            override fun onDeleteClick(position: Int) {
                if (position != 0) {
                    imgUriList = imgUriList.toMutableList().apply {
                        removeAt(position - 1)
                    }
                    Log.d("이미지 삭제 후 리스트", imgUriList.toString())
                    Log.d("이미지 삭제 후 리스트 사이즈", imgUriList.size.toString())
                    val layoutManager = LinearLayoutManager(this@PostWrtieActivity, LinearLayoutManager.HORIZONTAL, false)
                    binding.postWriteImgRecy.layoutManager = layoutManager
                    adapter = PostWriteAdapter(this@PostWrtieActivity, imgUriList)
                    binding.postWriteImgRecy.adapter=adapter
                    adapter.setItemSpacing(binding.postWriteImgRecy, 15)
                    itemClike()
                }
            }

            override fun onImageClick(position: Int) {
                imgPosition = position
                Log.d("아이템 클릭됨", position.toString())
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == GALLERY_REQUEST_CODE)
            && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            // 선택한 이미지를 해당 이미지뷰에 표시
            selectedImageUri?.let { uri ->
                if (imgPosition == 0) {
                    imgUriList.add(uri)
                } else {
                    imgUriList[imgPosition - 1] = uri
                }
                Log.d("이미지 URi : ", imgUriList.toString())
                adapter.notifyDataSetChanged()
            }
        }

    }

    //todo : 사진 설정을 위한 onActivityResult

    //글쓰기 완료
    @RequiresApi(Build.VERSION_CODES.N)
    private fun complete(){
        binding.postWriteCompleteBtn.setOnClickListener {
            imgUriList.stream()

            var request=PostCreateRequest(
                binding.writePostTitleEtxt.text.toString(),
                binding.postWritePostTextEtxt.text.toString().replace("\n", "<br>")
            )

            posting(convertUriListToMultipart(imgUriList), request, boardId){ isSuccess->
                    if(isSuccess){
                        finish()
                    }else{
                        showToast("게시글 쓰기 실패")
                    }
                }
        }
    }

    //게시글쓰기 api 연결
    private fun posting(images:List<MultipartBody.Part> , request: PostCreateRequest, categoryId:Long
                        ,callback: (Boolean) -> Unit){
        val retrofitManager = PostRetrofitManager.getInstance(this)

        //게시물 생성 api 연결
        retrofitManager.postCreate(images, request, categoryId){response ->
            if(response){
                //로그
                Log.d("게시물 생성", "${response.toString()}")
                callback(true)

            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                Log.d("게시물 생성 isSuccess가 false", "${response.toString()}")
                //토스트 메시지 띄우기
                showToast("게시글 업로드 실패")
                callback(false)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun convertUriListToMultipart(imgUriList: List<Uri>): List<MultipartBody.Part> {
        val fileParts:List<MultipartBody.Part> = imgUriList.stream()
            .map { uri ->
                val realPath = getRealPathFromURI(uri)
                val file = File(realPath)
                val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
                MultipartBody.Part.createFormData("multipartFiles", file.name, requestBody)
            }
            .collect(Collectors.toList())

        Log.d("이미지 멀티파트 변환 확인", fileParts.toString())
        return fileParts
    }

    //fun getRealPathFromURI() 이미지 uri을 실제 파일 경로로 변환
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
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}