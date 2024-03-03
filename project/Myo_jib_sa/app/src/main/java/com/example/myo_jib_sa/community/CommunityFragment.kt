package com.example.myo_jib_sa.community

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.BuildConfig
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.manager.ManagerPageActivity
import com.example.myo_jib_sa.community.api.communityHome.CommunityHomeManager
import com.example.myo_jib_sa.community.api.communityHome.MainMission
import com.example.myo_jib_sa.community.api.communityHome.PopularArticle
import com.example.myo_jib_sa.community.adapter.HomeMissionAdapter
import com.example.myo_jib_sa.community.adapter.HomePostAdapter
import com.example.myo_jib_sa.databinding.FragmentCommunityBinding
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

class CommunityFragment : Fragment() {

    private lateinit var binding: FragmentCommunityBinding
    private lateinit var retrofitManager: CommunityHomeManager
    private var isFabOpen = false

    private var adLoader: AdLoader? = null //광고를 불러올 adLoader 객체
    val AD_UNIT_ID = BuildConfig.AD_UNIT_ID


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityBinding.inflate(inflater, container, false)
//
        retrofitManager = CommunityHomeManager.getInstance(requireContext())

        //애드몹 광고 표시
        createAd()
        adLoader?.loadAd(AdRequest.Builder().build())

        //터치시 게시판 이동
        moveBoard()

        //더보기 터치 시 이동
        binding.homePulsTxt.setOnClickListener {
            val intent = Intent(requireActivity(), BoardActivity::class.java)
            intent.putExtra("isBest", true)
            startActivity(intent)
        }

        //api 연결, 뷰 띄우기
        getHomeData(requireContext())

