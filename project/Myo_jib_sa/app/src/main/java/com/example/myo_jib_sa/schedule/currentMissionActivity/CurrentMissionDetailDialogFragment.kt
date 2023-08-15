package com.example.myo_jib_sa.schedule.currentMissionActivity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.BuildConfig
import com.example.myo_jib_sa.databinding.DialogFragmentCurrentMissionDetailBinding
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.schedule.api.scheduleDetail.ScheduleDetailResponse
import com.example.myo_jib_sa.schedule.api.scheduleDetail.ScheduleDetailResult
import com.example.myo_jib_sa.schedule.api.scheduleDetail.ScheduleDetailService
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionDetail.CurrentMissionDetailResponse
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionDetail.CurrentMissionDetailService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit


class CurrentMissionDetailDialogFragment : DialogFragment() {
    private lateinit var binding: DialogFragmentCurrentMissionDetailBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentCurrentMissionDetailBinding.inflate(inflater, container, false)

        //CurrentMission 미션클릭이벤트함수에서 missionId 받아오기
        val bundle = arguments
        var missionId = bundle?.getLong("missionId")?: -1
        Log.d("debug", "\"missionId\" : $missionId")

        //missionDetail api연결
        missionDetailApi(missionId)

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



    //missionDetail api연결
    private fun missionDetailApi(missionId: Long) {
        val token: String = BuildConfig.API_TOKEN
        Log.d("debug", "token = "+token+"l");

        val service = RetrofitClient.getInstance().create(CurrentMissionDetailService::class.java)
        val listCall = service.currentMissionDetail(token, missionId)

        listCall.enqueue(object : Callback<CurrentMissionDetailResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<CurrentMissionDetailResponse>,
                response: Response<CurrentMissionDetailResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("debug", "retrofit: "+response.body().toString());
                    val result = response.body()!!.result

                    binding.missionTitleTv.text = result.missionTitle
                    binding.missionCategoryTv.text = result.categoryTitle
                    binding.missionStartDateTv.text = missionDateFormatter(result.startAt)
                    binding.missionPeriodTv.text = "${calMissionPeriod(result.startAt,result.endAt)}일"
                    binding.missionEndDateTv.text = missionDateFormatter(result.endAt)
                    binding.missionMemoTv.text = result.content

                } else {
                    Log.e("retrofit", "onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "onResponse: Error Body $errorBody")
                }
            }

            override fun onFailure(call: Call<CurrentMissionDetailResponse>, t: Throwable) {
                Log.e("retrofit", "onFailure: ${t.message}")
            }
        })
    }


    //startAt, endAt 포맷: yyyy.mm.dd
    fun missionDateFormatter(startAt: String?): String {


        val formatter = DecimalFormat("00")

        val time = startAt!!.split("-")
        val year = time[0].toInt()
        val month = time[1].toInt()
        val day = time[2].toInt()

        return "$year.${formatter.format(month)}.${formatter.format(day)}"
    }

    //미션 기간 구하기: 시작일 넣는 방식으로 계산 즉, startAt-endAt+1
    @RequiresApi(Build.VERSION_CODES.O)
    fun calMissionPeriod(startAt: String?, endAt: String?): Long{
        val start = startAt!!.split("-")
        val end = endAt!!.split("-")

        val startyear = start[0].toInt()
        val startmonth = start[1].toInt()
        val startday = start[2].toInt()

        val endyear = end[0].toInt()
        val endmonth = end[1].toInt()
        val endday = end[2].toInt()

        // 시작 날짜와 종료 날짜 생성
        val startDate = LocalDate.of(startyear, startmonth, startday)
        val endDate = LocalDate.of(endyear, endmonth, endday)

        return ChronoUnit.DAYS.between(startDate, endDate) + 1

    }

}