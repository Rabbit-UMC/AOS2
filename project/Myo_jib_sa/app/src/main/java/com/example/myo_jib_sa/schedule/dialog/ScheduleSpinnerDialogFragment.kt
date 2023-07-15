package com.example.myo_jib_sa.schedule.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.DialogFragmentScheduleSpinnerBinding
import com.example.myo_jib_sa.schedule.viewpager.ScheduleSpinnerViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator


class ScheduleSpinnerDialogFragment : DialogFragment() {
    private lateinit var binding : DialogFragmentScheduleSpinnerBinding
    private val tabTitleArray = arrayOf(
        "미션",
        "날짜",
        "시작시간",
        "종료시간"
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentScheduleSpinnerBinding.inflate(inflater, container, false)

        // 레이아웃 배경을 투명하게 해줌
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        //viewPager 연결
        binding.spinnerViewPager.adapter = ScheduleSpinnerViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle)

        //TabLayout 연결
        TabLayoutMediator(binding.spinnerTabLayout, binding.spinnerViewPager) { tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()


        //pre이동
        binding.preBtn.setOnClickListener {
            var current = binding.spinnerViewPager.currentItem
            if (current == 0){
                binding.spinnerViewPager.setCurrentItem(3, false)
            }
            else{
                binding.spinnerViewPager.setCurrentItem(current-1, false)
            }
        }
        //next 이동
        binding.nextBtn.setOnClickListener {
            var current = binding.spinnerViewPager.currentItem
            if (current == 3){
                binding.spinnerViewPager.setCurrentItem(0, false)
            }
            else{
                binding.spinnerViewPager.setCurrentItem(current+1, false)
            }
        }


        //확인, 취소 버튼
        binding.completeBtn.setOnClickListener{
            dismiss()
        }
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        return binding.root
    }


}