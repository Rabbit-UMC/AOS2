package com.example.myo_jib_sa.community.manager

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myo_jib_sa.databinding.ActivityManagerImgBinding

class ManagerImgActivity : AppCompatActivity() {

    private lateinit var binding:ActivityManagerImgBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityManagerImgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인텐트에서 데이터 가져오기
        val imageUriString = intent.getStringExtra("imgPath")

        // URI 문자열을 Uri 객체로 변환
        val imageUri: Uri? = imageUriString?.let { Uri.parse(it) }

        // 이미지뷰에 이미지 설정
        binding.imgImg.setImageURI(imageUri)
    }
}