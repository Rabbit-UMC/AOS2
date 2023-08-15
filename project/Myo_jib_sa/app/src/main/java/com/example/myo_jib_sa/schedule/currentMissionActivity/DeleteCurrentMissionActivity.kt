package com.example.myo_jib_sa.schedule.currentMissionActivity

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.BuildConfig
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ActivityDeleteCurrentMissionBinding
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.schedule.api.scheduleDelete.ScheduleDeleteResponse
import com.example.myo_jib_sa.schedule.api.scheduleDelete.ScheduleDeleteService
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.*
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMission.CurrentMissionResponse
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMission.CurrentMissionResult
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMission.CurrentMissionService
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionDelete.CurrentMissionDeleteResponse
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionDelete.CurrentMissionDeleteService
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionSchedule.CurrentMissionScheduleResponse
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionSchedule.CurrentMissionScheduleResult
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionSchedule.CurrentMissionScheduleService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread


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


        currentMissionApi()//currentMission api연결
        setCurrentMissionCurrentMissionDeleteAdapter()        //CurrentMissionCurrentMissionDeleteAdapter 연결
        currentMissionCurrentMissionDeleteRvItemClickEvent()        //currentMissionCurrentMissionDeleteRv item클릭 이벤트


//        setActivity()//화면 셋팅
        setButton()//버튼 셋팅

        //setCurrentMissionScheduleDeleteAdapter(missionList[position].missionTitle)        //CurrentMissionScheduleDeleteAdapter 연결


        // 2초동안 액션 없으면 작동|핸들러에 메세지 보내기
        handlerDelayStart(MESSAGE_WHAT_TIMER, LIMIT_TIME)

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
//
//        missionList.add(CurrentMissionDeleteData("헬스1", "D+5", 10, R.drawable.ic_currentmission_exercise, 1))
//        missionList.add(CurrentMissionDeleteData("헬스2", "D+5", 10, R.drawable.ic_currentmission_exercise, 1))
//        missionList.add(CurrentMissionDeleteData("미션 제목3", "D+10", 10, R.drawable.ic_currentmission_art, 1))
//        missionList.add(CurrentMissionDeleteData("미션 제목4", "D+10", 10, R.drawable.ic_currentmission_art, 1))
//        missionList.add(CurrentMissionDeleteData("미션 제목5", "D+10", 10, R.drawable.ic_currentmission_art, 1))
//        missionList.add(CurrentMissionDeleteData("미션 제목6", "D+10", 10, R.drawable.ic_currentmission_art, 1))
//        missionList.add(CurrentMissionDeleteData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))
//        missionList.add(CurrentMissionDeleteData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))
//        missionList.add(CurrentMissionDeleteData("미션 제목", "D+10", 10, R.drawable.ic_currentmission_art, 1))


        currentMissionDeleteAdapter = CurrentMissionCurrentMissionDeleteAdapter(missionList, getDisplayWidthSize())
        binding.missionListRv.layoutManager = GridLayoutManager(this, 2)
        binding.missionListRv.adapter = currentMissionDeleteAdapter
    }

    //CurrentMissionScheduleAdapter 연결
    private fun setCurrentMissionScheduleDeleteAdapter(){
        //scheduleList.clear()
//        scheduleList = ArrayList<ScheduleDeleteAdapterData>()
//        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 4일차", "2023.07.01", 1))
//        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 4일차", "2023.07.01", 1))
//        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 3일차", "2023.06.30", 1))
//        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 3일차", "2023.06.30", 1))
//        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 3일차", "2023.06.30", 1))
//        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29", 1))
//        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29", 1))
//        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29", 1))
//        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29", 1))
//        scheduleList.add(ScheduleDeleteAdapterData("${missionTitle}: 헬스 2일차", "2023.06.29", 1))


        scheduleDeleteAdapter = CurrentMissionScheduleDeleteAdapter(scheduleList, getDisplayHeightSize())
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
                    //setCurrentMissionScheduleDeleteAdapter(currentMissionData.currentMissionResult.missionTitle)
                    currentMissionScheduleApi(currentMissionData.currentMissionResult.missionId)

                    delay = System.currentTimeMillis()+200
                    return
                }
                if (System.currentTimeMillis() <= delay) {
                    //두번 클릭 동작
                    // 미션 상세 다이어로그 띄우기
                    var bundle = Bundle()
                    bundle.putLong("missionId", currentMissionData.currentMissionResult.missionId)
                    Log.d("debug", "\"scheduleId\", ${currentMissionData.currentMissionResult.missionId}")
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


        //삭제 버튼 클릭
        binding.deleteFbtn.setOnClickListener {
            Log.d("debug_delete", "delete mission: ${currentMissionDeleteAdapter.getSelectedItemMissionId()}")
            Log.d("debug_delete", "delete schedule: ${scheduleDeleteAdapter.getSelectedItemScheduleId()}")
            currentMissionDeleteApi(currentMissionDeleteAdapter.getSelectedItemMissionId())//미션삭제api
            scheduleDeleteApi(scheduleDeleteAdapter.getSelectedItemScheduleId())//스케줄 삭제 api
        }

    }

    //currentMission api연결
    private fun currentMissionApi() {
        val token: String = BuildConfig.API_TOKEN
        Log.d("debug", "token = "+token+"l");

        missionList = ArrayList<CurrentMissionDeleteData>()

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

                    for(element in result) {
                        missionList.add(CurrentMissionDeleteData(element))
                    }
                    setCurrentMissionCurrentMissionDeleteAdapter()//CurrentMissionDelete rv 연결
                    setActivity()//화면 셋팅
                    currentMissionScheduleApi(missionList[position].currentMissionResult.missionId)//하위 스케쥴 데이터 추가+schedule rv연결+clickevent
                    currentMissionCurrentMissionDeleteRvItemClickEvent()        //currentMissionCurrentMissionDeleteRv item클릭 이벤트


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

        scheduleList = ArrayList<ScheduleDeleteAdapterData>()

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
                        scheduleList.add(ScheduleDeleteAdapterData(result[i]))
                    }
                    setCurrentMissionScheduleDeleteAdapter()//CurrentMissionSchedule rv 연결
                    currentMissionCurrentMissionDeleteRvItemClickEvent()

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

    //currentMission api연결
    private fun currentMissionDeleteApi(deleteMissionIdList:MutableList<Long>) {
        val token: String = BuildConfig.API_TOKEN
        Log.d("debug", "token = "+token+"l");


        val service = RetrofitClient.getInstance().create(CurrentMissionDeleteService::class.java)
        val url ="app/mission/my-missions/${deleteMissionIdList.joinToString(",")}"
        Log.d("debug", "$url");
        val listCall = service.currentMissionDelete(token, url)

        listCall.enqueue(object : Callback<CurrentMissionDeleteResponse> {
            override fun onResponse(
                call: Call<CurrentMissionDeleteResponse>,
                response: Response<CurrentMissionDeleteResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("debug", "retrofit: "+response.body().toString());
                    val result = response.body()!!.result


                } else {
                    Log.e("retrofit", "onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "onResponse: Error Body $errorBody")
                }
            }
            override fun onFailure(call: Call<CurrentMissionDeleteResponse>, t: Throwable) {
                Log.e("retrofit", "onFailure: ${t.message}")
            }
        })
    }

    //scheduleDelete api연결: 일정삭제
    private fun scheduleDeleteApi(deleteScheduleIdList:MutableList<Long>) {
        val token : String = BuildConfig.API_TOKEN
//        Log.d("retrofit", "token = "+token+"l");


        val service = RetrofitClient.getInstance().create(ScheduleDeleteService::class.java)
        val url ="app/schedule/${deleteScheduleIdList.joinToString(", ")}"
        Log.d("debug", "$url");
        val listCall = service.scheduleDeleteModifyVer(token, url)

        listCall.enqueue(object : Callback<ScheduleDeleteResponse> {
            override fun onResponse(
                call: Call<ScheduleDeleteResponse>,
                response: Response<ScheduleDeleteResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", response.body().toString());

                    //scheduleAdaptar.removeTask(position)
                }else {
                    Log.e("retrofit", "onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "onResponse: Error Body $errorBody")
                }}
            override fun onFailure(call: Call<ScheduleDeleteResponse>, t: Throwable) {
                Log.e("retrofit", "onFailure: ${t.message}")
            }
        })
    }

    //화면 width구하기
    fun getDisplayWidthSize(): Int {

        val display = this.applicationContext?.resources?.displayMetrics

        return display?.widthPixels!!
    }
    //화면 height구하기
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



    //2초동안 동작 없으면 삭제 버튼 보여주기==============================================================
    private var MESSAGE_WHAT_TIMER = 1
    private var LIMIT_TIME = 2000 //2초
    private var isButtonVisible = false //버튼 보이는지

    //2초동안 동작 없으면
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val userAction = event.action
        when (userAction) {
            MotionEvent.ACTION_DOWN -> {
                if(!isButtonVisible) {
                    Log.d("handler_debug", "scroll action")
                    handlerDelayStop(MESSAGE_WHAT_TIMER)
                    handlerDelayStart(MESSAGE_WHAT_TIMER, LIMIT_TIME)
                }
                else {
                    if (isInsideButton(event)) {
                        showButton()
                    } else {
                        // 터치된 위치에 버튼이 없을 경우 버튼을 숨김
                        hideButton()
                        handlerDelayStop(MESSAGE_WHAT_TIMER)
                        handlerDelayStart(MESSAGE_WHAT_TIMER, LIMIT_TIME)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                // 사용자가 화면에 상호 작용함, 비활동 핸들러를 리셋
//                    handler.removeCallbacks(showButtonRunnable)
//                    handler.postDelayed(showButtonRunnable, inactivityTimeout)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 사용자가 터치를 놓거나 취소함, 비활동 핸들러를 리셋
//                    handler.removeCallbacks(showButtonRunnable)
//                    handler.postDelayed(showButtonRunnable, inactivityTimeout)
            }
        }
        return super.dispatchTouchEvent(event)
    }

    //핸들러
    private val handler = @SuppressLint("HandlerLeak")
    object: Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            when(msg.what){
                MESSAGE_WHAT_TIMER -> {
                    Log.d("handler_debug", " : MESSAGE_WHAT_TIMER ")
                    showButton()
                }
            }
        }
    }


    //핸들러 stop
    private fun handlerDelayStop(what: Int) {
        Log.d("handler_debug", " : handlerDelayStop ")
        handler.removeMessages(what)

    }
    //핸들러에 time후에 메세지 보냄
    private fun handlerDelayStart(what: Int, time: Int) {
        Log.d("handler_debug", " : handlerDelayStart ")
        handler.sendEmptyMessageDelayed(what, time.toLong())
    }

    // 버튼을 보이게 함
    private fun showButton() {
        if (!isButtonVisible) {
            binding.deleteFbtn.visibility = View.VISIBLE
            isButtonVisible = true
        }
    }
    // 버튼을 숨김
    private fun hideButton() {
        if (isButtonVisible) {
            binding.deleteFbtn.visibility = View.GONE
            isButtonVisible = false
        }
    }

    // 버튼 영역을 터치했는지 확인
    private fun isInsideButton(event: MotionEvent): Boolean {
        val buttonRect = Rect()
        binding.deleteFbtn.getGlobalVisibleRect(buttonRect)
        return buttonRect.contains(event.rawX.toInt(), event.rawY.toInt())
    }
    //2초동안 동작 없으면 삭제 버튼 보여주기========================================================여기까지




}
