package com.example.myo_jib_sa.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.myo_jib_sa.databinding.ActivityEditMypageBinding
import com.kakao.sdk.user.UserApiClient

class EditMypageActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEditMypageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("kakao", "사용자 정보 요청 실패", error)
            }
            else if (user != null) {
                var kakaoEmail=user.kakaoAccount?.email
                Log.d("kakaoM",kakaoEmail.toString())
                binding.txtMyPageEmail.text=kakaoEmail
            }
        }
    }
}