package com.example.myo_jib_sa.community.manager

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.ImgPath
import com.example.myo_jib_sa.community.api.imgUpload.imgUploadRetrofitManager
import com.example.myo_jib_sa.community.api.manager.ManagerRetrofitManager
import com.example.myo_jib_sa.databinding.ActivityManagerImgBinding
import java.io.File

class ManagerImgActivity : AppCompatActivity() {

    private lateinit var binding:ActivityManagerImgBinding
    private var imgUri: Uri? =null
    private var imgUrl:String=""
    private var boardId:Long=0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityManagerImgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인텐트에서 데이터 가져오기
        val imageUriString = intent.getStringExtra("imgUri")
        boardId=intent.getLongExtra("boardId",0L)

        // URI 문자열을 Uri 객체로 변환
        imgUri = imageUriString?.let { Uri.parse(it) }

        // 이미지뷰에 이미지 설정
        binding.imgImg.setImageURI(imgUri)

        binding.imgPostTxt.setOnClickListener {
            setPhoto(boardId){ isSuccess->
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

    //사진 저장 api
    private fun setPhoto(boardId:Long, callback: (Boolean) -> Unit){
        val retrofitManager = ManagerRetrofitManager.getInstance(this)
        ImgUpload(){isSuccess->
            if(isSuccess){
                retrofitManager.missionImgEdit(imgUrl ,boardId){response ->
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
                val message = response.errorMessage
                Log.d("이미지 업로드 결과", "$message")
                Log.d("이미지 업로드 결과", "$imageUrl")
                if(isSuccess){
                    Log.d("이미지 업로드 결과", "isSuccess")
                    imgUrl=response.result[0].imageUrl
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

    //토스트 메시지 띄우기
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}