package com.example.myo_jib_sa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.myo_jib_sa.community.CommunityFragment
import com.example.myo_jib_sa.databinding.ActivityMainBinding
import com.example.myo_jib_sa.mission.MissionFragment
import com.example.myo_jib_sa.mypage.MypageFragment
import com.example.myo_jib_sa.schedule.ScheduleFragment
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //바텀 네비게이션 설정
        binding.mainBottomNavi.itemIconTintList = null //아이콘 태마색 변경 방지
        //초기 프레그먼트 설정
        supportFragmentManager.beginTransaction().replace(R.id.main_layout, ScheduleFragment()).commitAllowingStateLoss()
        setBottomNavi()



    }

    private fun setBottomNavi(){
        // Item을 클릭했을 때 나타나는 이벤트 설정
        binding.mainBottomNavi.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.menu_schedule -> {
                    setFragment(ScheduleFragment())
                    true
                }
                R.id.menu_mission -> {
                    setFragment(MissionFragment())
                    true
                }
                R.id.menu_community -> {
                    setFragment(CommunityFragment())
                    true
                }
                R.id.menu_mypage -> {
                    setFragment(MypageFragment())
                    true
                }
                else -> false //처리 완료 x
            }
        }
    }
    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_layout, fragment).commit()
    }



}