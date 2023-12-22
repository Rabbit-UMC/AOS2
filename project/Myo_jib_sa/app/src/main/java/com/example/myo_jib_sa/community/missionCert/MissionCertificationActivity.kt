package com.example.myo_jib_sa.community.missionCert

import android.content.ComponentCallbacks
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.viewpager2.widget.ViewPager2
import com.example.myo_jib_sa.community.ManagerPageActivity
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.missionCert.MissionCertRetrofitManager
import com.example.myo_jib_sa.community.Retrofit.missionCert.MissionResponse
import com.example.myo_jib_sa.community.adapter.MissionCertViewpagerAdapter
import com.example.myo_jib_sa.databinding.ActivityMissionCertificationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MissionCertificationActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMissionCertificationBinding
    private val mAdapter = MissionCertViewpagerAdapter(this)
    private var missionId: Long = 0
    private var boardId: Int = 0
    private var missionImg: String = ""
    private var hostId: Long = 0

    //오늘 날짜
    private var date: Int = 0 //미션 몇일차 인지

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMissionCertificationBinding.inflate(layoutInflater)
        setContentView(binding.root)



        missionId = intent.getLongExtra("missionId", 0)
        missionId = intent.getLongExtra("missionId", 0)
        boardId = intent.getIntExtra("boardId", 0)
        missionImg = intent.getStringExtra("missionImg").toString()
        hostId = intent.getLongExtra("hostId", 0)

        Constance.jwt?.let { setMissionCert(it, 1, missionId) }

        //게시판 이름
        when (boardId) {
            Constance.ART_ID -> {
                binding.missionCertBoardNameTxt.text = "예술 게시판"
            }

            Constance.FREE_ID -> {
                binding.missionCertBoardNameTxt.text = "자유 게시판"
            }

            Constance.EXERCISE_ID -> {
                binding.missionCertBoardNameTxt.text = "운동 게시판"
            }
        }

        //뷰페이저 페이지 선택 되었을 때
        val onPageChangeListener = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // 페이지가 선택되었을 때 실행되는 로직을 여기에 작성합니다.
                val day = position + 1

            }

            override fun onPageScrollStateChanged(state: Int) {
                // 페이지 스크롤 상태가 변경될 때 실행되는 로직을 여기에 작성합니다.
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // 페이지가 스크롤되는 동안 실행되는 로직을 여기에 작성합니다.
            }
        }
        binding.missionCertVpr2.registerOnPageChangeCallback(onPageChangeListener)


        //인증 사진 올리기 엑티비티로 이동
        binding.MissionCertPostingBtn.setOnClickListener {
            val intent = Intent(this, MissionCertificationWriteActivity::class.java)
            intent.putExtra("boardId", boardId)
            startActivity(intent)
        }

        //관리자 페이지 이동
        binding.missionCertBoardNameTxt.setOnClickListener {
            if (hostId == Constance.USER_ID) {
                val intent = Intent(this, ManagerPageActivity::class.java)
                intent.putExtra("boardId", boardId)
                intent.putExtra("missionImg", missionImg)
                startActivity(intent)
            }
        }

        //뒤로가기
        binding.missionCertBackBtn.setOnClickListener {
            finish()
        }
    }

    //돌아왔을 때
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        Constance.jwt?.let { setMissionCert(it, 1, missionId) }
        super.onResume()
    }

    //미션 인증 사진 화면 프레그먼트 설정 + 미션 인증 화면 설정
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMissionCert(author: String, day: Int, mainMissionId: Long) {
        val retrofitManager = MissionCertRetrofitManager.getInstance(this)
        retrofitManager.mission(author, day, mainMissionId) { response ->
            Log.d("setMissionCertFrag 미션 인증 날짜 확인", day.toString())
            if (response.isSuccess == "true") {
                if (response.result != null) {


                    //미션 인증 엑티비티 뷰 설정
                    binding.missionCertMissionNameTxt.text = response.result.mainMissionName
                    binding.missionCertDdayTxt.text = response.result.dday

                    //랭킹 설정
                    //todo : setRankText(response)

                    //LocalDate 형식으로 Formate
                    if (response.result.startDay.isNotEmpty()) {

                        //미션 몇일차인지 설정
                        //date=setMissionDate(response.result.startDay)

                        Log.d("미션 인증 n일차", "$date")

                        //미션 시작 전일 경우
                        if (day < 1) {
                            //todo : beforeMission()
                        } else {
                            binding.missionCertNotMissionTxt.visibility = View.GONE
                        }
                        //뷰페이져 어댑터 연결
                        binding.missionCertVpr2.adapter = mAdapter
                        Constance.jwt?.let { mAdapter.setData(it, date, missionId, this) }
                        binding.missionCertVpr2.currentItem = date - 1
                    }

                } else {
                    Log.d("뷰페이져 어댑터로 리스트 전달", "List가 비었다네요")
                }
            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                val returnCode = response.code
                val returnMsg = response.message

                Log.d("미션 인증 API isSuccess가 false", "${returnCode}  ${returnMsg}")
            }


        }
    }
}
    //랭킹 설정
    /*private fun setRankText(response: MissionResponse){
        if (response.result.rank.isNotEmpty()) {
            /*if (response.result.rank.size >= 1 && response.result.rank[0].userName.isNotBlank()) {
                binding.missionCert1stNameTxt.text = response.result.rank[0].userName
            } else {
                binding.missionCert1stNameTxt.visibility = View.INVISIBLE
                binding.textView13.visibility = View.INVISIBLE
            }

            if (response.result.rank.size >= 2 && response.result.rank[1].userName.isNotBlank()) {
                binding.missionCert2ndNameTxt.text = response.result.rank[1].userName
            } else {
                binding.missionCert2ndNameTxt.visibility = View.INVISIBLE
                binding.textView14.visibility = View.INVISIBLE
            }

            if (response.result.rank.size >= 3 && response.result.rank[2].userName.isNotBlank()) {
                binding.missionCert3rdNameTxt.text = response.result.rank[2].userName
            } else {
                binding.missionCert3rdNameTxt.visibility = View.INVISIBLE
                binding.textView15.visibility = View.INVISIBLE
            }*/
        } else {
            rankTextGone()
        }
    }

    //랭킹이 없을 시 text 안보이게
    private fun rankTextGone(){
        /*binding.missionCert1stNameTxt.visibility=View.INVISIBLE
        binding.textView13.visibility=View.INVISIBLE
        binding.missionCert2ndNameTxt.visibility=View.INVISIBLE
        binding.textView14.visibility=View.INVISIBLE
        binding.missionCert3rdNameTxt.visibility=View.INVISIBLE
        binding.textView15.visibility=View.INVISIBLE
    }*/

    //미션 몇일차 인지
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMissionDate(day:String): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val missionStartDate = LocalDate.parse(day, formatter)
        val referenceDate= LocalDate.now()

        //미션 몇일차인지 설정
        return (referenceDate.toEpochDay()-missionStartDate.toEpochDay()).toInt()+1
    }

    //todo :  미션 시작 전일 경우 설정
    /*private fun beforeMission(){
        binding.missionCertRightBtn.visibility=View.GONE
        binding.missionCertLeftBtn.visibility=View.GONE
        binding.missionCertDay.visibility=View.GONE
        binding.missionCertLeftDay.visibility=View.GONE
        binding.missionCertRightBtn.visibility=View.GONE
        binding.missionCertNotMissionTxt.visibility=View.VISIBLE
        binding.missionCertNotMissionTxt.text="진행 중인 미션이 없습니다."
    }*/
}



*/