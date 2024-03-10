package com.example.myo_jib_sa.community.missionCert

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.example.myo_jib_sa.community.dialog.CommunityMissionCertPostDialog
import com.example.myo_jib_sa.databinding.ActivityMissionCertificationWriteCheckBinding
import com.example.myo_jib_sa.databinding.ToastRedBlackBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class MissionCertificationWriteCheckActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMissionCertificationWriteCheckBinding
    private var boardId:Long=0
    private var isFinish:Boolean=false
    private var isCamera:Boolean=false
    private val intent = Intent(this, MissionCertificationWriteActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionCertificationWriteCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isCamera=intent.getBooleanExtra("isCamera", false)
        boardId=intent.getLongExtra("boardId", 0L)
        Log.d("게시판 아이디", boardId.toString())

        //이미지 설정
        val imgUri: Uri? = intent.getParcelableExtra("imgUri")
        binding.missionCertImg.setImageURI(imgUri)

        //뒤로 가기
        binding.missionCertBackBtn.setOnClickListener {
            startActivity(intent)
            finish()
        }

        //게시하기
        binding.missionCertCompleteTxt.setOnClickListener {
            val imgPath = getRealPathFromURI(imgUri)
            if (imgPath != null) {
                val file = File(imgPath) // 이미지 파일 경로
                val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                val body = MultipartBody.Part.createFormData("multipartFile", file.name, requestFile)
                Log.d("이미지 파트", body.toString())

                postImg(boardId, body)
                if (isFinish) {
                    intent.putExtra("isFinish", true)
                    startActivity(intent)
                    finish()
                }
            } else {
                // 이미지 경로를 가져올 수 없을 경우에 대한 처리
                // 예: Toast 메시지 출력 또는 로그 등
                Log.e("MissionCertWriteCheck", "Failed to retrieve image path")
            }

        }
    }

    //이미지 첨부 api
    private fun postImg(boardId:Long, img:MultipartBody.Part ){
        val reportDialog = CommunityMissionCertPostDialog(boardId, img)

        reportDialog.setReportDialogListener(object : CommunityMissionCertPostDialog.ReportDialogListener {
            override fun onReportSubmitted(message: String) {
                Log.d("onReportSubmitted", message)
                // 뷰 바인딩을 사용하여 커스텀 레이아웃을 인플레이트합니다.
                val snackbarBinding = ToastRedBlackBinding.inflate(layoutInflater)
                snackbarBinding.toastRedBlackTxt.text=message

                // 스낵바 생성 및 설정
                val snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_SHORT).apply {
                    animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                    (view as Snackbar.SnackbarLayout).apply {
                        setBackgroundColor(Color.TRANSPARENT)
                        addView(snackbarBinding.root)
                        translationY = -70.dpToPx(this.context).toFloat()
                        elevation = 0f
                    }
                }

                if(message=="작성하신 미션 인증 글이 저장되었어요."){
                    isFinish=true
                    intent.putExtra("isFinish", true)
                    startActivity(intent)
                    finish()
                }

                // 스낵바 표시
                snackbar.show()
                reportDialog.dismiss()
            }
        })

        Log.d("showReportDialog","report ID: ${img}")
        reportDialog.show(this.supportFragmentManager, "mission_report_dialog")

    }

    private fun Int.dpToPx(context: Context): Int {
        val scale = context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
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



}