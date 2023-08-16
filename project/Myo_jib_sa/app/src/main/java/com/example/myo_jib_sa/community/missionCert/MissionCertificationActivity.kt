package com.example.myo_jib_sa.community.missionCert

import android.content.ComponentCallbacks
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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

class MissionCertificationActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMissionCertificationBinding
    val mAdapter=MissionCertViewpagerAdapter(this)
    var missionId:Long=0
    var boardId:Int=0
    var testDay:Int=0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMissionCertificationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        missionId = intent.getLongExtra("missionId", 0)
        //todo: missionId=intent.getLongExtra("missionId",0)
        boardId = intent.getIntExtra("boardId", 0)
        testDay = 5 //todo: 미션 몇일차 인지 어딘가에서 알아와야함 일단 teatday 사용

        setMissionCert(Constance.jwt, 1, missionId)

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



        //뷰페이져 어댑터 연결
        binding.missionCertVpr2.adapter = mAdapter
        mAdapter.setData(Constance.jwt, testDay,missionId,this)


            //뷰페이저
            val onPageChangeListener = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    // 페이지가 선택되었을 때 실행되는 로직을 여기에 작성합니다.
                    val day = position + 1
                    val lastDay = testDay
                    //텍스트 설정
                    binding.missionCertDay.text = day.toString()
                    binding.missionCertLeftDay.text = (day - 1).toString()
                    binding.missionCertRightDay.text = (day + 1).toString()
                    if (day == 1) {
                        binding.missionCertLeftBtn.visibility = View.INVISIBLE
                        binding.missionCertLeftDay.text = ""
                        Log.d("버튼 상태", "왼쪽 버튼 안보임")
                    } else {
                        binding.missionCertLeftBtn.visibility = View.VISIBLE
                    }
                    if (day == lastDay) {
                        binding.missionCertRightBtn.visibility = View.INVISIBLE
                        binding.missionCertRightDay.text = ""
                        Log.d("버튼 상태", "오른쪽 버튼 안보임")
                    } else {
                        binding.missionCertRightBtn.visibility = View.VISIBLE
                    }

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


            //다음 페이지 넘어가기
            binding.missionCertRightBtn.setOnClickListener {
                val currentItem = binding.missionCertVpr2.currentItem
                binding.missionCertVpr2.setCurrentItem(currentItem + 1, true)
            }

            binding.missionCertLeftBtn.setOnClickListener {
                val currentItem = binding.missionCertVpr2.currentItem
                binding.missionCertVpr2.setCurrentItem(currentItem - 1, true)
            }


            //인증 사진 올리기 엑티비티로 이동
            binding.MissionCertPostingBtn.setOnClickListener {
                val intent = Intent(this, MissionCertificationWriteActivity::class.java)
                intent.putExtra("boardID", boardId)
                startActivity(intent)
            }
            binding.missionCertTalkTxt.setOnClickListener {
                val intent = Intent(this, MissionCertificationWriteActivity::class.java)
                intent.putExtra("boardID", boardId)
                startActivity(intent)
            }

            //관리자 페이지 이동
            binding.missionCertBoardNameTxt.setOnClickListener {
                // TextView 클릭될 시 할 코드작성 todo: 주석 제거 하기
                if (true /*hostId==Constance.USER_ID*/) {
                    val intent = Intent(this, ManagerPageActivity::class.java)
                    intent.putExtra("boardId", boardId)
                    startActivity(intent)
                }
            }

            //뒤로가기
            binding.missionCertBackBtn.setOnClickListener {
                finish()
            }

            //수다 터치
            binding.missionCertTalkTxt.setOnClickListener {
                finish()
            }
        }

        //todo:인증 이미지시 포스팅 이후, 뷰 업데이트


        //미션 인증 사진 화면 프레그먼트 설정 + 미션 인증 화면 설정
        private fun setMissionCert(author: String, day: Int, mainMissionId: Long) {

            val retrofitManager = MissionCertRetrofitManager.getInstance(this)
            retrofitManager.mission(author, day, mainMissionId) { response ->
                Log.d("setMissionCertFrag 미션 인증 날짜 확인", day.toString())
                if (response.isSuccess == "true") {

                    if (response.result != null) {

                        //todo: 지워
                        if (response.result.missionProofImages.isNotEmpty()) {
                            //로그
                            Log.d(
                                "MissionProofImages List 확인",
                                day.toString() + " : " + response.result.missionProofImages[0].filePath
                            )
                        } else {
                            Log.d("MissionProofImages List 빔", " 빔")
                        }

                        Log.d("미션 인증 API", response.result.mainMissionName)
                        Log.d("미션 인증 API", response.result.rank[1].userName)

                        //미션 인증 엑티비티 뷰 설정
                        binding.missionCertMissionNameTxt.text = response.result.mainMissionName
                        binding.missionCertDdayTxt.text = response.result.dday

                        if (!response.result.rank.isNullOrEmpty()) {
                            binding.missionCert2ndNameTxt.text = response.result.rank[0].userName
                            binding.missionCert1stNameTxt.text = response.result.rank[1].userName
                            binding.missionCert3rdNameTxt.text = response.result.rank[2].userName
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



