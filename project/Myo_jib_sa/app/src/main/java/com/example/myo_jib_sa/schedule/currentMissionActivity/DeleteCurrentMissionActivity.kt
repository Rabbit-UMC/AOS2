package com.example.myo_jib_sa.schedule.currentMissionActivity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ActivityDeleteCurrentMissionBinding
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.*

class DeleteCurrentMissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteCurrentMissionBinding
    private var position = 0 //CurrentMissionActivity에서 받아온 longclick한 아이템 포지션
    private var missionList = ArrayList<CurrentMissionDeleteData>() //CurrentMissionCurrentMissionDeleteAdapter의 데이터 리스트
    private lateinit var currentMissionDeleteAdapter : CurrentMissionCurrentMissionDeleteAdapter
    private var scheduleList = ArrayList<ScheduleDeleteAdapterData>() //CurrentMissionScheduleDeleteAdapter의 데이터 리스트
    private lateinit var scheduleDeleteAdapter : CurrentMissionScheduleDeleteAdapter
    private var isAllSeleted = false //전체선택
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteCurrentMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCurrentMissionCurrentMissionDeleteAdapter()        //CurrentMissionCurrentMissionDeleteAdapter 연결
        currentMissionCurrentMissionDeleteRvItemClickEvent()        //currentMissionCurrentMissionDeleteRv item클릭 이벤트


        setActivity()//화면 셋팅
        setButton()//버튼 셋팅

        setCurrentMissionScheduleDeleteAdapter(missionList[position].missionTitle)        //CurrentMissionScheduleDeleteAdapter 연결

    }
    //화면 셋팅
    private fun setActivity(){
        if(intent.hasExtra("position")){
            position = intent.getIntExtra("position", 0)
        }
            missionList[position].selected = true
        currentMissionDeleteAdapter.notifyItemChanged(position); //해당 포지션 아이템만 업데이트
    }
    //CurrentMissionCurrentMissionAdapter 연결
    private fun setCurrentMissionCurrentMissionDeleteAdapter(){

        missionList.add(CurrentMissionDeleteData("헬스1", "D+5", 10, R.drawable.ic_currentmission_exercise, 1))
        missionList.add(CurrentMissionDeleteData("헬스2", "D+5", 10, R.drawable.ic_currentmission_exercise, 1))
        missionList.add(CurrentMissionDeleteData("미션 제목3", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionDeleteData("미션 제목4", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionDeleteData("미션 제목5", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionDeleteData("미션 제목6", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionDeleteData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionDeleteData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))
        missionList.add(CurrentMissionDeleteData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))


        currentMissionDeleteAdapter = CurrentMissionCurrentMissionDeleteAdapter(missionList)
        binding.missionListRv.layoutManager = GridLayoutManager(this, 2)
        binding.missionListRv.adapter = currentMissionDeleteAdapter
    }

    //CurrentMissionScheduleAdapter 연결
    private fun setCurrentMissionScheduleDeleteAdapter(missionTitle:String){
        //scheduleList.clear()
        scheduleList = ArrayList<ScheduleDeleteAdapterData>()
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 4일차", "2023.07.01", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 4일차", "2023.07.01", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 3일차", "2023.06.30", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 3일차", "2023.06.30", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 3일차", "2023.06.30", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29", 1))
        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29", 1))


        scheduleDeleteAdapter = CurrentMissionScheduleDeleteAdapter(scheduleList)
        binding.scheduleListRv.layoutManager = LinearLayoutManager(this)
        binding.scheduleListRv.adapter = scheduleDeleteAdapter
    }


    //currentMissionCurrentMissionRv item클릭 이벤트
    private fun currentMissionCurrentMissionDeleteRvItemClickEvent() {
        currentMissionDeleteAdapter.setItemClickListener(object : CurrentMissionCurrentMissionDeleteAdapter.OnItemClickListener {

            var delay:Long = 0//클릭 간격

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(currentMissionData: CurrentMissionDeleteData) {
                if (System.currentTimeMillis() > delay) {
                    //한번 클릭 동작
                    setCurrentMissionScheduleDeleteAdapter(currentMissionData.missionTitle)


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
        })
    }

    //버튼 셋팅
    private fun setButton(){
        //전체 선택 버튼
        binding.allSeleteV.setOnClickListener{
            if(isAllSeleted){//전체 선택 해제
                for(i in 0 until scheduleList.size) {
                    scheduleList[i].selected = false
                }
                scheduleDeleteAdapter.notifyDataSetChanged();//rv update

                binding.allSeleteV.setBackgroundColor(Color.parseColor("#D9D9D9")) //회색으로 버튼 색 변경
                isAllSeleted = false
            }else{//전체 선택
                for(i in 0 until scheduleList.size) {
                    scheduleList[i].selected = true
                }
                scheduleDeleteAdapter.notifyDataSetChanged();//rv update

                binding.allSeleteV.setBackgroundColor(Color.parseColor("#C7DAFA")) //파란색으로 버튼 색 변경
                isAllSeleted = true
            }
        }

        //뒤로가기 버튼 클릭
        binding.goBackBtn.setOnClickListener {
//            var scheduleIntent = Intent(this, ScheduleFragment::class.java)
//            startActivity(scheduleIntent)
//            if(!isFinishing) {
            finish()
            //}
        }
    }

}
