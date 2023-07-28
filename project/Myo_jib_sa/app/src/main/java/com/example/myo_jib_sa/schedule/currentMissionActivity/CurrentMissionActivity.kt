package com.example.myo_jib_sa.schedule.currentMissionActivity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ActivityCurrentMissionBinding
import com.example.myo_jib_sa.databinding.ActivityMainBinding
import com.example.myo_jib_sa.schedule.HistoryActivity
import com.example.myo_jib_sa.schedule.ScheduleFragment
import com.example.myo_jib_sa.schedule.adapter.CalendarAdapter
import com.example.myo_jib_sa.schedule.adapter.ScheduleAdaptar
import com.example.myo_jib_sa.schedule.api.scheduleHome.Mission
import com.example.myo_jib_sa.schedule.api.scheduleOfDay.ScheduleOfDayResult
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.CurrentMissionCurrentMissionAdapter
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.CurrentMissionData
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.CurrentMissionScheduleAdapter
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.ScheduleAdapterData

class CurrentMissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrentMissionBinding
    private lateinit var currentMissionAdapter : CurrentMissionCurrentMissionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCurrentMissionCurrentMissionAdapter()    //CurrentMissionCurrentMissionAdapter 연결
        setCurrentMissionScheduleAdapter()    //CurrentMissionScheduleAdapter 연결

        //currentMissionCurrentMissionRv item클릭 이벤트
        currentMissionCurrentMissionRvItemClickEvent()

        //뒤로가기 버튼 클릭
        binding.goBackBtn.setOnClickListener {
//            var scheduleIntent = Intent(this, ScheduleFragment::class.java)
//            startActivity(scheduleIntent)
//            if(!isFinishing) {
            finish()
        //}
        }


    }

    //CurrentMissionCurrentMissionAdapter 연결
    fun setCurrentMissionCurrentMissionAdapter(){
        var missionList = ArrayList<CurrentMissionData>()
        missionList.add(CurrentMissionData("헬스", "D+5", 10, R.drawable.ic_currentmission_exercise, 1))
        missionList.add(CurrentMissionData("헬스", "D+5", 10, R.drawable.ic_currentmission_exercise, 1))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))


        currentMissionAdapter = CurrentMissionCurrentMissionAdapter(missionList)
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

    //currentMissionCurrentMissionRv item클릭 이벤트
    fun currentMissionCurrentMissionRvItemClickEvent() {
        currentMissionAdapter.setItemClickListener(object : CurrentMissionCurrentMissionAdapter.OnItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(currentMissionData: CurrentMissionData) {
                // 클릭 시 이벤트 작성
                var bundle = Bundle()
                bundle.putLong("scheduleId", currentMissionData.missionId)
                Log.d("debug", "\"scheduleId\", ${currentMissionData.missionId}")
                val currentMissionDetailDialogFragment = CurrentMissionDetailDialogFragment()
                currentMissionDetailDialogFragment.arguments = bundle

                //scheduleDetailDialogItemClickEvent(scheduleDetailDialog)//scheduleDetailDialog Item클릭 이벤트 setting
                currentMissionDetailDialogFragment.show(supportFragmentManager, "currentMissionDetailDialogFragment")
            }
        })
    }
}
