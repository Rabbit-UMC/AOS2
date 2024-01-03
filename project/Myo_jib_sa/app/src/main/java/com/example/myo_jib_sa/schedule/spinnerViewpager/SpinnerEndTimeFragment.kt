package com.example.myo_jib_sa.schedule.spinnerViewpager

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TimePicker
import com.example.myo_jib_sa.databinding.FragmentSpinnerEndTimeBinding
import com.example.myo_jib_sa.schedule.api.scheduleDetail.ScheduleDetailResult
import java.text.DecimalFormat


class SpinnerEndTimeFragment : Fragment() {
    private lateinit var binding : FragmentSpinnerEndTimeBinding
    private var scheduleData : ScheduleDetailResult = ScheduleDetailResult(//sharedPreferences로 받은값 저장
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
        binding = FragmentSpinnerEndTimeBinding.inflate(inflater, container, false)

        getData()//sharedPreference값 받기

        //값 저장할 sharedPreference 부르기
        val sharedPreference = requireContext().getSharedPreferences("scheduleModifiedData",
            Context.MODE_PRIVATE
        )
        val editor = sharedPreference.edit()
        editor.putString("scheduleEndTime", scheduleData.endAt)//값 변경하지 않았을때 기본값으로 전달
        editor.apply()// data 저장!

        //timepicker 초기값 설정
        var endTime = scheduleData.endAt.split(":")
        if (endTime.size == 2) { // endTime 배열에 시간과 분이 둘 다 존재하는지 확인
            val hour = endTime[0].toIntOrNull() // 시간 문자열을 숫자로 변환
            val minute = endTime[1].toIntOrNull() // 분 문자열을 숫자로 변환

            if (hour != null && minute != null) {
                binding.endTimePicker.hour = hour
                binding.endTimePicker.minute = minute
            } else {
                // 숫자로 변환할 수 없는 값이 포함된 경우에 대한 예외 처리
                Log.e("debug", "Invalid time format: ${scheduleData.endAt}")
            }
        } else {
            // endTime 배열이 시간과 분으로 나누어진 형식이 아닌 경우에 대한 예외 처리
            Log.e("debug", "Invalid time format: ${scheduleData.endAt}")
        }

        binding.endTimePicker.descendantFocusability =
            NumberPicker.FOCUS_BLOCK_DESCENDANTS // editText가 눌리는 것을 막는다
        binding.endTimePicker.setOnTimeChangedListener(object : TimePicker.OnTimeChangedListener {
            override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
                val formatter = DecimalFormat("00")
                Log.d("debug", "endTime : 시:분 | ${formatter.format(hourOfDay)} : ${formatter.format(minute)}")
                editor.putString("scheduleEndTime", "${formatter.format(hourOfDay)}:${formatter.format(minute)}")
                editor.apply() // data 저장!
            }
        })

        return binding.root
    }


    //sharedPreference값 받기
    fun getData(){
        val sharedPreference = requireContext().getSharedPreferences("scheduleData",
            Context.MODE_PRIVATE
        )
        scheduleData.scheduleTitle = sharedPreference.getString("scheduleTitle", "").toString()
        scheduleData.scheduleWhen = sharedPreference.getString("scheduleDate", "").toString()
        scheduleData.missionTitle = sharedPreference.getString("missionTitle", "").toString()
        scheduleData.startAt = sharedPreference.getString("scheduleStartTime", "").toString()
        scheduleData.endAt = sharedPreference.getString("scheduleEndTime", "").toString()
        scheduleData.content = sharedPreference.getString("scheduleMemo", "").toString()
        scheduleData.missionId = sharedPreference.getLong("missionId", 0)
        scheduleData.scheduleId = sharedPreference.getLong("scheduleId", 0)
    }
}