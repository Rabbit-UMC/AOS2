package com.example.myo_jib_sa.schedule.currentMissionActivity

import android.content.Context
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.R.*
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.databinding.ActivityCurrentMissionBinding
import com.example.myo_jib_sa.databinding.ItemSubScheduleBinding
import com.example.myo_jib_sa.databinding.ToastCurrentMissionDeleteBinding
import com.example.myo_jib_sa.schedule.CustomItemDecoration
import com.example.myo_jib_sa.schedule.currentMissionActivity.adapter.*
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionSchedule.CurrentMissionScheduleResponse
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionSchedule.CurrentMissionScheduleService
import com.example.myo_jib_sa.schedule.api.currentMission.CurrentMissionResponse
import com.example.myo_jib_sa.schedule.api.currentMission.CurrentMissionResult
import com.example.myo_jib_sa.schedule.api.currentMission.CurrentMissionService
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CurrentMissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrentMissionBinding
    private lateinit var currentMissionAdapter : CurrentMissionAdapter
    private lateinit var scheduleAdapter: CurrentMissionScheduleAdapter
    private var missionList = ArrayList<CurrentMissionResult>()
    private var scheduleList = ArrayList<ScheduleDeleteAdapterData>()
    private var seeScheduleDeleteBtn = true;
    private var deleteScheduleIdList : MutableList<Long> = mutableListOf()//삭제할 스케줄 아이디
    private var missionId:Long = 0 //작성한 일지 보기&지우기에서 사용
    private var isMissionDelete = false //미션 삭제


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setCurrentMissionScheduleAdapter()    //CurrentMissionScheduleAdapter 초기화
//

        //todo:삭제
        missionList.add(CurrentMissionResult(1, "미션1", 10, 1,"img", "60"))
        missionList.add(CurrentMissionResult(2, "미션2", 10, 1,"img", "60"))
        missionList.add(CurrentMissionResult(3, "미션3", 10, 1,"img", "60"))
        missionList.add(CurrentMissionResult(4, "미션4", 10, 1,"img", "60"))
        missionList.add(CurrentMissionResult(5, "미션5", 10, 1,"img", "60"))

        setCurrentMissionAdapter()//CurrentMission rv 연결
        currentMissionScheduleApi(missionList[0].missionId, missionList[0].missionTitle)//하위 스케쥴 데이터 추가+schedule rv연결+clickevent

//
//        scheduleList.add(ScheduleDeleteAdapterData(CurrentMissionScheduleResult(1,"일정1", "2023-12-23"), false))
//        scheduleList.add(ScheduleDeleteAdapterData(CurrentMissionScheduleResult(2,"일정2", "2023-12-23"), false))
//        scheduleList.add(ScheduleDeleteAdapterData(CurrentMissionScheduleResult(3,"일정3", "2023-12-23"), false))
//        scheduleList.add(ScheduleDeleteAdapterData(CurrentMissionScheduleResult(4,"일정4", "2023-12-23"), false))
//        scheduleList.add(ScheduleDeleteAdapterData(CurrentMissionScheduleResult(5,"일정5", "2023-12-23"), false))
//        scheduleList.add(ScheduleDeleteAdapterData(CurrentMissionScheduleResult(6,"일정6", "2023-12-23"), false))
//        scheduleList.add(ScheduleDeleteAdapterData(CurrentMissionScheduleResult(7,"일정7", "2023-12-23"), false))
//        scheduleList.add(ScheduleDeleteAdapterData(CurrentMissionScheduleResult(8,"일정8", "2023-12-23"), false))

