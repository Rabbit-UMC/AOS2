package com.example.myo_jib_sa.schedule.currentMissionActivity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.BuildConfig
import com.example.myo_jib_sa.databinding.DialogFragmentCurrentMissionDetailBinding
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.schedule.api.scheduleDetail.ScheduleDetailResponse
import com.example.myo_jib_sa.schedule.api.scheduleDetail.ScheduleDetailResult
import com.example.myo_jib_sa.schedule.api.scheduleDetail.ScheduleDetailService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat


class CurrentMissionDetailDialogFragment : DialogFragment() {
    private lateinit var binding: DialogFragmentCurrentMissionDetailBinding
    private var result: ScheduleDetailResult = ScheduleDetailResult(
        scheduleId = 0,
        missionId = 0,
        missionTitle = "",
        scheduleTitle= "",
        startAt= "",
        endAt= "",
        content= "",
        scheduleWhen= ""
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentCurrentMissionDetailBinding.inflate(inflater, container, false)

        //CurrentMission 미션클릭이벤트함수에서 missionId 받아오기
        val bundle = arguments
        var missionId = bundle?.getLong("missionId")?: -1
        Log.d("debug", "\"missionId\" : $missionId")

        ////scheduleDetail api연결
        //scheduleDetailApi(missionId)

        // 레이아웃 배경을 투명하게 해줌
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)


        //x누르면 dialog종료
        binding.checkBtn.setOnClickListener {
            dismiss()
        }



        return binding.root
    }

    // 인터페이스
    interface OnButtonClickListener {
        fun onClickEditBtn()
    }

    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }

    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener



    //scheduleDetail api연결
    fun scheduleDetailApi(scheduleId: Long) {
        val token: String = BuildConfig.KAKAO_API_KEY
        Log.d("debug", "token = "+token+"l");

        val service = RetrofitClient.getInstance().create(ScheduleDetailService::class.java)
        val listCall = service.scheduleDetail(token, scheduleId)

        listCall.enqueue(object : Callback<ScheduleDetailResponse> {
            override fun onResponse(
                call: Call<ScheduleDetailResponse>,
                response: Response<ScheduleDetailResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("debug", "retrofit: "+response.body().toString());
                    result = response.body()!!.result

                    binding.missionTitleTv.text = result!!.scheduleTitle
                    binding.missionStartDateTv.text = result!!.scheduleWhen
                    binding.missionDdayTv.text = result!!.missionTitle
                    binding.missionEndDateTv.text = scheduleTimeFormatter(result!!.startAt)


                } else {
                    Log.e("retrofit", "onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "onResponse: Error Body $errorBody")
                }
            }

            override fun onFailure(call: Call<ScheduleDetailResponse>, t: Throwable) {
                Log.e("retrofit", "onFailure: ${t.message}")
            }
        })
    }


    //startTime, endTime 포맷
    fun scheduleTimeFormatter(startAt: String?): String {
        val formatter = DecimalFormat("00")

        val time = startAt!!.split(":")
        val hour = time[0].toInt()
        val minute = time[1].toInt()
        if (hour < 12) {
            return "오전 $startAt"
        } else {
            if (hour == 12)
                return "오후 $startAt"
            else
                return "오후 ${formatter.format(hour - 12)}:${formatter.format(minute)}"
        }
    }

}