package com.example.myo_jib_sa.community.post

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.ImgPath
import com.example.myo_jib_sa.community.adapter.PostEditAdapter
import com.example.myo_jib_sa.community.api.imgUpload.ImageUploadResponse
import com.example.myo_jib_sa.community.api.imgUpload.imgUploadRetrofitManager
import com.example.myo_jib_sa.community.api.post.ArticleImage
import com.example.myo_jib_sa.community.api.post.ImageList
import com.example.myo_jib_sa.community.api.post.PostCreateRequest
import com.example.myo_jib_sa.community.api.post.PostEditRequest
import com.example.myo_jib_sa.community.api.post.PostRetrofitManager
import com.example.myo_jib_sa.databinding.ActivityWritePostingBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.io.File

class PostEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWritePostingBinding


    private var postId:Long=0 //수정할 때만 씀
    private var boardId:Long=0

    //이미지 url 저장
    private var imgUrlList:MutableList<String> = mutableListOf()
    private var editImgTemp:MutableList<Long> = mutableListOf()

    //이미지가 있는 지, 갤러리에서 첨부한 이미지만 api를 통해 업로드하기 위해 사용하는 변수
    private var isHasNewImg:Boolean=false

    //이미지 포지션 저장
    private var imgPosition=0

    //삭제된 이미지, 추가된 이미지 저장
    private var newImageIdList:MutableList<Long> = mutableListOf()
    private var deleteImageIdList:MutableList<Long> = mutableListOf()

    var adapter = PostEditAdapter(this, imgUrlList)


    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWritePostingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //수정인지
        setData()

        boardId=intent.getLongExtra("boardId", 0)
        Log.d("게시판 아이디", boardId.toString())

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



        //게시글 쓰기, 수정 완료
        complete()

        itemClike()

        //뒤로가기 버튼
        binding.postWriteBackBtn.setOnClickListener {
            finish()
        }

    }

    //리사이클러뷰 클릭 리스너
    private fun itemClike(){
        adapter.setOnItemClickListener(object : PostEditAdapter.OnItemClickListener {

            override fun onDeleteClick(position: Int) {
                if (position != 0) {
                    imgUrlList = imgUrlList.toMutableList().apply {
                        removeAt(position-1)
                    }
                    deleteImageIdList.add(editImgTemp[position-1]) //삭제한 이미지 아이디 추가
                    editImgTemp = editImgTemp.toMutableList().apply {//이미지 아이디 저장
                        removeAt(position-1)
                    }
                    Log.d("이미지 확인", editImgTemp.toString())
                    Log.d("삭제 이미지 확인", deleteImageIdList.toString())
                    Log.d("새로운 이미지 확인", newImageIdList.toString())


                    adapter = PostEditAdapter(this@PostEditActivity, imgUrlList)
                    val layoutManager = LinearLayoutManager(this@PostEditActivity, LinearLayoutManager.HORIZONTAL, false)
                    binding.postWriteImgRecy.layoutManager = layoutManager
                    adapter = PostEditAdapter(this@PostEditActivity, imgUrlList)
                    binding.postWriteImgRecy.adapter=adapter
                    adapter.setItemSpacing(binding.postWriteImgRecy, 15)
                    itemClike()
                }
            }

            override fun onImageClick(position: Int) {
                imgPosition=position //이미지 터치시 갤러리 가서 이미지 추가
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //이미지
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
            }
        })
    }

    //todo : 사진 설정을 위한 onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == GALLERY_REQUEST_CODE)
            && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            // 선택한 이미지를 해당 이미지뷰에 표시
            selectedImageUri?.let { uri ->

                val imgPath=getRealPathFromURI(uri)

                imgUpload(listOf(imgPath) as List<String>){response ->
                    if(imgPosition==0){
                        imgUrlList.add(response.result[0].imageUrl) //이미지 추가
                        editImgTemp.add(response.result[0].imageId) //이미지 아이디 추가
                        newImageIdList.add(response.result[0].imageId) //새로운 이미지 추가
                        Log.d("이미지 확인", editImgTemp.toString())
                        Log.d("삭제 이미지 확인", deleteImageIdList.toString())
                        Log.d("새로운 이미지 확인", newImageIdList.toString())
                    }else{
                        imgUrlList[imgPosition-1]=response.result[0].imageUrl //이미지 변경
                        deleteImageIdList.add(editImgTemp[imgPosition-1]) //이미지 아이디 삭제
                        editImgTemp[imgPosition-1]=response.result[0].imageId //이미지 추가
                        newImageIdList.add(response.result[0].imageId) //새로운 이미지 추가
                        Log.d("이미지 확인", editImgTemp.toString())
                        Log.d("삭제 이미지 확인", deleteImageIdList.toString())
                        Log.d("새로운 이미지 확인", newImageIdList.toString())
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    //수정일시 데이터 설정
    private fun setData(){
        binding.postWritePostTextEtxt.setText(intent.getStringExtra("postText"))
        binding.writePostTitleEtxt.setText(intent.getStringExtra("title"))

        val jsonImageList = intent.getStringExtra("images")
        val gson = Gson()
        val type = object : TypeToken<List<ArticleImage>>() {}.type
        val imageList = gson.fromJson<List<ArticleImage>>(jsonImageList, type)
        Log.d("이미지 gson.fromJson<List<ArticleImage>>", imageList.toString())
        imgUrlList= imageList.map{ articleImage ->
            articleImage.filePath
        } as MutableList<String>
        editImgTemp=imageList.map{articleImage ->
            articleImage.imageId
        } as MutableList<Long>
        Log.d("이미지 URL 리스트", imgUrlList.toString())

        //이후 사용될 데이터 저장
        postId=intent.getLongExtra("postId",0)

        //이미지 수정
        Log.d("imgUrlList Size", imgUrlList.size.toString())
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.postWriteImgRecy.layoutManager = layoutManager
        adapter = PostEditAdapter(this, imgUrlList)
        binding.postWriteImgRecy.adapter=adapter
        adapter.setItemSpacing(binding.postWriteImgRecy, 15)
    }

    //글쓰기, 수정 완료
    private fun complete() {
        binding.postWriteCompleteBtn.setOnClickListener {
            val title = binding.writePostTitleEtxt.text.toString()
            val postText = binding.postWritePostTextEtxt.text.toString().replace("\n", "\\n")

            val request = PostEditRequest(title, postText, newImageIdList, deleteImageIdList)


                editing(request, postId) { isSuccess ->
                    if (isSuccess) {
                        finish()
                    } else {
                        showToast("게시글 수정 실패")
                    }
                }

        }
    }



    //수정 api 연결
    private fun editing(request: PostEditRequest, postId:Long
                        , callback: (Boolean) -> Unit){
        val retrofitManager = PostRetrofitManager.getInstance(this)

        //게시물 생성 api 연결
        retrofitManager.postEdit(request, postId){response ->
            if(response){
                //로그
                Log.d("게시물 수정", "${response.toString()}")
                callback(true)

            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                Log.d("게시물 수정 isSuccess가 false", "${response.toString()}")
                //토스트 메시지 띄우기
                showToast("게시글 수정 실패")
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

    //이미지 업로드 api
    private fun imgUpload(imgPath:List<String>, callback: (ImageUploadResponse) -> Unit){
        val imgList:MutableList<File> = mutableListOf()
        for(i in 1..imgPath.size){
            //있는 사진 부터 순차적으로
            if(!imgPath[i-1].isNullOrBlank()){
                val imageFile = File(imgPath[i-1]) // 이미지 파일 경로
                imgList.add(imageFile)
            }
        }

        val imgUploadRetrofitManager = imgUploadRetrofitManager(this)
        imgUploadRetrofitManager.uploadImage(imgList, ImgPath.POST) { response ->
            if (response != null) {
                val imageUrl = response.result[0]
                val isSuccess = response.isSuccess
                val message = response.errorMessage
                Log.d("이미지 업로드 결과", "$message")
                Log.d("이미지 업로드 결과", "$imageUrl")
                if(isSuccess){
                    Log.d("이미지 업로드 결과", "isSuccess")
                    if(response.result.isNullOrEmpty()){
                        callback(response)
                    }else{
                        //이미지 url 저장
                        callback(response)
                    }

                }else{
                    Log.d("이미지 업로드 결과", "isSuccess이 false")
                    showToast("이미지 업로드 실패")
                    callback(response)
                }

            } else {
                Log.d("이미지 업로드 결과", "실패")
                showToast("이미지 업로드 실패")

                if (response != null) {
                    callback(response)
                }
            }
        }
    }

private fun showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


}