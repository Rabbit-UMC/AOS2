package com.example.myo_jib_sa.community

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Retrofit.communityHome.CommunityHomeManager
import com.example.myo_jib_sa.community.Retrofit.communityHome.MainMission
import com.example.myo_jib_sa.community.Retrofit.communityHome.PopularTopic
import com.example.myo_jib_sa.community.adapter.BannerViewpagerAdapter
import com.example.myo_jib_sa.community.adapter.HomeMissionAdapter
import com.example.myo_jib_sa.community.adapter.HomePostAdapter
import com.example.myo_jib_sa.community.banner.Banner1Fragment
import com.example.myo_jib_sa.community.banner.Banner2Fragment
import com.example.myo_jib_sa.community.banner.Banner3Fragment
import com.example.myo_jib_sa.databinding.ActivityBoardArtBinding
import com.example.myo_jib_sa.databinding.FragmentCommunityBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.sql.Timestamp

class CommunityFragment : Fragment() {

    private lateinit var binding:FragmentCommunityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCommunityBinding.inflate(inflater, container, false)

        //터치시 게시판 이동
        binding.communityBoardArt.setOnClickListener {
            val intent=Intent(requireActivity(), BoardArtActivity::class.java )
            startActivity(intent)
        }
        binding.communityBoardExcs.setOnClickListener {
            val intent=Intent(requireActivity(), BoardExerciseActivity::class.java )
            startActivity(intent)
        }
        binding.communityBoardFree.setOnClickListener {
            val intent=Intent(requireActivity(), BoardFreeActivity::class.java )
            startActivity(intent)
        }

        //더보기 터치 시 이동 구현 필요
       /* binding.homePulsTxt.setOnClickListener {
            //베스트 게시물 엑티비티 이동
        }*/

        //서버 완성 후 getMissionData(author:String)로 대체 해야함
        //리사이클러 뷰 test 코드
        val mList = listOf(
            MainMission("미션명 입니다", "D-3", "", "운동 게시판",2)
            ,MainMission("미션명 입니다222", "D-3", "", "예술 게시판",1)
            ,MainMission("미션명 입니다333", "D-3", "", "자유 게시판",11)
        )

        val pList= listOf(
            PopularTopic("게시물 제목", 11, 11, 2),
            PopularTopic("게시물 제목 22", 22, 22, 3),
            PopularTopic("게시물 제목 33", 33, 33, 4)
        )

        linkMrecyclr(mList)
        linkePrecyclr(pList)

        //배너 연결
        val vAdapter=BannerViewpagerAdapter(this)
        vAdapter.addFragment(Banner1Fragment())
        vAdapter.addFragment(Banner2Fragment())
        vAdapter.addFragment(Banner3Fragment())

        //아이콘 색상 설정
        val selectedColor = R.color.community_selected_color
        val unselectedColor = R.color.community_unselected_color
        customizeTabIconColors(binding.homeBannerTab, selectedColor, unselectedColor)

        // 기본(default) 화면 설정
        val defaultPosition = 0 // 기본 화면의 position을 설정합니다.
        binding.homeBannerVpager.setCurrentItem(defaultPosition, false) // 기본 화면으로 ViewPager를 설정
        binding.homeBannerTab.selectTab(binding.homeBannerTab.getTabAt(defaultPosition)) // 기본 화면으로 Tab을 설정

