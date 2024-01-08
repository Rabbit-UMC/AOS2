package com.example.myo_jib_sa.community.missionCert

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.ImgPath
import com.example.myo_jib_sa.community.api.imgUpload.imgUploadRetrofitManager
import com.example.myo_jib_sa.community.api.missionCert.MissionCertRetrofitManager
import com.example.myo_jib_sa.databinding.ActivityMissionCertificationWriteBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class MissionCertificationWriteActivity: AppCompatActivity() {
    private lateinit var binding:ActivityMissionCertificationWriteBinding
    private var imgPath:String=""
    private var boardId:Int=0

    private var imgUri:Uri= Uri.EMPTY
    private var imgUrl:String=""

    companion object {
        const val GALLERY_REQUEST_CODE = 1001
        const val CAMERA_REQUEST_CODE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionCertificationWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardId=intent.getIntExtra("boardId", 0)

        //binding.missionCertImg.clipToOutline=true //둥근 모서리 todo

        when(boardId.toLong()){
            Constance.ART_ID-> {
                binding.missionCertBoardNameTxt.text="예술 게시판"
            }
            Constance.FREE_ID-> {
                binding.missionCertBoardNameTxt.text="자유 게시판"
            }
            Constance.EXERCISE_ID-> {
                binding.missionCertBoardNameTxt.text="운동 게시판"
            }

        }

        //뒤로가기
        binding.missionCertBackBtn.setOnClickListener {
            finish()
        }


        binding.missionCertGalleryConstraint.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }

        //todo: 카메라로 사진 찍어서 올리기
        binding.missionCertCameraConstraint.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
            } else {
                Toast.makeText(this, "카메라 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

       /* binding.missionCertCompleteTxt.setOnClickListener {
            Constance.jwt?.let { it1 ->
                postImg(it1, boardId.toLong()){ isSuccess->
                    if(isSuccess){
                        finish()
                    }
                }
            }

        }*/

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {//갤러리
                GALLERY_REQUEST_CODE -> {
                    if (data != null && data.data != null) {
                        val selectedImageUri: Uri = data.data!!
                        val intent = Intent(this, MissionCertificationWriteCheckActivity::class.java)
                        intent.putExtra("imgUri", selectedImageUri)
                        startActivity(intent)
                        finish()
                    }
                } //카메라
                CAMERA_REQUEST_CODE -> {
                    val imageBitmap: Bitmap? = data?.let { getBitmapFromIntentData(it) }
                    imageBitmap?.let { bitmap ->
                        val uri: Uri? = bitmapToUri(bitmap, this)
                        uri?.let {
                            val intent = Intent(this, MissionCertificationWriteCheckActivity::class.java)
                            intent.putExtra("imgUri", it)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    //Intent를 통해 전달된 데이터에서 이미지를 가져옴
    private fun getBitmapFromIntentData(data: Intent): Bitmap? {
        return if (data?.data != null) {
            val selectedImageUri: Uri = data.data!!
            val inputStream: InputStream? = contentResolver.openInputStream(selectedImageUri)
            BitmapFactory.decodeStream(inputStream)
        } else {
            null
        }
    }

    //bitmap -> uri convert
    //임시 저장소에 저장
    fun bitmapToUri(bitmap: Bitmap, context: Context): Uri? {
        val wrapper = ContextWrapper(context.applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "${System.currentTimeMillis()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        return FileProvider.getUriForFile(
            context.applicationContext,
            "${context.applicationContext.packageName}.fileprovider",
            file
        )
    }

    //이미지 첨부 api
    private fun postImg(author:String ,boardId:Long, callback: (Boolean) -> Unit){
        val retrofitManager = MissionCertRetrofitManager.getInstance(this)
        val imgpath = getRealPathFromURI(imgUri)

        ImgUpload(imgpath.toString()) { isSuccess ->
            if(isSuccess){
                Log.d("mission postImg", "uploadImge 성공")
                retrofitManager.postImg(author, boardId, imgUrl) { response ->
                    if (response) {
                        Log.d("mission postImg", "postImge 성공")
                        callback(true)
                    } else {
                        showToast("이미지 업로드에 실패했습니다.")
                        // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                        Log.d("mission postImg", "postImg 실패")
                        callback(false)
                    }

                }
            }else{
                showToast("이미지 업로드에 실패했습니다.")
                Log.d("mission postImg", "uploadImge 실패")
                callback(false)
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
    fun ImgUpload(imgPath:String, callback: (Boolean) -> Unit){
        val imageFile = File(imgPath) // 이미지 파일 경로
        val imgList= listOf(imageFile)
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

    //토스트 메시지
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}