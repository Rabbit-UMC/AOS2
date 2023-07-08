package com.example.myo_jib_sa.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myo_jib_sa.databinding.ActivityEditMypageBinding

class EditMypageActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEditMypageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}