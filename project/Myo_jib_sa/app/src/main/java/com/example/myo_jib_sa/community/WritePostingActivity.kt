package com.example.myo_jib_sa.community

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.post.ImageList
import com.example.myo_jib_sa.community.Retrofit.post.ImageListC
import com.example.myo_jib_sa.community.Retrofit.post.PostCreateRequest
import com.example.myo_jib_sa.community.Retrofit.post.PostEditRequest
import com.example.myo_jib_sa.community.Retrofit.post.PostRetrofitManager
import com.example.myo_jib_sa.community.adapter.PostImgAdapter
import com.example.myo_jib_sa.community.adapter.WritePostImgAdapter
import com.example.myo_jib_sa.databinding.ActivityWritePostingBinding
import java.io.ByteArrayOutputStream

class WritePostingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWritePostingBinding
    private var imgList: List<String> = listOf("","")
    private var imgListEdit:List<ImageList> = listOf(ImageList(0,""), ImageList(0, ""))
    private var isEdit:Boolean=false
    private var postId:Long=0 //수정할 때만 씀


    companion object {
        private const val GALLERY_REQUEST_CODE1 = 1001
        private const val GALLERY_REQUEST_CODE2 = 1002
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWritePostingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //수정인지, 그냥 글쓰기인지 구별
        isEdit=intent.getBooleanExtra("isEdit", false)
        if(isEdit){
            binding.postWritePostTextEtxt.setText(intent.getStringExtra("postText"))
            binding.writePostTitleEtxt.setText(intent.getStringExtra("title"))
            //이미지 설정
            setImgGlide(binding.missionCertImg, intent.getStringExtra("imgList1_path").toString())
            setImgGlide(binding.missionCertImg1, intent.getStringExtra("imgList2_path").toString())

            //이후 사용될 데이터 저장
            postId=intent.getLongExtra("postId",0)
            val list1=ImageList(intent.getLongExtra("imgList1_id",0), intent.getStringExtra("imgList1_path").toString())
            val list2=ImageList(intent.getLongExtra("imgList2_id",0), intent.getStringExtra("imgList2_path").toString())
            imgListEdit = listOf(list1, list2)
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



        //게시글 쓰기, 수정 완료
        binding.postWriteCompleteBtn.setOnClickListener {
            if(!isEdit){
                val request=PostCreateRequest(
                    binding.postWriteNameTxt.text.toString(),
                    binding.postWritePostTextEtxt.text.toString(),
                    imgList //이미지 리스트 넣음
                )
                posting(Constance.jwt,request, boardId.toLong()){isSuccess->
                    if(isSuccess){
                        finish()
                    }else{
                        showToast("게시글 쓰기 실패")
                    }
                }
            }else{
                imgListEdit[0].filePath=imgList[0]
                imgListEdit[1].filePath=imgList[1]

                val request= PostEditRequest(
                    binding.postWritePostTextEtxt.text.toString()
                    , binding.postWritePostTextEtxt.text.toString()
                    , imgListEdit)
                //api로 콜 보냄
                editing(Constance.jwt, request, postId){isSuccess->
                    if(isSuccess){
                        finish()
                    }else{
                        showToast("게시글 수정 실패")
                    }
                }
            }

        }

        //이미지뷰 터치시 갤러리로 가서 사진 선택 후 해당 이미지 뷰에 뷰 설정
        binding.missionCertImg.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE1)
        }

        binding.missionCertImg1.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE2)
        }
        //뒤로가기 버튼
        binding.postWriteBackBtn.setOnClickListener {
            finish()
        }

    }

    //사진 설정을 위한 onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == GALLERY_REQUEST_CODE1||requestCode == GALLERY_REQUEST_CODE2)
            && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            // 선택한 이미지를 해당 이미지뷰에 표시
            selectedImageUri?.let { uri ->
                val imageView: ImageView = binding.missionCertImg
                imageView.setImageURI(uri)
                val imgPath = getRealPathFromURI(uri)
                val base63Img = encodeImageToBase64(imgPath.toString())
                if(requestCode == GALLERY_REQUEST_CODE1){
                    imgList = imgList.toMutableList().apply {
                        set(0, base63Img.toString())
                    }
                }else{
                    imgList = imgList.toMutableList().apply {
                        set(1, base63Img.toString())
                    }
                }
            }
        }
    }


    //게시글쓰기 api 연결
    private fun posting(author:String,request: PostCreateRequest, categoryId:Long
                        ,callback: (Boolean) -> Unit){
        val retrofitManager = PostRetrofitManager.getInstance(this)

        //게시물 생성 api 연결
        retrofitManager.postCreate(author,request, categoryId){response ->
            if(response){
                //로그
                Log.d("게시물 생성", "${response.toString()}")
                callback(true)

            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                Log.d("게시물 생성 isSuccess가 false", "${response.toString()}")
                //토스트 메시지 띄우기
                callback(false)
            }
        }
    }

    //수정 api 연결
    private fun editing(author:String, request: PostEditRequest, postId:Long
                        ,callback: (Boolean) -> Unit){
        val retrofitManager = PostRetrofitManager.getInstance(this)

        //게시물 생성 api 연결
        retrofitManager.postEdit(author,request, postId, postId){response ->
            if(response){
                //로그
                Log.d("게시물 수정", "${response.toString()}")
                callback(true)

            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                Log.d("게시물 수정 isSuccess가 false", "${response.toString()}")
                //토스트 메시지 띄우기
                callback(false)
            }
        }
    }

    //이미지 설정
    private fun setImgGlide(imgView: ImageView, imgUrl: String){
        Glide.with(this)
            .load(imgUrl)
            .into(imgView)
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

    //Base64로 인코딩하기
    fun encodeImageToBase64(imagePath: String): String? {
        val bitmap = BitmapFactory.decodeFile(imagePath)
        if (bitmap != null) {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val byteArray = baos.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
        return null
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}