        binding.homeBannerVpager.adapter=vAdapter
        val tabLayoutMediator = TabLayoutMediator(binding.homeBannerTab, binding.homeBannerVpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.setIcon(R.drawable.ic_dot)
                }
                1 -> {
                    tab.setIcon(R.drawable.ic_dot)
                }
                2 -> {
                    tab.setIcon(R.drawable.ic_dot)
                }
            }
        }.attach()




        return binding.root
    }



    //API 연결, 리사이클러뷰 띄우기
    private fun getMissionData(author:String, binding:FragmentCommunityBinding){
        val retrofitManager =CommunityHomeManager.getInstance(requireContext())
        retrofitManager.home(author){homeResponse ->
            if(homeResponse.isSuccess=="TRUE"){
                val missionList:List<MainMission> = homeResponse.mainMission
                val postList:List<PopularTopic> = homeResponse.popularArticle
                if(missionList.isNotEmpty() || postList.isNotEmpty()){

                    //로그
                    Log.d("MissionList 확인", missionList[0].mainMissionName)
                    Log.d("MissionList 확인", missionList[0].catagoryName)
                    Log.d("hMissionList 확인", missionList[0].catagoryImage)
                    Log.d("MissionList 확인", missionList[0].dDay.toString())

                    Log.d("PostList 확인", postList[0].topicTitle)
                    Log.d("PostList 확인", postList[0].commentCount.toString())
                    Log.d("PostList 확인", postList[0].likeCount.toString())


                    //리사이클러뷰 연결
                    //linkMrecyclr(missionList)
                    //linkePrecyclr(postList)


                }else{
                    Log.d("리사이클러뷰 어댑터로 리스트 전달", "List가 비었다네요")
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
    private fun linkMrecyclr(missionList:List<MainMission>){
        Log.d("리사이클러뷰","linkMrecyclr(mList) 시작")
        Log.d("리사이클러뷰","${missionList.size}")
        //미션
        val Madapter = HomeMissionAdapter(requireContext(),missionList)
        val MlayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.homeMissionRecyclr.layoutManager = MlayoutManager
        Log.d("리사이클러뷰","binding.homeMissionRecyclr.layoutManager 시작")
        binding.homeMissionRecyclr.adapter = Madapter
        Log.d("리사이클러뷰","binding.homeMissionRecyclr.adapter 시작")

        Madapter.setItemSpacing(binding.homeMissionRecyclr, 15)
    }

    //베스트 게시글 리사이클러뷰, 어댑터 연결
    private fun linkePrecyclr(postList:List<PopularTopic>){

        val Padapter = HomePostAdapter(requireContext(),postList)
        val PlayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.homeBestPostRecyclr.layoutManager = PlayoutManager
        binding.homeBestPostRecyclr.adapter = Padapter

        Padapter.setItemSpacing(binding.homeBestPostRecyclr, 15)
    }


    //5초 마다 페이지 넘기기
    // 5초 마다 페이지 넘기기
    private var currentPage = 0
    private lateinit var thread: Thread

    private val handler = Handler(Looper.getMainLooper()) {
        setPage()
        true
    }

    inner class PagerRunnable : Runnable {
        override fun run() {
            while (true) {
                try {
                    Thread.sleep(5000)
                    handler.sendEmptyMessage(0)
                } catch (e: InterruptedException) {
                    Log.d("interrupt", "interrupt 발생")
                }
            }
        }
    }

    private fun setPage() {
        if (currentPage == 3)
            currentPage = 0
        currentPage += 1
        binding.homeBannerVpager.setCurrentItem(currentPage, true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        thread = Thread(PagerRunnable())
        thread.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        thread.interrupt()
        handler.removeCallbacksAndMessages(null)
    }


    //탭 선택 리스너, (색상)
    fun customizeTabIconColors(tabLayout: TabLayout, selectedColor: Int, unselectedColor: Int) {
        val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabIconColor = ContextCompat.getColor(tabLayout.context, selectedColor)
                val colorFilter = PorterDuffColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
                tab.icon?.colorFilter = colorFilter
            }


            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabIconColor = ContextCompat.getColor(tabLayout.context, unselectedColor)
                val colorFilter = PorterDuffColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
                tab.icon?.colorFilter = colorFilter
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // 선택된 탭이 다시 선택된 경우 추가 동작을 수행할 수 있습니다.
            }
        }

        tabLayout.addOnTabSelectedListener(tabSelectedListener)
    }
}