//        setCurrentMissionScheduleAdapter()//CurrentMissionSchedule rv 연결
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
        currentMissionAdapter = CurrentMissionAdapter(missionList,
            onItemClickListener = object : CurrentMissionAdapter.OnItemClickListener{
                override fun onMissionClick(missionId: Long) {
                    showDetailDialog(missionId)
                }
                //작성한 일지보기 클릭
                override fun onScheduleClick(currentMissionData: CurrentMissionResult) {
                    binding.missionScheduleListLayout.visibility = View.VISIBLE
                    missionId=currentMissionData.missionId
                    currentMissionScheduleApi(missionId, currentMissionData.missionTitle)
                    binding.scheduleDeleteBtn.visibility = View.VISIBLE
                }
            })
        binding.missionListRv.layoutManager = LinearLayoutManager(this)
        binding.missionListRv.adapter = currentMissionAdapter
    }

    private fun showDetailDialog(missionId: Long) {
        val detailDialog = CurrentMissionDetailDialog(missionId)
        detailDialog.show(supportFragmentManager, "mission_detail_dialog")
    }

    //CurrentMissionScheduleAdapter 연결
    private fun setCurrentMissionScheduleAdapter() {//missionTitle:String
        scheduleAdapter = CurrentMissionScheduleAdapter(scheduleList,
            onItemClickListener = object : CurrentMissionScheduleAdapter.OnItemClickListener {
                override fun onClick(itemBinding: ItemSubScheduleBinding, selected: Boolean) {
                    if(!seeScheduleDeleteBtn) {
                        //삭제 버튼 설정
                        Log.d("debug", "selected schedule count : "+scheduleAdapter.getSelectedItemScheduleId().size)

                        if(scheduleAdapter.getSelectedItemScheduleId().size >0||isMissionDelete) {//삭제버튼 활성화
                            binding.cancelBtn.visibility = View.GONE
                            binding.deleteBtn.visibility = View.VISIBLE
                        }else{
                            binding.cancelBtn.visibility = View.VISIBLE
                            binding.deleteBtn.visibility = View.INVISIBLE
                        }

                        if(selected) {
                            itemBinding.root.setBackgroundColor(Color.parseColor("#1A234BD9"))
                        }
                        else {
                            itemBinding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
                        }
                    }
                }
            },
            getDisplayHeightSize())

        binding.scheduleListRv.apply {
            layoutManager = LinearLayoutManager(this@CurrentMissionActivity)
            adapter = scheduleAdapter
            addItemDecoration(CustomItemDecoration(this@CurrentMissionActivity))
        }
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

                    if(result.isNotEmpty()){
                        missionList= result as ArrayList<CurrentMissionResult>
                        setCurrentMissionAdapter()//CurrentMission rv 연결
                        binding.curMissionCountTv.text = result.size.toString()
                    }
                    else
                        binding.curMissionCountTv.text = "0"

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
    private fun currentMissionScheduleApi(missionId:Long, missionTitle:String) {
        binding.missionTitleInScheduleListTv.text =missionTitle
        // JWT 값 가져오기
        val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", null)

        scheduleList = ArrayList<ScheduleDeleteAdapterData>()

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

                    if(result != null) {
                        for (element in result) {
                            scheduleList.add(ScheduleDeleteAdapterData(element, false))
                        }
                        setCurrentMissionScheduleAdapter()//CurrentMissionSchedule rv 연결
                    }

                } else {
                    Log.e("retrofit", "currentMissionScheduleApi_onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "currentMissionScheduleApi_onResponse: Error Body $errorBody")
                }
            }
            override fun onFailure(call: Call<CurrentMissionScheduleResponse>, t: Throwable) {
                Log.e("retrofit", "currentMissionScheduleApi_onFailure: ${t.message}")
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

        binding.closeBtn.setOnClickListener{
            binding.missionScheduleListLayout.visibility = View.GONE
        }

        binding.cancelBtn.setOnClickListener {
            seeScheduleDeleteBtn = true;
            binding.missionDeleteLayout.visibility = View.GONE
            binding.deleteBtn.visibility = View.INVISIBLE
            binding.scheduleDeleteBtn.visibility = View.VISIBLE
            binding.closeBtn.visibility = View.VISIBLE
            binding.cancelBtn.visibility = View.GONE
        }

        //------------------------하위일정 레이아웃---------------------
        binding.scheduleDeleteBtn.setOnClickListener {
            seeScheduleDeleteBtn = false;
            binding.missionDeleteLayout.visibility = View.VISIBLE
            binding.deleteBtn.visibility = View.VISIBLE
            binding.scheduleDeleteBtn.visibility = View.GONE
            binding.closeBtn.visibility = View.GONE
            binding.cancelBtn.visibility = View.VISIBLE
        }

        //미션 삭제 선택 버튼
        binding.missionDeleteBtn.setOnClickListener {
            if(isMissionDelete){
                isMissionDelete = false
                binding.missionDeleteBtn.setImageResource(R.drawable.ic_check_box_unchecked)
            }else{
                isMissionDelete = true
                binding.missionDeleteBtn.setImageResource(R.drawable.ic_check_box_checked)
            }

            if(scheduleAdapter.getSelectedItemScheduleId().size >0 || isMissionDelete) {//삭제버튼 활성화
                binding.cancelBtn.visibility = View.GONE
                binding.deleteBtn.visibility = View.VISIBLE
            }else{
                binding.cancelBtn.visibility = View.VISIBLE
                binding.deleteBtn.visibility = View.INVISIBLE
            }
        }





        //삭제 버튼
        binding.deleteBtn.setOnClickListener {
            Log.d("debug_delete", "delete schedule: $deleteScheduleIdList")
            deleteScheduleIdList.addAll(scheduleAdapter.getSelectedItemScheduleId())//삭제할 스케줄 아이디 저장

            var mId : Long?
            mId = if(isMissionDelete){
                missionId
            }else{
                null
            }
            Log.d("debug", "mid:$mId deleteScheduleIdList : $deleteScheduleIdList")
            var deleteDialog = DeleteDialogFragment(mId, deleteScheduleIdList)
            deleteDialogItemClickEvent(deleteDialog)
            deleteDialog.show(this.supportFragmentManager, "DeleteDialogFragment")
        }

    }

    //scheduleDetailDialog Item클릭 이벤트
    fun deleteDialogItemClickEvent(dialog: DeleteDialogFragment){
        dialog.setButtonClickListener(object: DeleteDialogFragment.DeleteDialogListener{

            override fun onDeleteListener(message: String, missionFlag : Boolean) {
                if(missionFlag){
                    currentMissionApi()//currentMission api 재호출
                }
                seeScheduleDeleteBtn = true;
                binding.missionDeleteLayout.visibility = View.GONE
                binding.deleteBtn.visibility = View.GONE
                binding.scheduleDeleteBtn.visibility = View.VISIBLE
                binding.missionScheduleListLayout.visibility = View.GONE


                // 뷰 바인딩을 사용하여 커스텀 레이아웃을 인플레이트합니다.
                val snackbarBinding = ToastCurrentMissionDeleteBinding.inflate(layoutInflater)
                snackbarBinding.toastMessageTv.text = message

                // 스낵바 생성 및 설정
                val snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_SHORT).apply {
                    animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                    (view as Snackbar.SnackbarLayout).apply {
                        setBackgroundColor(Color.TRANSPARENT)
                        addView(snackbarBinding.root)
                        translationY = -30.dpToPx().toFloat()
                        elevation = 0f
                    }
                }
                // 스낵바 표시
                snackbar.show()
            }
        })
    }
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()


}
