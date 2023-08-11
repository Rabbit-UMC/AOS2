package com.example.myo_jib_sa.schedule.currentMissionActivity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.BuildConfig
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ActivityCurrentMissionBinding
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.schedule.api.scheduleDetail.ScheduleDetailResponse
import com.example.myo_jib_sa.schedule.api.scheduleDetail.ScheduleDetailService
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.*
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMission.CurrentMissionResponse
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMission.CurrentMissionResult
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMission.CurrentMissionService
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionSchedule.CurrentMissionScheduleResponse
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionSchedule.CurrentMissionScheduleResult
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionSchedule.CurrentMissionScheduleService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CurrentMissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrentMissionBinding
    private lateinit var currentMissionAdapter : CurrentMissionCurrentMissionAdapter
    private var missionList = ArrayList<CurrentMissionResult>()
    private var scheduleList = ArrayList<CurrentMissionScheduleResult>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        currentMissionApi()//currentMission api연결

        setCurrentMissionCurrentMissionAdapter()    //CurrentMissionCurrentMissionAdapter 연결
        //setCurrentMissionScheduleAdapter(missionList[0].missionTitle)    //CurrentMissionScheduleAdapter 연결


        currentMissionCurrentMissionRvItemClickEvent()//currentMissionCurrentMissionRv item클릭 이벤트

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
//        missionList = ArrayList<CurrentMissionResult>()

//        missionList.add(CurrentMissionData("헬스1", "D+5", 10, R.drawable.ic_currentmission_exercise, 1))
//        missionList.add(CurrentMissionData("헬스2", "D+5", 10, R.drawable.ic_currentmission_exercise, 1))
//        missionList.add(CurrentMissionData("미션 제목3", "D+10", 10, R.drawable.ic_currentmission_art, 1))
//        missionList.add(CurrentMissionData("미션 제목4", "D+10", 10, R.drawable.ic_currentmission_art, 1))
//        missionList.add(CurrentMissionData("미션 제목5", "D+10", 10, R.drawable.ic_currentmission_art, 1))
//        missionList.add(CurrentMissionData("미션 제목6", "D+10", 10, R.drawable.ic_currentmission_art, 1))
//        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))
//        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))
//        missionList.add(CurrentMissionData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))


        currentMissionAdapter = CurrentMissionCurrentMissionAdapter(missionList, getDisplayWidthSize(), getDisplayHeightSize())
        binding.missionListRv.layoutManager = GridLayoutManager(this, 2)
        binding.missionListRv.adapter = currentMissionAdapter
    }

    //CurrentMissionScheduleAdapter 연결
    private fun setCurrentMissionScheduleAdapter(){//missionTitle:String
//        scheduleList = ArrayList<ScheduleAdapterData>()
//        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 4일차", "2023.07.01"))
//        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 4일차", "2023.07.01"))
//        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 3일차", "2023.06.30"))
//        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 3일차", "2023.06.30"))
//        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 3일차", "2023.06.30"))
//        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29"))
//        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29"))
//        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29"))
//        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29"))
//        scheduleList.add(ScheduleAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29"))


        val scheduleAdapter = CurrentMissionScheduleAdapter(scheduleList, getDisplayHeightSize())
        binding.scheduleListRv.layoutManager = LinearLayoutManager(this)
        binding.scheduleListRv.adapter = scheduleAdapter
    }



    //currentMissionCurrentMissionRv item클릭 이벤트
    private fun currentMissionCurrentMissionRvItemClickEvent() {
        currentMissionAdapter.setItemClickListener(object : CurrentMissionCurrentMissionAdapter.OnItemClickListener {

            var delay:Long = 0//클릭 간격

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(currentMissionData: CurrentMissionResult) {
                if (System.currentTimeMillis() > delay) {
                    //한번 클릭 동작
                    currentMissionScheduleApi(currentMissionData.missionId)
                    //setCurrentMissionScheduleAdapter(currentMissionData.missionId)


                    delay = System.currentTimeMillis()+200 //클릭 간격
                    return
                }
                if (System.currentTimeMillis() <= delay) {
                    //두번 클릭 동작
                    // 미션 상세 다이어로그 띄우기
                    var bundle = Bundle()
                    bundle.putLong("missionId", currentMissionData.missionId)
                    Log.d("debug", "\"missionId\", ${currentMissionData.missionId}")
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


    //currentMission api연결
    private fun currentMissionApi() {
        val token: String = BuildConfig.API_TOKEN
        Log.d("debug", "token = "+token+"l");

        missionList = ArrayList<CurrentMissionResult>()

        val service = RetrofitClient.getInstance().create(CurrentMissionService::class.java)
        val listCall = service.currentMission(token)

        listCall.enqueue(object : Callback<CurrentMissionResponse> {
            override fun onResponse(
                call: Call<CurrentMissionResponse>,
                response: Response<CurrentMissionResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("debug", "retrofit: "+response.body().toString());
                    val result = response.body()!!.result

                    for(i in 0 until result.size) {
                        missionList.add(result[i])
                    }
                    setCurrentMissionCurrentMissionAdapter()//CurrentMission rv 연결
                    currentMissionScheduleApi(missionList[0].missionId)//하위 스케쥴 데이터 추가+schedule rv연결+clickevent
                    currentMissionCurrentMissionRvItemClickEvent()

                } else {
                    Log.e("retrofit", "onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "onResponse: Error Body $errorBody")
                }
            }
            override fun onFailure(call: Call<CurrentMissionResponse>, t: Throwable) {
                Log.e("retrofit", "onFailure: ${t.message}")
            }
        })
    }

    //currentMissionSchedule api연결
    private fun currentMissionScheduleApi(missionId:Long) {
        val token: String = BuildConfig.API_TOKEN
        Log.d("debug", "token = "+token+"l");

        scheduleList = ArrayList<CurrentMissionScheduleResult>()

        val service = RetrofitClient.getInstance().create(CurrentMissionScheduleService::class.java)
        val listCall = service.currentMissionSchedule(token, missionId)

        listCall.enqueue(object : Callback<CurrentMissionScheduleResponse> {
            override fun onResponse(
                call: Call<CurrentMissionScheduleResponse>,
                response: Response<CurrentMissionScheduleResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("debug", "retrofit: "+response.body().toString());
                    val result = response.body()!!.result

                    for(i in 0 until result.size) {
                        scheduleList.add(result[i])
                    }
                    setCurrentMissionScheduleAdapter()//CurrentMissionSchedule rv 연결
                    currentMissionCurrentMissionRvItemClickEvent()

                } else {
                    Log.e("retrofit", "onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "onResponse: Error Body $errorBody")
                }
            }
            override fun onFailure(call: Call<CurrentMissionScheduleResponse>, t: Throwable) {
                Log.e("retrofit", "onFailure: ${t.message}")
            }
        })
    }

    //화면 width
    private fun getDisplayWidthSize(): Int {

        val display = this.applicationContext?.resources?.displayMetrics

        return display?.widthPixels!!
    }
    //화면 height
    private fun getDisplayHeightSize(): Int {

        //statusbarHeight
        var statusbarHeight = 0
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusbarHeight = resources.getDimensionPixelSize(resourceId)
        }

        //navigationbarHeight
        val resourceId2 = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        var navigationbarHeight = 0
        if (resourceId2 > 0) {
            navigationbarHeight = resources.getDimensionPixelSize(resourceId)
        }

        val display = this.applicationContext?.resources?.displayMetrics

        return display?.heightPixels!! + statusbarHeight + navigationbarHeight

    }
}
