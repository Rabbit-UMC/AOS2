package com.example.myo_jib_sa.schedule.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.DialogFragmentScheduleSpinnerBinding
import com.example.myo_jib_sa.schedule.viewpager.ScheduleSpinnerViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener


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

//        //viewPager 연결
//        binding.spinnerViewPager.adapter = ScheduleSpinnerViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
//        //TabLayout 연결
//        TabLayoutMediator(binding.spinnerTabLayout, binding.spinnerViewPager) { tab, position ->
//            tab.text = tabTitleArray[position]
//        }.attach()

        setTablayout()
        //position정보 전달받아 시작 포지션 설정
//        val bundle = arguments
//        var position = bundle!!.getInt("position")
//        binding.spinnerViewPager.setCurrentItem(position, false)
//        if(position == 0)



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

    var startPosition:Int = -1
    fun setTablayout(){
        val bundle = arguments
        startPosition = bundle!!.getInt("position")
        var mTabLayout = binding.spinnerTabLayout
        var mViewPager = binding.spinnerViewPager

        for(i in 0 ..tabTitleArray.size-1){
            createTabView(mTabLayout, tabTitleArray[i], R.drawable.ic_schedule_edit_tab_indicator, i)
        }
        var scheduleSpinnerViewPagerAdapter = ScheduleSpinnerViewPagerAdapter(
            requireActivity().getSupportFragmentManager(), lifecycle
        )
        mViewPager.setAdapter(scheduleSpinnerViewPagerAdapter)




        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                mTabLayout.selectTab(mTabLayout.getTabAt(position))
            }
        })

        binding.spinnerViewPager.setCurrentItem(startPosition, false)

        mTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                mViewPager.setCurrentItem(tab.position)
                val customView = tab.customView
                val tabIcon = customView?.findViewById<ImageView>(R.id.customTab_img)
                tabIcon?.visibility = View.VISIBLE
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val customView = tab.customView
                val tabIcon = customView?.findViewById<ImageView>(R.id.customTab_img)
                tabIcon?.visibility = View.INVISIBLE
            }
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
    private fun createTabView(tabLayout: TabLayout, tabName: String, iconResId: Int, i:Int) {
        val tabView: View = LayoutInflater.from(requireContext()).inflate(R.layout.tablayout_schedule_custom_tab_, null)
        val txt_name: TextView = tabView.findViewById<View>(R.id.customTab_tv) as TextView
        val txt_icon: ImageView = tabView.findViewById<View>(R.id.customTab_img) as ImageView

        txt_icon.setImageResource(iconResId)
        txt_name.text = tabName

        if(startPosition == i)
            txt_icon.visibility = View.VISIBLE
        else
            txt_icon.visibility = View.INVISIBLE

        val tab = tabLayout.newTab().setCustomView(tabView)
        tabLayout.addTab(tab)


    }

}