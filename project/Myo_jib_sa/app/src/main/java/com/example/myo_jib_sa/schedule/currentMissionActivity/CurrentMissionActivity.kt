package com.example.myo_jib_sa.schedule.currentMissionActivity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ActivityCurrentMissionBinding
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.CurrentMissionCurrentMissionAdapter
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.CurrentMissionData
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.CurrentMissionScheduleAdapter
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.ScheduleAdapterData


class CurrentMissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrentMissionBinding
    private lateinit var currentMissionAdapter : CurrentMissionCurrentMissionAdapter
    private var missionList = ArrayList<CurrentMissionData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCurrentMissionCurrentMissionAdapter()    //CurrentMissionCurrentMissionAdapter 연결
        setCurrentMissionScheduleAdapter(missionList[0].missionTitle)    //CurrentMissionScheduleAdapter 연결

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
    private fun setCurrentMissionCurrentMissionAdapter(){
        missionList = ArrayList<CurrentMissionData>()

        missionList.add(CurrentMissionData("헬스1", "D+5", 10, R.drawable.ic_currentmission_exercise, 1))
        missionList.add(CurrentMissionData("헬스2", "D+5", 10, R.drawable.ic_currentmission_exercise, 1))
        missionList.add(CurrentMissionData("미션 제목3", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionData("미션 제목4", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionData("미션 제목5", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionData("미션 제목6", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))


        currentMissionAdapter = CurrentMissionCurrentMissionAdapter(missionList)
        binding.missionListRv.layoutManager = GridLayoutManager(this, 2)
        binding.missionListRv.adapter = currentMissionAdapter
    }

    //CurrentMissionScheduleAdapter 연결
    private fun setCurrentMissionScheduleAdapter(missionTitle:String){
        var scheduleList = ArrayList<ScheduleAdapterData>()
        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 4일차", "2023.07.01"))
        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 4일차", "2023.07.01"))
        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 3일차", "2023.06.30"))
        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 3일차", "2023.06.30"))
        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 3일차", "2023.06.30"))
        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29"))
        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29"))
        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29"))
        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29"))
        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29"))


        val scheduleAdapter = CurrentMissionScheduleAdapter(scheduleList)
        binding.scheduleListRv.layoutManager = LinearLayoutManager(this)
        binding.scheduleListRv.adapter = scheduleAdapter
    }



    //currentMissionCurrentMissionRv item클릭 이벤트
    private fun currentMissionCurrentMissionRvItemClickEvent() {
        currentMissionAdapter.setItemClickListener(object : CurrentMissionCurrentMissionAdapter.OnItemClickListener {

            var delay:Long = 0//클릭 간격

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(currentMissionData: CurrentMissionData) {
                if (System.currentTimeMillis() > delay) {
                    //한번 클릭 동작
                    setCurrentMissionScheduleAdapter(currentMissionData.missionTitle)


                    delay = System.currentTimeMillis()+200
                    return
                }
                if (System.currentTimeMillis() <= delay) {
                    //두번 클릭 동작
                    // 미션 상세 다이어로그 띄우기
                    var bundle = Bundle()
                    bundle.putLong("scheduleId", currentMissionData.missionId)
                    Log.d("debug", "\"scheduleId\", ${currentMissionData.missionId}")
                    val currentMissionDetailDialogFragment = CurrentMissionDetailDialogFragment()
                    currentMissionDetailDialogFragment.arguments = bundle

                    //scheduleDetailDialogItemClickEvent(scheduleDetailDialog)//scheduleDetailDialog Item클릭 이벤트 setting
                    currentMissionDetailDialogFragment.show(supportFragmentManager, "currentMissionDetailDialogFragment")
                }
            }

            override fun onLongClick(position:Int){
                var deleteIntent = Intent(this@CurrentMissionActivity, DeleteCurrentMissionActivity::class.java)
                deleteIntent.putExtra("position",position);
                startActivity(deleteIntent)

            }
        })
    }
}
