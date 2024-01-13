package com.example.myo_jib_sa.community.missionCert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.api.missionCert.MissionCertRetrofitManager
import com.example.myo_jib_sa.community.dialog.CommunityDialogRedBlack
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
    private var isLike:Boolean=false //좋아요 여부


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인텐트에 값 전달해
        filePath=intent.getStringExtra("filePath").toString()
        isReportable=intent.getBooleanExtra("isReportable", false)
        imgId=intent.getLongExtra("imgId", 0)
        isLike=intent.getBooleanExtra("isLike", false)

        binding.missionCertTitleTxt.text=missioncertInfo.title
        binding.missionCertMemoTxt.text=missioncertInfo.memo

        //이미지 설정
        Glide.with(this)
            .load(filePath)
            .into(binding.missionCertImg)

        //신고 설정
        /*if(!isReportable){
            binding.imgCheckReportTxt.visibility= View.GONE
        }*/

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

        //이미지 다운로드 todo : 삭제하기
        //binding.imgCheckDownloadBtn.setOnClickListener {
        //    download()
        //}


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
                Constance.jwt?.let { it1 ->
                    unlike(it1,imgId){ isSuccess->
                        if(isSuccess){
                            binding.likeImg.setImageResource(R.drawable.ic_unlike)
                            setView() //좋아요 수 갱신
                            isLike=!isLike
                        }
                    }
                }
            }else{
                Constance.jwt?.let { it1 -> like(it1,imgId) { isSuccess->
                    if(isSuccess){
                        binding.likeImg.setImageResource(R.drawable.ic_like)
                        setView() //좋아요 수 갱신
                        isLike=!isLike
                    }
                }
                }
            }
        }
    }

    //좋아요
    private fun like(author:String ,imgId:Long, callback: (Boolean) -> Unit){
        val retrofitManager = MissionCertRetrofitManager.getInstance(this)
        retrofitManager.missionImgLike(author, imgId){response ->
            if(response){
                Log.d("missionImgLike", "missionImgLike 성공")
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
    private fun unlike(author:String ,imgId:Long, callback: (Boolean) -> Unit){
        val retrofitManager = MissionCertRetrofitManager.getInstance(this)
        retrofitManager.missionImgUnlike(author, imgId){response ->
            if(response){
                Log.d("missionImgUnlike", "missionImgUnlike 성공")
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
            val DelDialog = CommunityDialogRedBlack(this, "해당 게시글을 신고하시겠어요?"
                , "다른 사용자에게 불쾌감을 조성하는 게시글인지 다시 확인해주세요."
                ,"게시글을 신고할게요"
                ,"아니요, 취소할게요")
            DelDialog.setCustomDialogListener(object : CommunityDialogRedBlack.CustomDialogListener {
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

            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리

                Log.d("mission report", "mission report 실패")
                showToast("신고 실패")
            }


        }
    }

    //사진 다운로드 todo : 삭제하기.
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

    //todo : 신고 토스트 메시지
    private fun reportToast(){
        // Inflate the custom layout
        val inflater: LayoutInflater = layoutInflater
        val layout: View = inflater.inflate(R.layout.toast_red_black, findViewById(R.id.toast_red_black_constraint))

        val toast = Toast(applicationContext)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.view

        toast.show()
    }

}