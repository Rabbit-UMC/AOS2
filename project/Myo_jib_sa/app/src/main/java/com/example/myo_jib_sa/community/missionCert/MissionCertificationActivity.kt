package com.example.myo_jib_sa.community.missionCert

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Manager.ManagerPageActivity
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.api.missionCert.MissionCertRetrofitManager
import com.example.myo_jib_sa.community.adapter.MissionCertViewpagerAdapter
import com.example.myo_jib_sa.community.api.missionCert.MissionResponse
import com.example.myo_jib_sa.databinding.ActivityMissionCertificationBinding
import com.example.myo_jib_sa.databinding.FragmentBottomsheetRankingBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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

    // BottomSheet layout 변수
    private val bottomSheetLayout by lazy { findViewById<ConstraintLayout>(R.id.bottomsheet_constraint) }

    // bottomSheetBehavior
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>


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

        //랭킹 확인 (바텀 시트)
        initializePersistentBottomSheet()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN //처음은 숨김 상태
        //persistentBottomSheetEvent() 내부에서 어떤 이벤트가 필요할 떄 정의해서 사용하기

        binding.rankingImg.setOnClickListener {
            toggleBottomSheetVisibility()
        }

        Constance.jwt?.let { setMissionCert(it, 1, missionId) }

        //게시판 이름
        when (boardId.toLong()) {
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
                setDay(day)

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

    //바텀 시트
    // Persistent BottomSheet 초기화
    private fun initializePersistentBottomSheet() {

        // BottomSheetBehavior에 layout 설정
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // BottomSheetBehavior state 변경 이벤트 처리
                // 필요하다면 상태별 처리를 여기에 추가할 수 있습니다.
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // BottomSheetBehavior slide 이벤트 처리
            }
        })
    }

    // PersistentBottomSheet 토글 이벤트
    private fun toggleBottomSheetVisibility() {
        when (bottomSheetBehavior.state) {
            BottomSheetBehavior.STATE_EXPANDED, BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                // BottomSheet가 펼쳐진 상태일 때는 숨김
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
            else -> {
                // BottomSheet가 숨겨진 상태일 때는 펼침
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    //미션 인증 사진 화면 프레그먼트 설정 + 미션 인증 화면 설정
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMissionCert(author: String, day: Int, mainMissionId: Long) {
        val retrofitManager = MissionCertRetrofitManager.getInstance(this)
        retrofitManager.mission(author, day, mainMissionId) { response ->
            Log.d("setMissionCertFrag 미션 인증 날짜 확인", day.toString())
            if (response.isSuccess) {
                if (response.result != null) {


                    //미션 인증 엑티비티 뷰 설정
                    binding.missionCertMissionNameTxt.text = response.result.mainMissionName
                    binding.missionCertDdayTxt.text = response.result.dday

                    //랭킹 설정
                    setRankText(response)

                    Log.d("시작 날짜 확인", "${response.result.startDay}")
                    Log.d("디데이 확인", "${response.result.dday}")
                    //LocalDate 형식으로 Formate
                    if (response.result.startDay.isNotEmpty()) {

                        //미션  몇일차인지 설정
                        //todo : date = setMissionDate(response.result.startDay)
                        date=9
                        Log.d("미션 인증 n일차", "$date")

                        //미션 시작 전일 경우
                        if (day < 1) {
                            beforeMission()
                        } else {
                            binding.missionCertNotMissionTxt.visibility = View.GONE
                        }
                        //뷰페이져 어댑터 연결
                        binding.missionCertVpr2.adapter = mAdapter
                        Constance.jwt?.let { mAdapter.setData(it, date, missionId, this) }
                        binding.missionCertVpr2.currentItem=date-1
                    }

                } else {
                    Log.d("뷰페이져 어댑터로 리스트 전달", "List가 비었다네요")
                }
            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리

                Log.d("미션 인증 API isSuccess가 false", "${response.errorCode}  ${response.errorMessage}")
            }


        }
    }

    //n일차 설정
    private fun setDay(day:Int){
        dayVisible()

        binding.missionCertDay.text="${day}일차"
        binding.missionCertRightDay.text="${day+1}일차"
        binding.missionCertLeftDay.text="${day-1}일차"

        //1일차면
        if(day==1){
            binding.missionCertLeftDay.visibility=View.GONE
            binding.missionCertLeftV.visibility=View.GONE
        }

        //마지막 날이면
        if(day==date){
            binding.missionCertRightDay.visibility=View.GONE
            binding.missionCertRightV.visibility=View.GONE
        }
    }

    //n일차 뷰 보이게
    private fun dayVisible(){
        binding.missionCertRightDay.visibility=View.VISIBLE
        binding.missionCertRightV.visibility=View.VISIBLE
        binding.missionCertLeftDay.visibility=View.VISIBLE
        binding.missionCertLeftV.visibility=View.VISIBLE
    }

    //랭킹 설정
    private fun setRankText(response: MissionResponse){
        if (response.result.rank.isNotEmpty()) {
            if (response.result.rank.size >= 1 && response.result.rank[0].userName.isNotBlank()) {
                binding.rank1Txt.text = response.result.rank[0].userName
            } else {
                binding.rank1Txt.visibility = View.INVISIBLE
            }

            if (response.result.rank.size >= 2 && response.result.rank[1].userName.isNotBlank()) {
                binding.rank2Txt.text = response.result.rank[1].userName
            } else {
                binding.rank2Txt.visibility = View.INVISIBLE
            }

            if (response.result.rank.size >= 3 && response.result.rank[2].userName.isNotBlank()) {
                binding.rank3Txt.text = response.result.rank[2].userName
            } else {
                binding.rank3Txt.visibility = View.INVISIBLE
            }
        } else {
            rankTextGone()
        }
    }

    //랭킹이 없을 시 text 안보이게
    private fun rankTextGone(){
        binding.rank1Txt.visibility=View.INVISIBLE
        binding.rank2Txt.visibility=View.INVISIBLE
        binding.rank3Txt.visibility=View.INVISIBLE
    }

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
    private fun beforeMission(){
        binding.missionCertDay.visibility=View.GONE
        binding.missionCertLeftDay.visibility=View.GONE
        binding.missionCertNotMissionTxt.visibility=View.VISIBLE
        binding.missionCertNotMissionTxt.text="진행 중인 미션이 없습니다."
    }
}

