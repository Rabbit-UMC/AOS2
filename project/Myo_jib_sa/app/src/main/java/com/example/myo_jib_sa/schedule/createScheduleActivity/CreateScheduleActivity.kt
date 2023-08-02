package com.example.myo_jib_sa.schedule.createScheduleActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ActivityCreateScheduleBinding
import com.example.myo_jib_sa.databinding.ActivityCurrentMissionBinding

class CreateScheduleActivity : AppCompatActivity() {
    lateinit var binding : ActivityCreateScheduleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}