package com.example.myo_jib_sa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.spfManager
import com.example.myo_jib_sa.community.CommunityFragment
import com.example.myo_jib_sa.databinding.ActivityMainBinding
import com.example.myo_jib_sa.mission.MissionFragment
import com.example.myo_jib_sa.mypage.MypageFragment
import com.example.myo_jib_sa.mypage.UnregisterDialogFragment
import com.example.myo_jib_sa.schedule.ScheduleFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        spfManager.setAccessToken("eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ.eyJ1c2VySWR4IjoxLCJpYXQiOjE3MDU4MTExMjEsImV4cCI6MTcwNzI4MjM1MH0.Lre_HeCZbsabDXcS0xUvUEWPVNjJZTxuYvVG2ekVT3Y")

        //바텀 네비게이션 설정
        binding.mainBottomNavi.itemIconTintList = null //아이콘 태마색 변경 방지
        //초기 프레그먼트 설정
        supportFragmentManager.beginTransaction().replace(R.id.main_layout, ScheduleFragment()).commitAllowingStateLoss()
        //supportFragmentManager.beginTransaction().replace(R.id.main_layout, MissionFragment()).commitAllowingStateLoss()

        setBottomNavi()

        binding.unregisterBtn.setOnClickListener {
            showUnregisterDialog()
        }
    }

    private fun setBottomNavi(){
        // Item을 클릭했을 때 나타나는 이벤트 설정
        binding.mainBottomNavi.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.menu_schedule -> {
                    setFragment(ScheduleFragment())
                    binding.unregisterBtn.visibility = View.GONE
                    true
                }
                R.id.menu_mission -> {
                    setFragment(MissionFragment())
                    binding.unregisterBtn.visibility = View.GONE
                    true
                }
                R.id.menu_community -> {
                    setFragment(CommunityFragment())
                    binding.unregisterBtn.visibility = View.GONE
                    true
                }
                R.id.menu_mypage -> {
                    setFragment(MypageFragment())
                    binding.unregisterBtn.visibility = View.VISIBLE
                    true
                }
                else -> false //처리 완료 x
            }
        }
    }
    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_layout, fragment).commit()
    }

    private fun showUnregisterDialog() {
        val dialog = UnregisterDialogFragment()
        dialog.show(supportFragmentManager, "UnregisterDialogFragment")
    }

}