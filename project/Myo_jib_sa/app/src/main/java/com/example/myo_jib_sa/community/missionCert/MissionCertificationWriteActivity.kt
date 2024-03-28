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
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import java.text.SimpleDateFormat
import java.util.Date

class MissionCertificationWriteActivity: AppCompatActivity() {
    private lateinit var binding:ActivityMissionCertificationWriteBinding
    private var boardId:Long=0L
    private var isFinish:Boolean=false

    private var imgUri:Uri= Uri.EMPTY

    companion object {
        const val GALLERY_REQUEST_CODE = 1001
        const val CAMERA_REQUEST_CODE = 1002

        const val CAMERA_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionCertificationWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardId=intent.getLongExtra("boardId", 0L)
        isFinish=intent.getBooleanExtra("isFinish", false)
        if(isFinish){
            finish()
        }
        Log.d("게시판 아이디", boardId.toString())

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

        //카메라로 사진 찍어서 올리기
        //binding.missionCertCameraConstraint.setOnClickListener {
         //   startCamera()
        //}

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
                        Log.d("전달할 이미지", selectedImageUri.toString())
                        intent.putExtra("boardId", boardId)
                        Log.d("전달할 게시판 아이디", boardId.toString())
                        startActivity(intent)
                    }
                } //카메라
                CAMERA_REQUEST_CODE -> {
                    // 사진이 촬영되었으므로 여기서 imgUri 변수를 사용할 수 있습니다.
                    if (imgUri != null) {
                        val intent = Intent(this, MissionCertificationWriteCheckActivity::class.java)
                        intent.putExtra("imgUri", imgUri)
                        intent.putExtra("isCamera", true)
                        Log.d("이미지 uri", imgUri.toString())
                        intent.putExtra("boardId", boardId)
                        startActivity(intent)
                    } else {
                        showToast("사진 촬영에 실패했습니다.")
                    }
                }
            }
        }
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

    // 사진을 저장할 파일 경로
    private var currentPhotoPath: String? = null

    // 카메라 권한 확인 후 실행되는 코드
    private fun startCamera() {
        if (checkCameraPermission()) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                // 이미지를 저장할 임시 파일 생성
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }

                // 파일이 성공적으로 생성된 경우에만 계속 진행
                photoFile?.also {

                    imgUri = FileProvider.getUriForFile(
                        applicationContext,
                        "${packageName}.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                }
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

    //토스트 메시지
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // 이미지를 저장할 임시 파일을 생성
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // 이미지 파일 이름 생성
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* 접두사 */
            ".jpg", /* 확장자 */
            storageDir /* 디렉토리 */
        )
    }


}