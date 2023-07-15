package com.example.myo_jib_sa.schedule.viewpager

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
import java.util.*


class SpinnerDateFragment : Fragment() {

    private lateinit var binding : FragmentSpinnerDateBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpinnerDateBinding.inflate(inflater, container, false)

        //datePicker초기값 설정
//        datePicker.updateDate(year, month, day);

        binding.scheduleDatePicker.setOnDateChangedListener(object: DatePicker.OnDateChangedListener{
            override fun onDateChanged(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                //month+1이 실제 month
                Log.d("debug", "$year.${month+1}.$dayOfMonth")
            }
        })

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


}