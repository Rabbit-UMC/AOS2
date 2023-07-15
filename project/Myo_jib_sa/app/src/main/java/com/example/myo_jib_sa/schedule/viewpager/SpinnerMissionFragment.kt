package com.example.myo_jib_sa.schedule.viewpager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.example.myo_jib_sa.databinding.FragmentSpinnerMissionBinding


class SpinnerMissionFragment : Fragment() {
    private lateinit var binding : FragmentSpinnerMissionBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpinnerMissionBinding.inflate(inflater, container, false)

        var numberpicker = binding.missionSpinner
        val missionList = arrayOf<String>(
            "헬스(D+5)",
            "미션1(D-10)",
            "미션2(D-20)",
            "미션3(D-30)",
            "미션4(D-40)",
            "미션5(D-50)"
        )
        numberpicker.minValue = 0
        numberpicker.maxValue = missionList.size-1
        numberpicker.value = 3//초기값 설정
        numberpicker.displayedValues = missionList
        numberpicker.wrapSelectorWheel = false //순환
        numberpicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS //editText가 눌리는 것을 막는다
        numberpicker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(picker: NumberPicker, oldVal: Int, newVal: Int) {
                //Display the newly selected value from picker
                //tv.setText("Selected value : " + missionList.get(newVal))
                Log.d("debug", "missionTitle : ${missionList.get(newVal)}")
            }
        })

        return binding.root
    }

    fun setTablayoutBtn(){

    }
}