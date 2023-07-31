package com.example.myo_jib_sa.schedule.viewpager

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.FragmentSpinnerMissionBinding
import com.example.myo_jib_sa.databinding.FragmentSpinnerStartTimeBinding
import com.example.myo_jib_sa.schedule.api.scheduleDetail.ScheduleDetailResult


class SpinnerStartTimeFragment : Fragment() {
    private lateinit var binding : FragmentSpinnerStartTimeBinding
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
        binding = FragmentSpinnerStartTimeBinding.inflate(inflater, container, false)

        getData()//sharedPreference값 받기

        //값 저장할 sharedPreference 부르기
        val sharedPreference = requireContext().getSharedPreferences("scheduleModifiedData",
            Context.MODE_PRIVATE
        )
        val editor = sharedPreference.edit()
        editor.putString("scheduleStartTime", scheduleData.startAt)//값 변경하지 않았을때 기본값으로 전달
        editor.apply()// data 저장!

        //timepicker 초기값 설정
        var startTime = scheduleData.startAt.split(":")
        binding.startTimePicker.hour = startTime[0].toInt();
        binding.startTimePicker.minute = startTime[1].toInt();

        binding.startTimePicker.setOnTimeChangedListener(object: TimePicker.OnTimeChangedListener {
            override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
                Log.d("debug", "startTime : 시:분 | $hourOfDay : $minute")
                editor.putString("scheduleStartTime", "$hourOfDay:$minute")
                editor.apply()// data 저장!
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

        Log.d("debug", " startAt: ${scheduleData.startAt}")
    }
}