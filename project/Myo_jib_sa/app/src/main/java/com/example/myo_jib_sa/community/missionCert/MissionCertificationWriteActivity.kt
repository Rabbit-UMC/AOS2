package com.example.myo_jib_sa.community.missionCert

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private var isFinish:Boolean=false

    private var imgUri:Uri= Uri.EMPTY
    private var imgUrl:String=""

    companion object {
        const val GALLERY_REQUEST_CODE = 1001
        const val CAMERA_REQUEST_CODE = 1002

        const val CAMERA_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionCertificationWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardId=intent.getIntExtra("boardId", 0)
        isFinish=intent.getBooleanExtra("isFinish", false)
        if(isFinish){
            finish()
        }

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
            startCamera()
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
                    }
                } //카메라
                CAMERA_REQUEST_CODE -> {
                    val photo: Bitmap = data?.extras?.get("data") as Bitmap

                    // 이제 가져온 이미지를 사용하면 됩니다.
                    // 예를 들어, 다른 액티비티로 전달하거나 특정 동작을 수행할 수 있습니다.
                    val intent = Intent(this, MissionCertificationWriteCheckActivity::class.java)
                    intent.putExtra("photo", photo) // 필요한 경우 이미지를 다른 액티비티로 전달할 수 있습니다.
                    startActivity(intent)
                }
            }
        }
    }

    //Intent를 통해 전달된 데이터에서 이미지를 가져옴
    private fun getBitmapFromIntentData(data: Intent): Bitmap? {
        val extras: Bundle? = data.extras
        val byteArray: ByteArray? = extras?.getByteArray("data")

        // byteArray로부터 Bitmap 객체 생성
        return byteArray?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
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

    // 권한이 있는지 확인하는 함수
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 권한 요청하는 함수
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    // 카메라 권한 확인 후 실행되는 코드
    private fun startCamera() {
        if (checkCameraPermission()) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
            }
        } else {
            // 권한이 없는 경우 권한 요청
            requestCameraPermission()
        }
    }

    // onRequestPermissionsResult를 통해 사용자의 권한 요청 응답 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 사용자가 권한을 허용한 경우 카메라 시작
                    startCamera()
                } else {
                    // 사용자가 권한을 거부한 경우 처리
                    // 권한이 필요한 이유를 사용자에게 설명하고 필요한 조치를 안내할 수 있음
                }
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

    //토스트 메시지
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}