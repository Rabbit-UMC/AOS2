package com.example.myo_jib_sa.community.manager

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.ImgPath
import com.example.myo_jib_sa.community.api.imgUpload.imgUploadRetrofitManager
import com.example.myo_jib_sa.community.api.manager.ManagerRetrofitManager
import com.example.myo_jib_sa.databinding.ActivityManagerPageEditBinding
import java.io.ByteArrayOutputStream
import java.io.File

class ManagerPageEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManagerPageEditBinding
    private var imgUri:Uri= Uri.EMPTY
    private var imgUrl:String=""

    private var missionImg:String=""

    //갤러리 REQUEST_CODE
    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityManagerPageEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val boardId =intent.getLongExtra("boardId", 0)
        missionImg=intent.getStringExtra("missionImg").toString()

        binding.managerPageImg.clipToOutline=true //모서리 궁글게
        //이미지 설정
        if(missionImg.isNotBlank()){
            binding.managerPageIcImg.visibility= View.GONE
            binding.constraintLayout.backgroundTintList=
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
            Glide.with(this)
                .load(missionImg)
                .into(binding.managerPageImg)
        }else{ //기본 이미지로 설정
            binding.constraintLayout.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#F1F1F1"))
            setMissionIcon(boardId)
        }

        //바꾼 사진 저장
        binding.managerPageCompleteBtn.setOnClickListener {
            Constance.jwt?.let { it1 ->
                setPhoto(it1, boardId){ isSuccess->
                    if(isSuccess){ //저장 성공 시에만 종료
                        val resultIntent = Intent()
                        resultIntent.putExtra("imgPath", imgUrl)
                        Log.d("바뀐 이미지 intent에 넣어", imgUrl)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }

                }
            }
        }

        //갤러리에서 사진 선택하기
        binding.managerPageImgEditBtn.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }
        binding.managerPageImg

    }

    //사진 저장 api
    private fun setPhoto(author:String, boardId:Long, callback: (Boolean) -> Unit){
        val retrofitManager = ManagerRetrofitManager.getInstance(this)
        ImgUpload(){isSuccess->
            if(isSuccess){
                retrofitManager.missionImgEdit(author,imgUrl ,boardId){response ->
                    if(response){
                        Log.d("관리자 페이지 사진 저장", "성공!!")
                        callback(true)
                    } else {
                        showToast("사진을 저장하지 못했습니다.")
                        Log.d("관리자 페이지 사진 저장", "실패 ㅜㅠ")
                        callback(false)
                    }
                }
            }else{
                showToast("사진을 저장하지 못했습니다.")
                callback(false)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            // 선택한 이미지를 해당 이미지뷰에 표시
            selectedImageUri?.let { uri ->
                val imageView: ImageView = binding.managerPageImg
                imageView.setImageURI(uri)
                imgUri=uri
            }
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

    //이미지 업로드 api
    private fun ImgUpload(callback: (Boolean) -> Unit){
        val imgpath = getRealPathFromURI(imgUri)
        Log.d("이미지 경로", "${imgpath}")
        val imageFile = File(imgpath) // 이미지 파일 경로
        Log.d("이미지 파일 경로", imageFile.toString())

        val imgUploadRetrofitManager = imgUploadRetrofitManager(this)
        val imgList:List<File> = listOf(imageFile)
        imgUploadRetrofitManager.uploadImage(imgList, ImgPath.CATEGORY) { response ->
            if (response != null) {
                val imageUrl = response.result[0]
                val isSuccess = response.isSuccess
                val message = response.message
                Log.d("이미지 업로드 결과", "$message")
                Log.d("이미지 업로드 결과", "$imageUrl")
                if(isSuccess=="true"){
                    Log.d("이미지 업로드 결과", "isSuccess")
                    imgUrl=imageUrl
                    callback(true)

                }else{
                    Log.d("이미지 업로드 결과", "isSuccess이 false")
                    callback(false)
                }

            } else {
                Log.d("이미지 업로드 결과", "실패")
                callback(false)
            }
        }
    }

    //기본 이미지 설정
    private fun setMissionIcon(boardId:Long){
        when(boardId){
            Constance.ART_ID -> {
                val drawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.ic_mission_art_p)
                binding.managerPageIcImg.setImageDrawable(drawable)
            }
            Constance.FREE_ID -> {
                val drawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.ic_mission_free_p)
                binding.managerPageIcImg.setImageDrawable(drawable)
            }
            Constance.EXERCISE_ID -> {
                val drawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.ic_mission_exercise_p)
                binding.managerPageIcImg.setImageDrawable(drawable)
            }
        }
    }


    //토스트 메시지 띄우기
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}