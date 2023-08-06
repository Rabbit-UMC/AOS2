package com.example.myo_jib_sa.schedule.dialog

import android.content.Context
import android.content.Context.MODE_PRIVATE
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
import com.example.myo_jib_sa.databinding.DialogFragmentScheduleDetailBinding
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.schedule.api.scheduleDetail.ScheduleDetailResponse
import com.example.myo_jib_sa.schedule.api.scheduleDetail.ScheduleDetailResult
import com.example.myo_jib_sa.schedule.api.scheduleDetail.ScheduleDetailService
import com.example.myo_jib_sa.schedule.api.scheduleModify.ScheduleModifyRequest
import com.example.myo_jib_sa.schedule.api.scheduleModify.ScheduleModifyResponse
import com.example.myo_jib_sa.schedule.api.scheduleModify.ScheduleModifyService
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class ScheduleDetailDialogFragment : DialogFragment() {
    private lateinit var binding: DialogFragmentScheduleDetailBinding
    private var result:ScheduleDetailResult = ScheduleDetailResult(
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
        binding = DialogFragmentScheduleDetailBinding.inflate(inflater, container, false)

        //shceduleFragment의 스케쥴클릭이벤트함수에서 scheduleId값 받아오기
        val bundle = arguments
        var scheduleId = bundle?.getLong("scheduleId")?: -1
        Log.d("debug", "\"scheduleId\" : $scheduleId")

        //scheduleDetail api연결
        scheduleDetailApi(scheduleId)

        // 레이아웃 배경을 투명하게 해줌
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)





        //x누르면 dialog종료
        binding.exitTv.setOnClickListener {
            dismiss()
        }



        CoroutineScope(Dispatchers.Main).launch {
            delay(100)

            binding.editBtn.setOnClickListener {
                Log.d(
                    "debug",
                    "scheduleData!!.missionId: ${result!!.missionId}, scheduleData!!.scheduleId: ${result!!.scheduleId}"
                )

                //sharedPreference저장
                val sharedPreference =
                    requireContext().getSharedPreferences("scheduleData", MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putString("scheduleTitle", binding.scheduleTitleTv.text.toString())
                editor.putString("scheduleDate", binding.scheduleDateTv.text.toString())
                editor.putString("missionTitle", binding.missionTitleTv.text.toString())
                editor.putString("scheduleStartTime", result?.startAt)
                editor.putString("scheduleEndTime", result?.endAt)
                editor.putString("scheduleMemo", binding.scheduleMemoTv.text.toString())
                result?.missionId?.let { it1 -> editor.putLong("missionId", it1) }
                result?.scheduleId?.let { it1 -> editor.putLong("scheduleId", it1) }
                editor.apply()

                Log.d("timeDebug", "scheduleStartTime = ${result?.startAt}")

//            var bundle = Bundle()
//            bundle.putString("scheduleTitle", binding.scheduleTitleTv.text.toString())
//            bundle.putString("scheduleDate", binding.scheduleDateTv.text.toString())
//            bundle.putString("missionTitle", binding.missionTitleTv.text.toString())
//            bundle.putString("scheduleStartTime", binding.scheduleStartTimeTv.text.toString())
//            bundle.putString("scheduleEndTime", binding.scheduleEndTimeTv.text.toString())
//            bundle.putString("scheduleMemo", binding.scheduleMemoTv.text.toString())
//            bundle.putLong("missionId", result.missionId)
//            bundle.putLong("scheduleId", result.scheduleId)
//            ScheduleEditDialogFragment().arguments = bundle

                buttonClickListener.onClickEditBtn()
            }
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

                    binding.scheduleTitleTv.text = result!!.scheduleTitle
                    binding.scheduleDateTv.text = result!!.scheduleWhen
                    binding.missionTitleTv.text = result!!.missionTitle
                    binding.scheduleStartTimeTv.text = scheduleTimeFormatter(result!!.startAt)
                    binding.scheduleEndTimeTv.text = scheduleTimeFormatter(result!!.endAt)
                    binding.scheduleMemoTv.text = result!!.content


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
