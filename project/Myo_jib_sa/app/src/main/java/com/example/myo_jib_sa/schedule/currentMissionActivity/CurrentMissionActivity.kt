package com.example.myo_jib_sa.schedule.currentMissionActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ActivityCurrentMissionBinding
import com.example.myo_jib_sa.databinding.ActivityMainBinding
import com.example.myo_jib_sa.schedule.adapter.CalendarAdapter
import com.example.myo_jib_sa.schedule.api.scheduleHome.Mission
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.CurrentMissionCurrentMissionAdapter
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.CurrentMissionData
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.CurrentMissionScheduleAdapter
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.ScheduleAdapterData

class CurrentMissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrentMissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCurrentMissionCurrentMissionAdapter()    //CurrentMissionCurrentMissionAdapter 연결
        setCurrentMissionScheduleAdapter()    //CurrentMissionScheduleAdapter 연결



    }

    //CurrentMissionCurrentMissionAdapter 연결
    fun setCurrentMissionCurrentMissionAdapter(){
        var missionList = ArrayList<CurrentMissionData>()
        missionList.add(CurrentMissionData("헬스", "D+5", 10, R.drawable.ic_currentmission_exercise))
        missionList.add(CurrentMissionData("헬스", "D+5", 10, R.drawable.ic_currentmission_exercise))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art))


        val currentMissionAdapter = CurrentMissionCurrentMissionAdapter(missionList)
        binding.missionListRv.layoutManager = GridLayoutManager(this, 2)
        binding.missionListRv.adapter = currentMissionAdapter
    }

    //CurrentMissionScheduleAdapter 연결
    fun setCurrentMissionScheduleAdapter(){
        var scheduleList = ArrayList<ScheduleAdapterData>()
        scheduleList.add(ScheduleAdapterData("헬스 4일차", "2023.07.01"))
        scheduleList.add(ScheduleAdapterData("헬스 4일차", "2023.07.01"))
        scheduleList.add(ScheduleAdapterData("헬스 3일차", "2023.06.30"))
        scheduleList.add(ScheduleAdapterData("헬스 3일차", "2023.06.30"))
        scheduleList.add(ScheduleAdapterData("헬스 3일차", "2023.06.30"))
        scheduleList.add(ScheduleAdapterData("헬스 2일차", "2023.06.29"))
        scheduleList.add(ScheduleAdapterData("헬스 2일차", "2023.06.29"))
        scheduleList.add(ScheduleAdapterData("헬스 2일차", "2023.06.29"))
        scheduleList.add(ScheduleAdapterData("헬스 2일차", "2023.06.29"))
        scheduleList.add(ScheduleAdapterData("헬스 2일차", "2023.06.29"))


        val scheduleAdapter = CurrentMissionScheduleAdapter(scheduleList)
        binding.scheduleListRv.layoutManager = LinearLayoutManager(this)
        binding.scheduleListRv.adapter = scheduleAdapter
    }

}
