package com.example.myo_jib_sa.schedule.currentMissionActivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.databinding.ActivityCurrentMissionBinding
import com.example.myo_jib_sa.schedule.api.RetrofitClient
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

        //currentMissionApi()//currentMission api연결

//        setCurrentMissionScheduleAdapter()    //CurrentMissionScheduleAdapter 초기화
//        setCurrentMissionCurrentMissionAdapter()    //CurrentMissionCurrentMissionAdapter 연결
//
//
//        currentMissionCurrentMissionRvItemClickEvent()//currentMissionCurrentMissionRv item클릭 이벤트

        ///currentMissionApi()//currentMission api연결

        //뒤로가기 버튼 클릭
        binding.goBackBtn.setOnClickListener {
            finish()
        }

        setBtn()

    }
    override fun onResume() {
        super.onResume()
        currentMissionApi()//currentMission api연결



    }








    //CurrentMissionCurrentMissionAdapter 연결
    private fun setCurrentMissionCurrentMissionAdapter(){

        currentMissionAdapter = CurrentMissionCurrentMissionAdapter(missionList, getDisplayWidthSize(), getDisplayHeightSize())
        binding.missionListRv.layoutManager = LinearLayoutManager(this)
        binding.missionListRv.adapter = currentMissionAdapter
    }

    //CurrentMissionScheduleAdapter 연결
    private fun setCurrentMissionScheduleAdapter() {//missionTitle:String

        val scheduleAdapter = CurrentMissionScheduleAdapter(scheduleList, getDisplayHeightSize())
        binding.scheduleListRv.layoutManager = LinearLayoutManager(this)
        binding.scheduleListRv.adapter = scheduleAdapter
    }



    //currentMissionCurrentMissionRv item클릭 이벤트
    private fun currentMissionCurrentMissionRvItemClickEvent() {
        var doubleClickFlag = 0
        val CLICK_DELAY: Long = 250
        currentMissionAdapter.setItemClickListener(object : CurrentMissionCurrentMissionAdapter.OnItemClickListener {
            ////double click ver 1
            //var delay:Long = 0//클릭 간격

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(currentMissionData: CurrentMissionResult) {
                doubleClickFlag++;
                var handler = Handler(Looper.getMainLooper());
                val clickRunnable = Runnable {
                    doubleClickFlag = 0
                    // 클릭 이벤트 처리
                    currentMissionScheduleApi(currentMissionData.missionId)
                }
                if (doubleClickFlag == 1) {
                    handler.postDelayed(clickRunnable, CLICK_DELAY)
                } else if (doubleClickFlag == 2) {
                    doubleClickFlag = 0
                    // 더블클릭 이벤트 처리
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
            //작성한 일지 보기 text view 클릭 - [하위 스케줄 보기]
            override fun onScheduleClick(currentMissionData: CurrentMissionResult) {
                binding.missionScheduleListLayout.visibility = View.VISIBLE
                currentMissionScheduleApi(currentMissionData.missionId)
                binding.scheduleDeleteBtn.visibility = View.VISIBLE
            }
        })
    }


    //currentMission api연결
    private fun currentMissionApi() {
        // SharedPreferences 객체 가져오기
        val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        // JWT 값 가져오기
        val token = sharedPreferences.getString("jwt", null)

        //val token: String = BuildConfig.API_TOKEN
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
                    Log.d("retrofit", response.body().toString());
                    val result = response.body()!!.result

                    for(i in 0 until result.size) {
                        missionList.add(result[i])
                    }
                    if(result.isNotEmpty()) {
                        setCurrentMissionCurrentMissionAdapter()//CurrentMission rv 연결
                        currentMissionScheduleApi(missionList[0].missionId)//하위 스케쥴 데이터 추가+schedule rv연결+clickevent
                        currentMissionCurrentMissionRvItemClickEvent()
                    }

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
        // SharedPreferences 객체 가져오기
        val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        // JWT 값 가져오기
        val token = sharedPreferences.getString("jwt", null)

        //val token: String = BuildConfig.API_TOKEN
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
                    Log.d("retrofit", response.body().toString());
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




    private fun setBtn(){
        //뒤로가기 버튼 클릭
        binding.goBackBtn.setOnClickListener {
            finish()
        }

        //------------------------하위일정 레이아웃---------------------
        binding.scheduleDeleteBtn.setOnClickListener{
            binding.selectAllLayout.visibility = View.VISIBLE
            binding.deleteTv.visibility = View.VISIBLE
            binding.scheduleDeleteBtn.visibility = View.GONE
        }

    }

}
