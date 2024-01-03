package com.example.myo_jib_sa.community.missionCert

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
import com.example.myo_jib_sa.community.dialog.CommunityDialog
import com.example.myo_jib_sa.community.dialog.CommunityPopupOk
import com.example.myo_jib_sa.databinding.ActivityImageBinding
import com.example.myo_jib_sa.databinding.ActivityMissionPictureBinding
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인텐트에 값 전달해
        filePath=intent.getStringExtra("filePath").toString()
        isReportable=intent.getBooleanExtra("isReportable", false)
        imgId=intent.getLongExtra("imgId", 0)

        //이미지 설정
        Glide.with(this)
            .load(filePath)
            .into(binding.missionCertImg)

        //신고 설정
        /*if(!isReportable){
            binding.imgCheckReportTxt.visibility= View.GONE
        }*/

        //신고하기
        clikeReport()

        //화면 닫기
        binding.imgCheckCancleTxt.setOnClickListener {
            finish()
        }

        //이미지 다운로드
        binding.imgCheckDownloadBtn.setOnClickListener {
            download()
        }


    }

    //신고 누르기
    private fun clikeReport(){
        //신고하기
        binding.reportTxt.setOnClickListener {
            val DelDialog = CommunityDialog(this, "해당 게시글을 신고하시겠어요?"
                , "다른 사용자에게 불쾌감을 조성하는 게시글인지 다시 확인해주세요."
                ,"게시글을 신고할게요"
                ,"아니요, 취소할게요"
                ,"#C1C1C196"
                ,"#F22222")
            DelDialog.setCustomDialogListener(object : CommunityDialog.CustomDialogListener {
                override fun onPositiveButtonClicked(value: Boolean) {
                    if (value){
                        //신고 재확인 팝업창 띄우고 확인 누르면 api 연결
                        Log.d("미션 인증 게시물 신고", "미션 인증 게시물 신고")
                        Constance.jwt?.let { it1 -> report(it1, imgId) }
                    }
                }
            })
            DelDialog.show()
        }
    }

    //신고 api
    private fun report(author:String ,imgId:Long){

        val retrofitManager = MissionCertRetrofitManager.getInstance(this)
        retrofitManager.report(author, imgId){response ->
            if(response){
                Log.d("mission report", "mission report 성공")
                showToast("신고 완료")
            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리

                Log.d("mission report", "mission report 실패")
                showToast("신고 실패")
            }


        }
    }

    //사진 다운로드
    private fun download(){
        val request = Request.Builder()
            .url(filePath)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 다운로드 실패 처리
                runOnUiThread {
                    // 토스트 메시지 출력
                    showToast("사진 저장 실패")
                }

            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val tempResponseBody = response.peekBody(Long.MAX_VALUE) // 임시 복사본 생성
                    val inputStream = tempResponseBody.byteStream()
                    if (inputStream != null) {
                        val fileName = filePath.substringAfterLast("/") // 파일명 추출
                        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName)
                        val outputStream = FileOutputStream(file)
                        inputStream.copyTo(outputStream)

                        outputStream.close()
                        inputStream.close()

                        runOnUiThread {
                            // 토스트 메시지 출력
                            showToast("사진 저장 완료")
                        }
                    }
                } else {
                    // 응답이 실패한 경우 처리
                    runOnUiThread {
                        // 토스트 메시지 출력
                        showToast("사진 저장 실패")
                    }
                }
            }
        })
    }

    //토스트 메시지
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}