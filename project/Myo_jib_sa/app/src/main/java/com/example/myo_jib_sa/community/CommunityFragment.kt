package com.example.myo_jib_sa.community

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.community.Retrofit.communityHome.CommunityHomeManager
import com.example.myo_jib_sa.community.Retrofit.communityHome.MainMission
import com.example.myo_jib_sa.community.Retrofit.communityHome.PopularArticle
import com.example.myo_jib_sa.community.adapter.HomeMissionAdapter
import com.example.myo_jib_sa.community.adapter.HomePostAdapter
import com.example.myo_jib_sa.databinding.FragmentCommunityBinding

class CommunityFragment : Fragment() {

    private lateinit var binding: FragmentCommunityBinding
    private lateinit var retrofitManager: CommunityHomeManager
    private var isFabOpen = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityBinding.inflate(inflater, container, false)
//
        Constance.initializeJwt(requireContext())
//
        retrofitManager = CommunityHomeManager.getInstance(requireContext())

        //터치시 게시판 이동
        moveBoard()

        //더보기 터치 시 이동
        binding.homePulsTxt.setOnClickListener {
            val intent = Intent(requireActivity(), BoardExerciseActivity::class.java)
            intent.putExtra("isBest", true)
            startActivity(intent)
        }

        //api 연결, 뷰 띄우기
        Constance.jwt?.let { getMissionData(it, requireContext()) }

        //setFABClickEvent()
        binding.myoZip3Btn.setOnClickListener {
            val intent=Intent(requireContext(),ManagerPageActivity::class.java)
            startActivity(intent)
        }
        binding.myoZip2Btn.setOnClickListener {
            val intent=Intent(requireContext(),ManagerPageActivity::class.java)
            startActivity(intent)
        }
        binding.myoZip1Btn.setOnClickListener {
            val intent=Intent(requireContext(),ManagerPageActivity::class.java)
            startActivity(intent)
        }
        binding.myoZipMainBtn.setOnClickListener {
            val intent=Intent(requireContext(),ManagerPageActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //setFABClickEvent() //플로팅 버튼 동작 이벤트
    }

    //다시 돌아올 때 뷰 업데이트
    override fun onResume() {
        super.onResume()
        retrofitManager = CommunityHomeManager.getInstance(requireContext())
        Constance.jwt?.let { getMissionData(it, requireContext()) }
    }

    //플로팅 버튼
    private fun setFABClickEvent() {
        // 플로팅 버튼 클릭시 애니메이션 동작 기능
        binding.myoZipMainBtn.setOnClickListener {
            toggleFab()
        }

        // 플로팅 버튼 클릭 이벤트
        binding.myoZip1Btn.setOnClickListener {

        }

        // 플로팅 버튼 클릭 이벤트
        binding.myoZip2Btn.setOnClickListener {

        }

        // 플로팅 버튼 클릭 이벤트
        binding.myoZip3Btn.setOnClickListener {

        }
    }

    //플로팅 버튼 꺼내기
    private fun toggleFab() {

        //todo: 관리자 권한에 따라 플로팅 버튼 숨기기 나타내기

        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션
        if (isFabOpen) {
            ObjectAnimator.ofFloat(binding.myoZip3Btn, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.myoZip2Btn, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.myoZip1Btn, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.myoZipMainBtn, View.ROTATION, 45f, 0f).apply { start() }
        } else { // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션
            ObjectAnimator.ofFloat(binding.myoZip3Btn, "translationY", -360f).apply { start() }
            ObjectAnimator.ofFloat(binding.myoZip2Btn, "translationY", -180f).apply { start() }
            ObjectAnimator.ofFloat(binding.myoZip1Btn, "translationY", -180f).apply { start() }
            ObjectAnimator.ofFloat(binding.myoZipMainBtn, View.ROTATION, 0f, 45f).apply { start() }
        }

        isFabOpen = !isFabOpen

    }

    //게시판 이동
    private fun moveBoard() {
        binding.communityBoardArt.setOnClickListener {
            val intent = Intent(requireActivity(), BoardExerciseActivity::class.java)
            intent.putExtra("boardId", Constance.ART_ID)
            startActivity(intent)
        }
        binding.communityBoardExcs.setOnClickListener {
            val intent = Intent(requireActivity(), BoardExerciseActivity::class.java)
            intent.putExtra("boardId", Constance.EXERCISE_ID)
            startActivity(intent)
        }
        binding.communityBoardFree.setOnClickListener {
            val intent = Intent(requireActivity(), BoardExerciseActivity::class.java)
            intent.putExtra("boardId", Constance.FREE_ID)
            startActivity(intent)
        }

    }


    //API 연결, 리사이클러뷰 띄우기
    private fun getMissionData(author: String, context: Context) {
        retrofitManager.home(author) { homeResponse ->
            if (homeResponse.isSuccess == "true") {
                val missionList: List<MainMission> = homeResponse.result.mainMission
                val postList: List<PopularArticle> = homeResponse.result.popularArticle
                if (missionList.isNotEmpty()) {
                    linkMrecyclr(context, missionList)
                } else {
                    Log.d("리사이클러뷰 어댑터로 리스트 전달", "MissionList가 비었다네요")
                }
                if (postList.isNotEmpty()) {
                    linkePrecyclr(context, postList)
                } else {
                    Log.d("리사이클러뷰 어댑터로 리스트 전달", "PostList가 비었다네요")
                }
            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                val returnCode = homeResponse.code
                val returnMsg = homeResponse.message

                Log.d("홈 API isSuccess가 false", "${returnCode}  ${returnMsg}")
            }


        }
    }

    //미션 리사이클러뷰, 어댑터 연결
    private fun linkMrecyclr(context: Context, missionList: List<MainMission>) {
        Log.d("리사이클러뷰", "linkMrecyclr(mList) 시작")
        Log.d("리사이클러뷰", "${missionList.size}")
        //미션
        val Madapter = HomeMissionAdapter(context, missionList)
        val MlayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.homeMissionRecyclr.layoutManager = MlayoutManager
        Log.d("리사이클러뷰", "binding.homeMissionRecyclr.layoutManager 시작")
        binding.homeMissionRecyclr.adapter = Madapter
        Log.d("리사이클러뷰", "binding.homeMissionRecyclr.adapter 시작")

        Madapter.setItemSpacing(binding.homeMissionRecyclr, 15)
    }

    //베스트 게시글 리사이클러뷰, 어댑터 연결
    private fun linkePrecyclr(context: Context, postList: List<PopularArticle>) {

        val Padapter = HomePostAdapter(context, postList)
        val PlayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.homeBestPostRecyclr.layoutManager = PlayoutManager
        binding.homeBestPostRecyclr.adapter = Padapter

        //Padapter.setItemSpacing(binding.homeBestPostRecyclr, 15)
    }
}