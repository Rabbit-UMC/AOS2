package com.example.myo_jib_sa.schedule.viewpager

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


class SpinnerStartTimeFragment : Fragment() {
    private lateinit var binding : FragmentSpinnerStartTimeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpinnerStartTimeBinding.inflate(inflater, container, false)

        binding.startTimePicker.setOnTimeChangedListener(object: TimePicker.OnTimeChangedListener {
            override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
                Log.d("debug", "startTime : 시:분 | $hourOfDay : $minute")
            }
        })

        return binding.root
    }
}