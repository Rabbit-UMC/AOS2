package com.example.myo_jib_sa.community.missionCert

import android.content.Intent
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
import com.example.myo_jib_sa.databinding.ActivityMissionCertificationWriteCheckBinding
import java.io.File

class MissionCertificationWriteCheckActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMissionCertificationWriteCheckBinding
    private var imgUrl:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionCertificationWriteCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //이미지 설정
        val imgUri= intent.getStringExtra("imgUri")?.toUri()
        binding.missionCertImg.setImageURI(imgUri)

        //뒤로 가기
        binding.missionCertBackBtn.setOnClickListener {
            val intent= Intent(this, MissionCertificationWriteActivity::class.java)
            startActivity(intent)
            finish()
        }

        //게시하기
        binding.missionCertCompleteTxt.setOnClickListener {
            val DelDialog = CommunityDialogBlueBlack(this, "인증샷을 게시하시겠어요?"
                , "인증샷은 하루에 한번으로 제한됩니다. 업로드 전, 다시 확인 부탁드립니다."
                ,"인증샷을 게시할게요"
                ,"아니요, 취소할게요")
            DelDialog.setCustomDialogListener(object : CommunityDialogBlueBlack.CustomDialogListener {
                override fun onPositiveButtonClicked(value: Boolean) {
                    if (value){
                        //신고 재확인 팝업창 띄우고 확인 누르면 api 연결
                        Log.d("미션 인증 게시글 쓰기", "미션 인증 게시글 쓰기")
                        Constance.jwt?.let { it1 ->
                            if (imgUri != null) {
                                postImg(it1, 1/*todo : boardId.toLong()*/, imgUri){ isSuccess->
                                    if(isSuccess){
                                        finish()
                                    }
                                }
                            }
                        }
                    }
                }
            })
            DelDialog.show()
        }
    }

    //이미지 첨부 api
    private fun postImg(author:String ,boardId:Long, imgUri:Uri,callback: (Boolean) -> Unit){
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
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}