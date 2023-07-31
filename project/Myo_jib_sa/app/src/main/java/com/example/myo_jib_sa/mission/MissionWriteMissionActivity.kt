package com.example.myo_jib_sa.mission

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myo_jib_sa.databinding.ActivityMissionWriteMissionBinding

class MissionWriteMissionActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMissionWriteMissionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionWriteMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}