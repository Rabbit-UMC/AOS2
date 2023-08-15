package com.example.myo_jib_sa.schedule.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.DialogFragmentScheduleSpinnerBinding
import com.example.myo_jib_sa.schedule.api.scheduleDetail.ScheduleDetailResult
import com.example.myo_jib_sa.schedule.spinnerViewpager.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


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


        setTablayout()//dialog에 viewpager+tablayout셋팅
        setButton() //버튼 세팅


        val sharedPreference = requireContext().getSharedPreferences("scheduleData",
            Context.MODE_PRIVATE
        )
        //값 저장할 sharedPreference 부르기
        val modifiedSharedPreference = requireContext().getSharedPreferences("scheduleModifiedData",
            Context.MODE_PRIVATE
        )
        val editor = modifiedSharedPreference.edit()
        editor.putString("scheduleTitle", sharedPreference.getString("scheduleTitle", "").toString())//값 변경하지 않았을때 기본값으로 전달
        editor.putString("scheduleDate", sharedPreference.getString("scheduleDate", "").toString())
        editor.putString("missionTitle", sharedPreference.getString("missionTitle", "").toString())
        editor.putString("scheduleStartTime", sharedPreference.getString("scheduleStartTime", "").toString())
        editor.putString("scheduleEndTime", sharedPreference.getString("scheduleEndTime", "").toString())
        editor.putString("scheduleMemo", sharedPreference.getString("scheduleMemo", "").toString())
        editor.putLong("missionId", sharedPreference.getLong("missionId", 0))
        editor.putLong("scheduleId", sharedPreference.getLong("scheduleId", 0))
        editor.apply()// data 저장!



        return binding.root
    }


    //버튼 세팅
    private fun setButton(){
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

            buttonClickListener.onClickCompeleteBtn(true)
            //확인눌렀는지 판단위해
//            val bundle = Bundle()
//            bundle.putBoolean("isEdit", true)
//            //ScheduleEditDialogFragment열기
//            val scheduleEditDialogFragment = ScheduleEditDialogFragment()
//            scheduleEditDialogFragment.arguments = bundle
//            scheduleEditDialogFragment.show(requireActivity().supportFragmentManager, "ScheduleEditDialog")
            dismiss()//spinnerDialog종료
        }
        binding.cancelBtn.setOnClickListener {

            buttonClickListener.onClickCompeleteBtn(false)
            //확인눌렀는지 판단위해
//            val bundle = Bundle()
//            bundle.putBoolean("isEdit", false)
//            //ScheduleEditDialogFragment열기
//            val scheduleEditDialogFragment = ScheduleEditDialogFragment()
//            scheduleEditDialogFragment.arguments = bundle
//            scheduleEditDialogFragment.show(requireActivity().supportFragmentManager, "ScheduleEditDialog")
            dismiss()//spinnerDialog종료
        }
    }


    //scheduleFragment에 값을 넘겨주기 위한 인터페이스
    interface OnButtonClickListener {
        fun onClickCompeleteBtn(isEdit: Boolean)
    }
    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }
    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener
    //====================================================================



    //dialog에 viewpager+tablayout셋팅
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


    //ScheduleSpinner의 ViewPagerAdapter
    inner class ScheduleSpinnerViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):
        FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int {
            return 4
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    val spinnerMissionFragment = SpinnerMissionFragment()
                    spinnerMissionFragment
                }
                1 -> {
                    val spinnerDateFragment = SpinnerDateFragment()
                    spinnerDateFragment
                }
                2 -> {
                    val spinnerStartTimeFragment = SpinnerStartTimeFragment()
                    spinnerStartTimeFragment
                }
                else -> {
                    val spinnerEndTimeFragment = SpinnerEndTimeFragment()
                    spinnerEndTimeFragment
                }
            }
        }
    }

}