        return binding.root
    }

    //다시 돌아올 때 뷰 업데이트
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
        retrofitManager = CommunityHomeManager.getInstance(requireContext())
       getHomeData(requireContext())
    }

    //플로팅 버튼
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setFABClickEvent(userHostCategory:List<Long>, mainMissionList:List<MainMission>) {

        if(userHostCategory.isEmpty()){
            binding.myoZipMainBtn.hide() //관리자가 아닌 유저의 경우
        }
        Log.d("내가 묘집사인 카테고리", userHostCategory.toString())
        binding.myoZip1Btn.hide()
        binding.myoZip2Btn.hide()
        binding.myoZip3Btn.hide()

        for (i in userHostCategory.indices) {
            val fab = when (i) {
                0 -> binding.myoZip1Btn
                1 -> binding.myoZip2Btn
                2 -> binding.myoZip3Btn
                else -> null
            }

            fab?.apply {
                show()

                when (userHostCategory[i]) {
                    Constance.ART_ID -> {
                        setImageResource(R.drawable.ic_floating_art)
                        setOnClickListener {
                            //todo: 아래 부분 묘듈화 시키기
                            val intent = Intent(requireContext(), ManagerPageActivity::class.java)
                            intent.putExtra("boardId", userHostCategory[i])

                            mainMissionList.stream().filter { mission ->
                                mission.missionCategoryId == userHostCategory[i]
                            }.findFirst().ifPresent { mission ->
                                intent.putExtra("missionId", mission.mainMissionId)
                                Log.d("관리자 페이지로 전달할 미션 아이디", mission.mainMissionId.toString())
                            }

                            startActivity(intent)
                        }
                    }
                    Constance.FREE_ID -> {
                        setImageResource(R.drawable.ic_floating_free)
                        setOnClickListener {
                            val intent = Intent(requireContext(), ManagerPageActivity::class.java)
                            intent.putExtra("boardId", userHostCategory[i])

                            mainMissionList.stream().filter { mission ->
                                mission.missionCategoryId == userHostCategory[i]
                            }.findFirst().ifPresent { mission ->
                                intent.putExtra("missionId", mission.mainMissionId)
                                Log.d("관리자 페이지로 전달할 미션 아이디", mission.mainMissionId.toString())
                            }

                            startActivity(intent)
                        }
                    }
                    Constance.EXERCISE_ID -> {
                        setImageResource(R.drawable.ic_floating_exercise)
                        setOnClickListener {
                            val intent = Intent(requireContext(), ManagerPageActivity::class.java)
                            intent.putExtra("boardId", userHostCategory[i])

                            mainMissionList.stream().filter { mission ->
                                mission.missionCategoryId == userHostCategory[i]
                            }.findFirst().ifPresent { mission ->
                                intent.putExtra("missionId", mission.mainMissionId)
                                Log.d("관리자 페이지로 전달할 미션 아이디", mission.mainMissionId.toString())
                            }

                            startActivity(intent)
                        }
                    }
                }
            }
        }


        // 플로팅 버튼 클릭시 애니메이션 동작 기능
        binding.myoZipMainBtn.setOnClickListener {
            toggleFab()
        }
    }

    //플로팅 버튼 꺼내기
    private fun toggleFab() {

        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션
        if (isFabOpen) {
            ObjectAnimator.ofFloat(binding.myoZip3Btn, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.myoZip2Btn, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.myoZip1Btn, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.myoZipMainBtn, View.ROTATION, 0f, 0f).apply { start() }
        } else { // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션
            ObjectAnimator.ofFloat(binding.myoZip3Btn, "translationY", -540f).apply { start() }
            ObjectAnimator.ofFloat(binding.myoZip2Btn, "translationY", -360f).apply { start() }
            ObjectAnimator.ofFloat(binding.myoZip1Btn, "translationY", -180f).apply { start() }
            ObjectAnimator.ofFloat(binding.myoZipMainBtn, View.ROTATION, 0f, 0f).apply { start() }
        }

        isFabOpen = !isFabOpen

    }

    //게시판 이동
    private fun moveBoard() {
        binding.communityBoardArtLinear.setOnClickListener {
            val intent = Intent(requireActivity(), BoardActivity::class.java)
            intent.putExtra("boardId", Constance.ART_ID)
            startActivity(intent)
        }
        binding.communityBoardExerciseLinear.setOnClickListener {
            val intent = Intent(requireActivity(), BoardActivity::class.java)
            intent.putExtra("boardId", Constance.EXERCISE_ID)
            startActivity(intent)
        }
        binding.communityBoardFreeLinear.setOnClickListener {
            val intent = Intent(requireActivity(), BoardActivity::class.java)
            intent.putExtra("boardId", Constance.FREE_ID)
            startActivity(intent)
        }

    }


    //API 연결, 리사이클러뷰 띄우기, 플로팅 버튼 설정 (Gone)
    @RequiresApi(Build.VERSION_CODES.N)
    private fun getHomeData(context: Context) {
        retrofitManager.home() { homeResponse ->
            if (homeResponse.isSuccess) {
                val missionList: List<MainMission> = homeResponse.result.mainMission
                val postList: List<PopularArticle> = homeResponse.result.popularArticle

                setFABClickEvent(homeResponse.result.userHostCategory, homeResponse.result.mainMission)

                Log.d("내가 묘집사인 카테고리 (응답)", homeResponse.result.userHostCategory.toString())
                if (missionList.isNotEmpty()) {
                    linkMrecyclr(context, missionList)
                } else {
                    Log.d("미션 리사이클러뷰 어댑터로 리스트 전달", "MissionList가 비었다네요")
                }
                if (postList.isNotEmpty()) {
                    linkePrecyclr(context, postList)
                } else {
                    Log.d("미션 리사이클러뷰 어댑터로 리스트 전달", "PostList가 비었다네요")
                }
            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                val returnCode = homeResponse.errorCode
                val returnMsg = homeResponse.errorMessage

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

        //Madapter.setItemSpacing(binding.homeMissionRecyclr, 10)
    }

    //베스트 게시글 리사이클러뷰, 어댑터 연결
    private fun linkePrecyclr(context: Context, postList: List<PopularArticle>) {

        val Padapter = HomePostAdapter(context, postList)
        val PlayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.homeBestPostRecyclr.layoutManager = PlayoutManager
        binding.homeBestPostRecyclr.adapter = Padapter

        //Padapter.setItemSpacing(binding.homeBestPostRecyclr, 15)
    }

    //광고 생성 메소드
    private fun createAd() {
        MobileAds.initialize(requireActivity())
        adLoader = AdLoader.Builder(requireActivity(), BuildConfig.AD_UNIT_ID)//sample아이디
            .forNativeAd { ad : NativeAd ->
                val template: TemplateView = binding.myTemplate
                template.setNativeAd(ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                // Methods in the NativeAdOptions.Builder class can be
                // used here to specify individual options settings.
                .build())
            .build()
    }
}