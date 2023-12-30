package com.example.myo_jib_sa.Schedule.currentMissionActivity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.databinding.ActivityCurrentMissionBinding
import com.example.myo_jib_sa.Schedule.API.RetrofitClient
import com.example.myo_jib_sa.Schedule.currentMissionActivity.adapter.*
import com.example.myo_jib_sa.Schedule.currentMissionActivity.api.currentMissionSchedule.CurrentMissionScheduleResponse
import com.example.myo_jib_sa.Schedule.currentMissionActivity.api.currentMissionSchedule.CurrentMissionScheduleResult
import com.example.myo_jib_sa.Schedule.currentMissionActivity.api.currentMissionSchedule.CurrentMissionScheduleService
import com.example.myo_jib_sa.Schedule.API.currentMission.CurrentMissionResponse
import com.example.myo_jib_sa.Schedule.API.currentMission.CurrentMissionResult
import com.example.myo_jib_sa.Schedule.API.currentMission.CurrentMissionService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CurrentMissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrentMissionBinding
    private lateinit var currentMissionAdapter : CurrentMissionAdapter
    private lateinit var scheduleAdapter: CurrentMissionScheduleAdapter
    private var missionList = ArrayList<CurrentMissionResult>()
    private var scheduleList = ArrayList<CurrentMissionScheduleResult>()
    private var scheduleAdapterList = ArrayList<ScheduleAdapterData>()
    private var seeScheduleDeleteBtn = true;
    private var deleteScheduleIdList : MutableSet<Long> = mutableSetOf()//삭제할 스케줄 아이디

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        setCurrentMissionScheduleAdapter()    //CurrentMissionScheduleAdapter 초기화
//

        //todo:삭제
        missionList.add(CurrentMissionResult(1, "미션1", 10, 1,"img", "60"))
        missionList.add(CurrentMissionResult(2, "미션2", 10, 1,"img", "60"))
        missionList.add(CurrentMissionResult(3, "미션3", 10, 1,"img", "60"))
        missionList.add(CurrentMissionResult(4, "미션4", 10, 1,"img", "60"))
        missionList.add(CurrentMissionResult(5, "미션5", 10, 1,"img", "60"))

        setCurrentMissionAdapter()//CurrentMission rv 연결
        currentMissionScheduleApi(missionList[0].missionId)//하위 스케쥴 데이터 추가+schedule rv연결+clickevent
        currentMissionRvItemClickEvent()


        scheduleAdapterList.add(ScheduleAdapterData(CurrentMissionScheduleResult(1,"일정1", "2023-12-23"), false))
        scheduleAdapterList.add(ScheduleAdapterData(CurrentMissionScheduleResult(2,"일정2", "2023-12-23"), false))
        scheduleAdapterList.add(ScheduleAdapterData(CurrentMissionScheduleResult(3,"일정3", "2023-12-23"), false))
        scheduleAdapterList.add(ScheduleAdapterData(CurrentMissionScheduleResult(4,"일정4", "2023-12-23"), false))
        scheduleAdapterList.add(ScheduleAdapterData(CurrentMissionScheduleResult(5,"일정5", "2023-12-23"), false))
        scheduleAdapterList.add(ScheduleAdapterData(CurrentMissionScheduleResult(6,"일정6", "2023-12-23"), false))
        scheduleAdapterList.add(ScheduleAdapterData(CurrentMissionScheduleResult(7,"일정7", "2023-12-23"), false))
        scheduleAdapterList.add(ScheduleAdapterData(CurrentMissionScheduleResult(8,"일정8", "2023-12-23"), false))

        setCurrentMissionScheduleAdapter()//CurrentMissionSchedule rv 연결
        currentMissionScheduleRvItemClickEvent() //CurrentMissionSchedule 클릭이벤트 연결
        currentMissionRvItemClickEvent()
        //todo:여기까지


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


    //CurrentMissionAdapter 연결
    private fun setCurrentMissionAdapter(){

        currentMissionAdapter = CurrentMissionAdapter(missionList, getDisplayWidthSize(), getDisplayHeightSize())
        binding.missionListRv.layoutManager = LinearLayoutManager(this)
        binding.missionListRv.adapter = currentMissionAdapter
    }

    //CurrentMissionScheduleAdapter 연결
    private fun setCurrentMissionScheduleAdapter() {//missionTitle:String

        scheduleAdapter = CurrentMissionScheduleAdapter(scheduleAdapterList, getDisplayHeightSize())
        binding.scheduleListRv.layoutManager = LinearLayoutManager(this)
        binding.scheduleListRv.adapter = scheduleAdapter
    }



    //currentMissionCurrentMissionRv item클릭 이벤트
    private fun currentMissionRvItemClickEvent() {
        var doubleClickFlag = 0
        val CLICK_DELAY: Long = 250
        currentMissionAdapter.setItemClickListener(object : CurrentMissionAdapter.OnItemClickListener {
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

    private fun currentMissionScheduleRvItemClickEvent() {
        scheduleAdapter.setItemClickListener(object : CurrentMissionScheduleAdapter.OnItemClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(scheduleItem: ConstraintLayout, isClicked: Boolean) {
                if(!seeScheduleDeleteBtn) {
                    if(!isClicked)
                        scheduleItem.setBackgroundColor(Color.parseColor("#1A234BD9"))
                    else
                        scheduleItem.setBackgroundColor(Color.parseColor("#FFFFFF"))
                }
            }
        })
    }


    //currentMission api연결
    private fun currentMissionApi() {
        val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", null)

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
                        setCurrentMissionAdapter()//CurrentMission rv 연결
                        currentMissionScheduleApi(missionList[0].missionId)//하위 스케쥴 데이터 추가+schedule rv연결+clickevent
                        currentMissionRvItemClickEvent()
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
                        scheduleAdapterList.add(ScheduleAdapterData(scheduleList[i], false))
                    }
                    setCurrentMissionScheduleAdapter()//CurrentMissionSchedule rv 연결
                    currentMissionRvItemClickEvent()

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
            if(seeScheduleDeleteBtn){
                seeScheduleDeleteBtn = false;
                binding.selectAllLayout.visibility = View.VISIBLE
                binding.deleteTv.visibility = View.VISIBLE
                binding.scheduleDeleteBtn.visibility = View.GONE
            }
            else{
                seeScheduleDeleteBtn = true;
                binding.selectAllLayout.visibility = View.GONE
                binding.deleteTv.visibility = View.GONE
                binding.scheduleDeleteBtn.visibility = View.VISIBLE
            }
        }

        binding.deleteTv.isClickable = false
        binding.deleteTv.setOnClickListener {
            var deleteDialog = DeleteDialogFragment()
            deleteDialogItemClickEvent(deleteDialog)
            deleteDialog.show(this.supportFragmentManager, "ScheduleDetailDialog")
        }

    }

    //scheduleDetailDialog Item클릭 이벤트
    fun deleteDialogItemClickEvent(dialog: DeleteDialogFragment){
        dialog.setButtonClickListener(object: DeleteDialogFragment.OnButtonClickListener{

            override fun onClickYesBtn() {
                seeScheduleDeleteBtn = true;
                binding.selectAllLayout.visibility = View.GONE
                binding.deleteTv.visibility = View.GONE
                binding.scheduleDeleteBtn.visibility = View.VISIBLE

                binding.missionScheduleListLayout.visibility = View.GONE
            }

            override fun onClickExitBtn() {

            }
        })
    }

}
