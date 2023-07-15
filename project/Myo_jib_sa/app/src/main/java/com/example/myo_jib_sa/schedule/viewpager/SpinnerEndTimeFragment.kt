package com.example.myo_jib_sa.schedule.viewpager

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.FragmentSpinnerDateBinding
import com.example.myo_jib_sa.databinding.FragmentSpinnerEndTimeBinding


class SpinnerEndTimeFragment : Fragment() {
    private lateinit var binding : FragmentSpinnerEndTimeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpinnerEndTimeBinding.inflate(inflater, container, false)


        //timepicker 초기값 설정
//        timePicker.setCurrentHour(hour);
//        timePicker.setCurrentMinute(min);

        binding.endTimePicker.setOnTimeChangedListener(object: TimePicker.OnTimeChangedListener {
            override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
                Log.d("debug", "endTime : 시:분 | $hourOfDay : $minute")
            }
        })



        return binding.root
    }

}