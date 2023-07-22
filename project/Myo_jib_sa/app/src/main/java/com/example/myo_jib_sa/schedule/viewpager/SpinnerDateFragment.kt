package com.example.myo_jib_sa.schedule.viewpager

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.FragmentSpinnerDateBinding
import com.example.myo_jib_sa.schedule.api.scheduleDetail.ScheduleDetailResult
import java.util.*


class SpinnerDateFragment : Fragment() {

    private lateinit var binding : FragmentSpinnerDateBinding
    private lateinit var scheduleData : ScheduleDetailResult//sharedPreferences로 받은값 저장

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpinnerDateBinding.inflate(inflater, container, false)
        getData()//sharedPreference로 값 받기


        //값 저장할 sharedPreference 부르기
        val sharedPreference = requireContext().getSharedPreferences("scheduleData",
            Context.MODE_PRIVATE
        )
        val editor = sharedPreference.edit()


        //year, month, day로 분리
        var date = scheduleData.scheduleWhen.split("-")

        //datePicker초기값 설정
        binding.scheduleDatePicker.updateDate(date[0].toInt(), date[1].toInt(), date[2].toInt());

        binding.scheduleDatePicker.setOnDateChangedListener(object: DatePicker.OnDateChangedListener{
            override fun onDateChanged(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                //month+1이 실제 month
                Log.d("debug", "$year.${month+1}.$dayOfMonth")
                editor.putString("scheduleDate", "$year-${month+1}-$dayOfMonth")
            }
        })

        editor.apply()// data 저장!
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        //오늘날짜
//        val calendar = Calendar.getInstance()
//        var year = calendar.get(Calendar.YEAR)
//        var month = calendar.get(Calendar.MONTH)+1
//        var dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        var year =binding.scheduleDatePicker.year.toString()
        var month =(binding.scheduleDatePicker.month + 1).toString()
        var dayOfMonth =binding.scheduleDatePicker.dayOfMonth.toString()

        Log.d("debug", "$year.$month.$dayOfMonth")
    }

    override fun onStop() {
        super.onStop()
        Log.d("debug", "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("debug", "onDestroyView")
    }

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