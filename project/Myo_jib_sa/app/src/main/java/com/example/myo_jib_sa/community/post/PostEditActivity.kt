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
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.ImgPath
import com.example.myo_jib_sa.community.adapter.PostEditAdapter
import com.example.myo_jib_sa.community.api.imgUpload.imgUploadRetrofitManager
import com.example.myo_jib_sa.community.api.post.ImageList
import com.example.myo_jib_sa.community.api.post.PostCreateRequest
import com.example.myo_jib_sa.community.api.post.PostEditRequest
import com.example.myo_jib_sa.community.api.post.PostRetrofitManager
import com.example.myo_jib_sa.databinding.ActivityWritePostingBinding
import java.io.ByteArrayOutputStream
import java.io.File

class PostEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWritePostingBinding

    private var imgList: List<String> = listOf()

    private var imgListEdit:List<ImageList> = listOf(ImageList(0,""), ImageList(0, ""))

    private var isEdit:Boolean=false
    private var postId:Long=0 //수정할 때만 씀
    private var boardId:Int=0

    //이미지 url 저장
    private var imgUrlList:MutableList<String> = mutableListOf()

    //이미지가 있는 지, 갤러리에서 첨부한 이미지만 api를 통해 업로드하기 위해 사용하는 변수
    private var isHasNewImg:Boolean=false

    //이미지 포지션 저장
    private var imgPosition=0

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

        boardId=intent.getIntExtra("boardId", 0)
        //게시판 이름
        when(boardId.toLong()){
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

        //이미지 어댑터
        adapter = PostEditAdapter(this, imgUrlList)
        binding.postWriteImgRecy.adapter=adapter

        adapter.setOnItemClickListener(object : PostEditAdapter.OnItemClickListener {

            override fun onDeleteClick(postition: Int) {
                imgUrlList.drop(postition) //지우기
                adapter.notifyDataSetChanged()
            }

            override fun onImageClick(position: Int) {
                imgPosition=position //이미지 터치시 갤러리 가서 이미지 추가
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //이미지
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
            }
        })

        //뒤로가기 버튼
        binding.postWriteBackBtn.setOnClickListener {
            finish()
        }

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

                imgUpload(listOf(imgPath) as List<String>){fileUrl ->
                    if(imgPosition==0){
                        imgUrlList.add(fileUrl) //이미지 추가
                    }else{
                        imgUrlList[imgPosition]=fileUrl //이미지 변경
                    }
                    adapter.notifyDataSetChanged()
                }


                /* if(requestCode== GALLERY_REQUEST_CODE){
                     binding.writePostPlusImgLayout.backgroundTintList=
                         ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
                     binding.missionCertImg.setImageURI(uri)
                     isHasNewImg=true
                     imgList = imgList.toMutableList().apply {
                         set(0, getRealPathFromURI(uri).toString())
                     }
                 }else{
                     binding.writePostPlusImgLayout1.backgroundTintList=
                     ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
                     binding.missionCertImg1.setImageURI(uri)
                     isHasNewImg=true
                     imgList = imgList.toMutableList().apply {
                         set(1, getRealPathFromURI(uri).toString())
                     }
                 }*/
            }
        }
    }

    //수정일시 데이터 설정
    private fun setData(){
        binding.postWritePostTextEtxt.setText(intent.getStringExtra("postText"))
        binding.writePostTitleEtxt.setText(intent.getStringExtra("title"))

        //이미지 설정
        //todo:이미지 설정하기
        /*if(!intent.getStringExtra("imgList1_path").toString().isNullOrBlank()){
            //todo : 이미지 설정
            //setImgGlide(binding.missionCertImg, intent.getStringExtra("imgList1_path").toString())
            //배경
            //binding.writePostPlusImgLayout.backgroundTintList=ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))

            //url 저장
            imgUrlList[0]= intent.getStringExtra("imgList1_path").toString()
        }
        if(!intent.getStringExtra("imgList2_path").toString().isNullOrBlank()){
            // setImgGlide(binding.missionCertImg1, intent.getStringExtra("imgList2_path").toString())
            //배경
            //binding.writePostPlusImgLayout1.backgroundTintList=ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))

            //url 저장
            imgUrlList[0]= intent.getStringExtra("imgList2_path").toString()
        }*/

        //이후 사용될 데이터 저장
        postId=intent.getLongExtra("postId",0)
        /*val list1=
            ImageList(intent.getLongExtra("imgList1_id",0), intent.getStringExtra("imgList1_path").toString())
        val list2=
            ImageList(intent.getLongExtra("imgList2_id",0), intent.getStringExtra("imgList2_path").toString())
        imgListEdit = listOf(list1, list2)*/
    }

    //글쓰기, 수정 완료
    private fun complete(){
        binding.postWriteCompleteBtn.setOnClickListener {
            var request: PostEditRequest
            /*todo: 이미지 수정
                request= PostEditRequest(
                    binding.writePostTitleEtxt.text.toString()
                    , binding.postWritePostTextEtxt.text.toString().replace("\n", "\\n")
                    , imgUrlList))
            }

            //api로 콜 보냄
            Constance.jwt?.let { it1 ->
                editing(it1, request, postId){ isSuccess->
                    if(isSuccess){
                        finish()
                    }else{
                        showToast("게시글 수정 실패")
                    }
                }
            }*/

            /*ImgUpload(imgList){isSuccess-> //이미지 있는 경우만 업로드 함
                if(isSuccess){
                    if(!isEdit){ //새로운 글쓰기
                        var request: PostCreateRequest
                        if(!isHasNewImg){
                            request= PostCreateRequest(
                                binding.writePostTitleEtxt.text.toString(),
                                binding.postWritePostTextEtxt.text.toString().replace("\n", "<br>"),
                                emptyList() //이미지 리스트 넣음
                            )
                        }else{
                            request= PostCreateRequest(
                                binding.writePostTitleEtxt.text.toString(),
                                binding.postWritePostTextEtxt.text.toString().replace("\n", "<br>"),
                                imgUrlList //이미지 리스트 넣음
                            )
                        }

                        Constance.jwt?.let { it1 ->
                            posting(it1,request, boardId.toLong()){ isSuccess->
                                if(isSuccess){
                                    finish()
                                }else{
                                    showToast("게시글 쓰기 실패")
                                }
                            }
                        }
                    }else{ //글 수정
                        var request: PostEditRequest
                        //이미지 유무에 따라 분기 나누기
                        if(isHasNewImg){
                            imgListEdit[0].filePath=imgUrlList[0]
                            imgListEdit[1].filePath=imgUrlList[1]
                            request= PostEditRequest(
                                binding.writePostTitleEtxt.text.toString()
                                , binding.postWritePostTextEtxt.text.toString().replace("\n", "\\n")
                                , imgListEdit)
                        }else{
                            request= PostEditRequest(
                                binding.writePostTitleEtxt.text.toString()
                                , binding.postWritePostTextEtxt.text.toString().replace("\n", "\\n")
                                , emptyList()
                            )
                        }

                        //api로 콜 보냄
                        Constance.jwt?.let { it1 ->
                            editing(it1, request, postId){ isSuccess->
                                if(isSuccess){
                                    finish()
                                }else{
                                    showToast("게시글 수정 실패")
                                }
                            }
                        }
                    }

                }
            }

        }*/
        }
    }

    //게시글쓰기 api 연결
    private fun posting(author:String, request: PostCreateRequest, categoryId:Long
                        , callback: (Boolean) -> Unit){
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
                showToast("게시글 업로드 실패")
                callback(false)
            }
        }
    }

    //수정 api 연결
    private fun editing(author:String, request: PostEditRequest, postId:Long
                        , callback: (Boolean) -> Unit){
        val retrofitManager = PostRetrofitManager.getInstance(this)

        //게시물 생성 api 연결
        retrofitManager.postEdit(author,request, postId){response ->
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
    private fun imgUpload(imgPath:List<String>, callback: (String) -> Unit){
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
                val message = response.message
                Log.d("이미지 업로드 결과", "$message")
                Log.d("이미지 업로드 결과", "$imageUrl")
                if(isSuccess=="true"){
                    Log.d("이미지 업로드 결과", "isSuccess")
                    if(response.result.isNullOrEmpty()){
                        callback("")
                    }else{
                        //이미지 url 저장
                        callback(response.result[0])
                    }

                }else{
                    Log.d("이미지 업로드 결과", "isSuccess이 false")
                    showToast("이미지 업로드 실패")
                    callback("")
                }

            } else {
                Log.d("이미지 업로드 결과", "실패")
                showToast("이미지 업로드 실패")

                callback("")
            }
        }
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