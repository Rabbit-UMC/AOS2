package com.example.myo_jib_sa.community.missionCert

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myo_jib_sa.community.ManagerPageActivity
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.communityHome.CommunityHomeManager
import com.example.myo_jib_sa.community.Retrofit.communityHome.MainMission
import com.example.myo_jib_sa.community.Retrofit.communityHome.PopularArticle
import com.example.myo_jib_sa.community.Retrofit.missionCert.MissionCertRetrofitManager
import com.example.myo_jib_sa.community.Retrofit.missionCert.MissionProofImages
import com.example.myo_jib_sa.community.adapter.MissionCertViewpagerAdapter
import com.example.myo_jib_sa.databinding.ActivityMissionCertificationBinding

class MissionCertificationActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMissionCertificationBinding
    val mAdapter=MissionCertViewpagerAdapter(this)
    var missionId:Long=0
    var boardId:Int=0
    var testDay:Int=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionCertificationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //todo: 인증 이미지시 포스팅 이후, 뷰 업데이트 (라이프사이클)
        missionId=intent.getLongExtra("missionId",0)
        boardId=intent.getIntExtra("boardId", 0)
        testDay=5 //todo: 미션 몇일차 인지 어딘가에서 알아와야함 일단 teatday 사용


        //게시판 이름
        when(boardId){
            Constance.ART_ID-> {
                binding.missionCertBoardNameTxt.text="예술 게시판"
            }
            Constance.FREE_ID-> {
                binding.missionCertBoardNameTxt.text="자유 게시판"
            }
            Constance.EXERCISE_ID-> {
                binding.missionCertBoardNameTxt.text="운동 게시판"
            }
        }

        //뷰페이져 어댑터 연결
        binding.missionCertVpr2.adapter=mAdapter
        for (i in 1..testDay){
            setMissionCertFrag(Constance.jwt, i, missionId)
        }

        //인증 사진 올리기 엑티비티로 이동
        binding.MissionCertPostingBtn.setOnClickListener{
            val intent= Intent(this, MissionCertificationWriteActivity::class.java)
            intent.putExtra("boardID", boardId)
            startActivity(intent)
        }
        binding.missionCertTalkTxt.setOnClickListener {
            val intent= Intent(this, MissionCertificationWriteActivity::class.java)
            intent.putExtra("boardID", boardId)
            startActivity(intent)
        }

        //관리자 페이지 이동
        binding.missionCertBoardNameTxt.setOnClickListener {
            // TextView 클릭될 시 할 코드작성 todo: 주석 제거 하기
            if(true /*hostId==Constance.USER_ID*/){
                val intent=Intent(this, ManagerPageActivity::class.java)
                intent.putExtra("boardId", boardId)
                startActivity(intent)
            }
        }
    }

    //인증 이미지시 포스팅 이후, 뷰 업데이트
    override fun onResume() {
        super.onResume()
        //뷰페이져 어댑터 연결
        binding.missionCertVpr2.adapter=mAdapter
        for (i in 1..testDay){
            setMissionCertFrag(Constance.jwt, i, missionId)
        }

    }



    //미션 인증 사진 화면 프레그먼트 설정 + 미션 인증 화면 설정
    private fun setMissionCertFrag(author:String, day:Int, mainMissionId:Long){
        val retrofitManager = MissionCertRetrofitManager.getInstance(this)
        retrofitManager.mission(author, day, mainMissionId){response ->
            if(response.isSuccess=="true"){
                val dataList:List<MissionProofImages> = response.result.missionProofImages
                if(dataList?.isNotEmpty() == true){

                    //로그
                    Log.d("MissionProofImages List 확인", dataList[0].filePath)

                    //어댑터에 dataList 추가
                    mAdapter.addData(response)

                    //미션 인증 엑티비티 뷰 설정
                    binding.missionCertMissionNameTxt.text=response.result.mainMissionName
                    binding.missionCertMissionContentTxt.text=response.result.mainMissionContent
                    binding.missionCertDdayTxt.text=response.result.dday
                    binding.missionCert2ndNameTxt.text=response.result.rank[0].userName
                    binding.missionCert1stNameTxt.text=response.result.rank[1].userName
                    binding.missionCert3rdNameTxt.text=response.result.rank[2].userName

                }else{
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