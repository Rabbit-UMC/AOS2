package com.example.myo_jib_sa.schedule.currentMissionActivity

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.databinding.DialogFragmentCurrentMissionDeleteBinding
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.schedule.api.scheduleDelete.ScheduleDeleteResponse
import com.example.myo_jib_sa.schedule.api.scheduleDelete.ScheduleDeleteService
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionDelete.CurrentMissionDeleteResponse
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionDelete.CurrentMissionDeleteService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DeleteDialogFragment(
    private val missionId: Long?,
    private val scheduleIdList: MutableList<Long>
) : DialogFragment() {
    private lateinit var binding: DialogFragmentCurrentMissionDeleteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentCurrentMissionDeleteBinding.inflate(inflater, container, false)


        //requestWindowFeature(Window.FEATURE_NO_TITLE)
        initViews()
        if (missionId != null) {
            currentMissionDeleteApi(missionId)
        }//미션삭제api
        scheduleDeleteApi(scheduleIdList)//스케줄 삭제 api

        return binding.root
    }

    private fun initViews() {

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        //확인
        binding.yesTv.setOnClickListener{
            dismiss()
            buttonClickListener.onClickYesBtn()
        }
        //취소
        binding.exitTv.setOnClickListener {
            dismiss()
            buttonClickListener.onClickExitBtn()
        }
    }

    //currentMission api연결
    private fun currentMissionDeleteApi(missionId:Long) {
        // JWT 값 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", null)

        val service = RetrofitClient.getInstance().create(CurrentMissionDeleteService::class.java)
//        val url ="app/mission/my-missions/${deleteMissionIdList.joinToString(",")}"
//        Log.d("debug", "$url");
//        val listCall = service.currentMissionDelete(token, url)

        val listCall = service.currentMissionDelete(token, missionId)
        listCall.enqueue(object : Callback<CurrentMissionDeleteResponse> {
            override fun onResponse(
                call: Call<CurrentMissionDeleteResponse>,
                response: Response<CurrentMissionDeleteResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("debug", "retrofit: "+response.body().toString());
                    val result = response.body()!!.result


                } else {
                    Log.e("retrofit", "currentMissionDeleteApi_onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "currentMissionDeleteApi_onResponse: Error Body $errorBody")
                }
            }
            override fun onFailure(call: Call<CurrentMissionDeleteResponse>, t: Throwable) {
                Log.e("retrofit", "currentMissionDeleteApi_onFailure: ${t.message}")
            }
        })
    }

    //scheduleDelete api연결: 일정삭제
    private fun scheduleDeleteApi(deleteScheduleIdList:MutableList<Long>) {
        // JWT 값 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", null)

        val service = RetrofitClient.getInstance().create(ScheduleDeleteService::class.java)
//        val url ="app/schedule/${deleteScheduleIdList.joinToString(", ")}"
//        Log.d("debug", "$url");
//        val listCall = service.scheduleDeleteModifyVer(token, url)
        val listCall = service.scheduleDeleteModifyVer(token, deleteScheduleIdList)

        listCall.enqueue(object : Callback<ScheduleDeleteResponse> {
            override fun onResponse(
                call: Call<ScheduleDeleteResponse>,
                response: Response<ScheduleDeleteResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", response.body().toString());

                    //scheduleAdaptar.removeTask(position)
                }else {
                    Log.e("retrofit", "scheduleDeleteApi_onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "scheduleDeleteApi_onResponse: Error Body $errorBody")
                }}
            override fun onFailure(call: Call<ScheduleDeleteResponse>, t: Throwable) {
                Log.e("retrofit", "scheduleDeleteApi_onFailure: ${t.message}")
            }
        })
    }

    // 인터페이스
    interface OnButtonClickListener {
        fun onClickYesBtn()
        fun onClickExitBtn()
    }
    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }
    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener



}