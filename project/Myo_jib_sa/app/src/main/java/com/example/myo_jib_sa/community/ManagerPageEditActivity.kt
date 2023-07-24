package com.example.myo_jib_sa.community

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.manager.ManagerRetrofitManager
import com.example.myo_jib_sa.community.Retrofit.post.ArticleImage
import com.example.myo_jib_sa.community.Retrofit.post.PostRetrofitManager
import com.example.myo_jib_sa.databinding.ActivityManagerPageBinding
import com.example.myo_jib_sa.databinding.ActivityManagerPageEditBinding
import java.io.ByteArrayOutputStream

class ManagerPageEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManagerPageEditBinding
    private var imgPath:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityManagerPageEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val boardId =intent.getLongExtra("boardId", 0)

        //바꾼 사진 저장
        binding.managerPageCompleteBtn.setOnClickListener {
            setPhoto(Constance.jwt, boardId)
            val resultIntent = Intent()
            resultIntent.putExtra("imgPath", imgPath)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        //갤러리에서 사진 선택하기
        binding.managerPageImgEditBtn.setOnClickListener {
            openGallery()
        }
        binding.managerPageImg

    }

    private fun setPhoto(author:String, boardId:Long){
        val retrofitManager = ManagerRetrofitManager.getInstance(this)
        retrofitManager.missionImgEdit(author,imgPath ,boardId){response ->
            if(response){
                finish()
            } else {
                showToast("바꾼 사진을 저장하지 못했습니다.")
                Log.d("게시글 API isSuccess가 false", "")
            }


        }
    }

    //갤러리에서 사진 선택
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageUri = data?.data
            // 이제 선택한 이미지를 "managerPageImg" ImageView에 설정
            binding.managerPageImg.setImageURI(imageUri)

            // 이미지의 파일 경로를 가져와 "imgPath" 변수에 저장
            imgPath = encodeImageToBase64(imageUri.toString())
        }
    }

    // 이미지 파일을 Base64 형식으로 변환하는 함수
    private fun encodeImageToBase64(imagePath: String): String {
        val bitmap = BitmapFactory.decodeFile(imagePath)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    //토스트 메시지 띄우기
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    //갤러리 REQUEST_CODE
    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }
}