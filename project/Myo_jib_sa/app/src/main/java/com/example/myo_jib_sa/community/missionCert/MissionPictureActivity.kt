package com.example.myo_jib_sa.community.missionCert

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.api.missionCert.MissionCertRetrofitManager
import com.example.myo_jib_sa.community.dialog.CommunityMissionCertReportDialog
import com.example.myo_jib_sa.databinding.ActivityMissionPictureBinding
import com.example.myo_jib_sa.databinding.ToastRedBlackBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MissionPictureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMissionPictureBinding

    //이미지
    private var filePath:String=""
    private var isReportable:Boolean=false //신고가능인지
    private var imgId:Long=0 //이미지 아이디
    private var isLike:Boolean=false //좋아요 여부
    private var position:Int=0
    private var date:Int=0
    private var mainMissionId:Long=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인텐트에 값 전달해
        filePath=intent.getStringExtra("filePath").toString()
        isReportable=intent.getBooleanExtra("isReportable", false)
        imgId=intent.getLongExtra("imgId", 0)
        isLike=intent.getBooleanExtra("isLike", false)
        position=intent.getIntExtra("position", 0)
        date=intent.getIntExtra("date", 0)
        mainMissionId=intent.getLongExtra("mainMissionId", 0)

        binding.missionCertTitleTxt.text=missioncertInfo.title
        binding.missionCertMemoTxt.text=missioncertInfo.memo

        //이미지 설정
        Glide.with(this)
            .load(filePath)
            .into(binding.missionCertImg)

        //todo : 뷰 설정하기 (사진 조회 api 개발 후)

        //좋아요 아이콘 설정
        setLike()

        //신고하기
        clikeReport()

        //좋아요 누르기
        clickLike()


        //화면 닫기
        binding.missionCertPictureBackBtn.setOnClickListener {
            finish()
        }
    }
    private fun setView(){
        //todo :
    }

    private fun setLike() {
        if(isLike){
            binding.likeImg.setImageResource(R.drawable.ic_like)
        }else{
            binding.likeImg.setImageResource(R.drawable.ic_unlike)
        }
    }

    //좋아요 누르기
    private fun clickLike(){
        binding.likeImg.setOnClickListener{
            if(isLike){
                    unlike(imgId){ isSuccess->
                        if(isSuccess){
                            binding.likeImg.setImageResource(R.drawable.ic_unlike)
                            setView() //좋아요 수 갱신
                            isLike=!isLike
                        }
                    }

            }else{
                like(imgId) { isSuccess->
                    if(isSuccess){
                        binding.likeImg.setImageResource(R.drawable.ic_like)
                        setView() //좋아요 수 갱신
                        isLike=!isLike
                    }
                }

            }
        }
    }

    //좋아요
    private fun like(imgId:Long, callback: (Boolean) -> Unit){
        val retrofitManager = MissionCertRetrofitManager.getInstance(this)
        retrofitManager.missionImgLike(imgId){response ->
            if(response){
                Log.d("missionImgLike", "missionImgLike 성공")
                updateLikeCnt()
                callback(true)
            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                Log.d("missionImgLike", "missionImgLike 실패")
                showToast("좋아요 실패")
                callback(false)
            }


        }
    }


    //좋아요 삭제
    private fun unlike(imgId:Long, callback: (Boolean) -> Unit){
        val retrofitManager = MissionCertRetrofitManager.getInstance(this)
        retrofitManager.missionImgUnlike(imgId){response ->
            if(response){
                Log.d("missionImgUnlike", "missionImgUnlike 성공")
                updateLikeCnt()
                callback(true)
            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                Log.d("missionImgUnlike", "missionImgUnlike 실패")
                showToast("좋아요 취소 실패")
                callback(false)
            }


        }
    }

    //신고 누르기
    private fun clikeReport(){
        //신고하기
        binding.reportTxt.setOnClickListener {
            report(imgId)
        }
    }

    //신고 api
    private fun report(imgId:Long){
        val reportDialog = CommunityMissionCertReportDialog(imgId)

        reportDialog.setReportDialogListener(object : CommunityMissionCertReportDialog.ReportDialogListener {
            override fun onReportSubmitted(message: String) {
                Log.d("onReportSubmitted", "$message")
                // 뷰 바인딩을 사용하여 커스텀 레이아웃을 인플레이트합니다.
                val snackbarBinding = ToastRedBlackBinding.inflate(layoutInflater)
                snackbarBinding.toastRedBlackTxt.text = message

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

        Log.d("showReportDialog","report ID: ${imgId}")
        reportDialog.show(this.supportFragmentManager, "mission_report_dialog")
    }

    //토스트 메시지
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun Int.dpToPx(context: Context): Int {
        val scale = context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    //좋아요 수 업데이트
    private fun updateLikeCnt(){
        val retrofitManager = MissionCertRetrofitManager.getInstance(this)
        retrofitManager.mission(date, mainMissionId) { response ->
            Log.d("setMissionCertFrag 미션 인증 날짜 확인", date.toString())
            Log.d("setMissionCertFrag 미션 인증 사진 현재 위치 확인", position.toString())
            if (response.isSuccess) {
                if (response.result != null) {
                    binding.likeCntTxt.text=response.result.missionProofImages[position].likeCount.toString()
                }

            } else {
                Log.d("뷰페이져 어댑터로 리스트 전달", "List가 비었다네요")
            }
        }
    }

    //미션 인증 api 연결


}