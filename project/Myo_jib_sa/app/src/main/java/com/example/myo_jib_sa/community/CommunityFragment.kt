package com.example.myo_jib_sa.community

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.communityHome.CommunityHomeManager
import com.example.myo_jib_sa.community.Retrofit.communityHome.MainMission
import com.example.myo_jib_sa.community.Retrofit.communityHome.PopularArticle
import com.example.myo_jib_sa.community.adapter.HomeMissionAdapter
import com.example.myo_jib_sa.community.adapter.HomePostAdapter
import com.example.myo_jib_sa.databinding.FragmentCommunityBinding

class CommunityFragment : Fragment() {

    private lateinit var binding: FragmentCommunityBinding
    private lateinit var retrofitManager: CommunityHomeManager


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


        return binding.root
    }

    //다시 돌아올 때 뷰 업데이트
    override fun onResume() {
        super.onResume()
        retrofitManager = CommunityHomeManager.getInstance(requireContext())
        Constance.jwt?.let { getMissionData(it, requireContext()) }
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