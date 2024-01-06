package com.example.myo_jib_sa.schedule.dialog

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myo_jib_sa.schedule.spinnerViewpager.SpinnerEndTimeFragment
import com.example.myo_jib_sa.schedule.spinnerViewpager.SpinnerStartTimeFragment

class TimeViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int =2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> SpinnerStartTimeFragment()
            else->SpinnerEndTimeFragment()
        }
    }
}