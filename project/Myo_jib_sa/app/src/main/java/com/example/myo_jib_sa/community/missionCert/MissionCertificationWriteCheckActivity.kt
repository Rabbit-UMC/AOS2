package com.example.myo_jib_sa.community.missionCert

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.ImgPath
import com.example.myo_jib_sa.community.api.imgUpload.imgUploadRetrofitManager
import com.example.myo_jib_sa.community.api.missionCert.MissionCertRetrofitManager
import com.example.myo_jib_sa.community.dialog.CommunityDialogBlueBlack
import com.example.myo_jib_sa.community.dialog.CommunityDialogRedBlack
import com.example.myo_jib_sa.community.dialog.CommunityMissionCertPostDialog
import com.example.myo_jib_sa.community.dialog.CommunityMissionCertReportDialog
import com.example.myo_jib_sa.databinding.ActivityMissionCertificationWriteCheckBinding
import com.example.myo_jib_sa.databinding.ToastMissionReportBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class MissionCertificationWriteCheckActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMissionCertificationWriteCheckBinding
    private var imgUrl:String=""
    private var boardId:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionCertificationWriteCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardId=intent.getLongExtra("boardId", 0)

        //이미지 설정
        val imgUri: Uri? = intent.getParcelableExtra("imgUri")
        binding.missionCertImg.setImageURI(imgUri)

        //뒤로 가기
        binding.missionCertBackBtn.setOnClickListener {
            val intent= Intent(this, MissionCertificationWriteActivity::class.java)
            startActivity(intent)
            finish()
        }

        //게시하기
        binding.missionCertCompleteTxt.setOnClickListener {
            val imgPath = getRealPathFromURI(imgUri)
            val imageFile = File(imgPath) // 이미지 파일 경로
            val requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile)
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            postImg(boardId, imagePart)

        }
    }

    //이미지 첨부 api
    private fun postImg(boardId:Long, img:MultipartBody.Part ){
        val reportDialog = CommunityMissionCertPostDialog(boardId, img)

        reportDialog.setReportDialogListener(object : CommunityMissionCertPostDialog.ReportDialogListener {
            override fun onReportSubmitted(message: String) {
                Log.d("onReportSubmitted", "$message")
                // 뷰 바인딩을 사용하여 커스텀 레이아웃을 인플레이트합니다.
                val snackbarBinding = ToastMissionReportBinding.inflate(layoutInflater)
                snackbarBinding.toastMissionReportTxt.text = message

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