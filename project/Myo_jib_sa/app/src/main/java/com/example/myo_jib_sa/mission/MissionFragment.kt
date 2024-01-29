package com.example.myo_jib_sa.mission

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.databinding.FragmentMissionBinding
import com.example.myo_jib_sa.databinding.ToastMissionReportBinding
import com.example.myo_jib_sa.mission.adapter.MissionRVAdapter
import com.example.myo_jib_sa.mission.api.Mission
import com.example.myo_jib_sa.mission.api.MissionCategoryListResponse
import com.example.myo_jib_sa.mission.api.MissionCategoryListResult
import com.example.myo_jib_sa.mission.api.MissionListResponse
import com.example.myo_jib_sa.mission.api.MissionAPI
import com.example.myo_jib_sa.mission.dialog.MissionDetailDialogFragment
import com.example.myo_jib_sa.mission.dialog.MissionReportDialogFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MissionFragment : Fragment() {
    private lateinit var binding:FragmentMissionBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var missionRVAdapter: MissionRVAdapter
    private lateinit var missionList: List<Mission>

    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false


    private val retrofit: MissionAPI = sRetrofit.create(MissionAPI::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMissionBinding.inflate(inflater, container, false)

        setMissionList()

        // NestedScrollView 스크롤 리스너 설정
        binding.missionScroll.setOnScrollChangeListener {
                v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (v != null) {
                if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                    if (!isLoading && !isLastPage) {
                        currentPage += 1
                        getMissionListAPI(currentPage)
                    }
                }
            }
        }

        //floating 버튼 설정
        binding.newMissionFloatingBtn.setOnClickListener{
            startActivity(Intent(activity, MissionCreateActivity::class.java))
        }

        return binding.root
    }


    private fun setMissionList() {
        super.onResume()
        recyclerView = binding.missionMissionRecycler
        missionList = emptyList()
        // 전체 리스트 탭
        binding.missionCategoryTl.addTab(binding.missionCategoryTl.newTab().setText("전체").setId(0))

        // 카테고리 리스트 조회 api 호출
        getMissionCategoryListApi()

        // 미션 리스트 조회 api 호출
        getMissionListAPI(0)
    }

    // 미션 리스트 조회
    private fun getMissionListAPI(page: Int) {
        isLoading = true // 데이터 로딩 시작

        retrofit.getMissionList(page).enqueue(object : Callback<MissionListResponse> {
            override fun onResponse(call: Call<MissionListResponse>, response: Response<MissionListResponse>) {
                isLoading = false // 데이터 로딩 완료

                if (response.isSuccessful) {
                    val newItems = response.body()?.result ?: emptyList()
                    if (newItems.isNotEmpty()) {
                        missionList = missionList + newItems
                        setMissionAdapter(missionList)
                        missionRVAdapter.notifyDataSetChanged()
                    } else {
                        isLastPage = true
                    }
                }
            }

            override fun onFailure(call: Call<MissionListResponse>, t: Throwable) {
                isLoading = false // 네트워크 오류 등으로 인한 로딩 실패
                Log.d("getMissionListAPI Fail", t.toString())
            }
        })
    }


    // 미션 리스트 리사이클러뷰 어댑터 구성
    private fun setMissionAdapter(dataList: List<Mission>) {
        if (!isAdded) {
            return
        }
        missionRVAdapter = MissionRVAdapter(
            dataList,
            onClickListener = object : MissionRVAdapter.OnClickListener {
                override fun onReportClick(reportItem: Mission) {
                    showReportDialog(reportItem)
                }

                override fun onItemClick(detailItem: Mission) {
                    showDetailDialog(detailItem)
                }
            }
        )
        recyclerView.adapter = missionRVAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // ItemTouchHelper.Callback 을 리사이클러뷰와 연결
        val swipeHelper = SwipeHelper().apply {
            setClamp(83f)
        }
        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    // 미션 카테고리 리스트 조회
    private fun getMissionCategoryListApi(){
        retrofit.getCategoryList().enqueue(object : Callback<MissionCategoryListResponse> {
            override fun onResponse(call: Call<MissionCategoryListResponse>, response: Response<MissionCategoryListResponse>) {
                if (response.isSuccessful) {
                    val categoryList = response.body()?.result ?: emptyList()
                    Log.d("getMissionCategoryListApi",response.body().toString())
                    // 카테고리 리스트로 탭 레이아웃 구성
                    setCategoryTabs(categoryList)
                } else {
                    // API 요청 실패 처리
                }
            }

            override fun onFailure(call: Call<MissionCategoryListResponse>, t: Throwable) {
                // 네트워크 등의 문제로 API 요청이 실패한 경우 처리
            }
        })
    }

    // 탭 레이아웃 구성
    private fun setCategoryTabs(categoryList: List<MissionCategoryListResult>) {
        with(binding) {
            // 카테고리 리스트 탭
            categoryList.forEachIndexed { _, category ->
                missionCategoryTl.addTab(
                    missionCategoryTl.newTab()
                        .setText(category.title)
                        .setId(category.id.toInt())
                )
            }

            missionCategoryTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val selectedCategoryId = when (tab.id) {
                        0 -> 0 // 전체 카테고리
                        else -> categoryList[tab.position - 1].id.toInt() // 해당 카테고리의 id를 가져옴
                    }

                    missionRVAdapter.updateDataByCategory(missionList, selectedCategoryId)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    // 신고 다이얼로그
    private fun showReportDialog(reportItem: Mission) {
        val reportDialog = MissionReportDialogFragment(reportItem)

        reportDialog.setReportDialogListener(object : MissionReportDialogFragment.ReportDialogListener {
            override fun onReportSubmitted(message: String) {
                Log.d("onReportSubmitted", "$message")
                // 뷰 바인딩을 사용하여 커스텀 레이아웃을 인플레이트합니다.
                val snackbarBinding = ToastMissionReportBinding.inflate(layoutInflater)
                snackbarBinding.toastMissionReportTxt.text = message

                // 스낵바 생성 및 설정
                val snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_SHORT).apply {
                    animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                    (view as Snackbar.SnackbarLayout).apply {
                        setBackgroundColor(Color.TRANSPARENT)
                        addView(snackbarBinding.root)
                        translationY = -70.dpToPx().toFloat()
                        elevation = 0f
                    }
                }

                // 스낵바 표시
                snackbar.show()
                reportDialog.dismiss()
            }
        })

        Log.d("showReportDialog","report ID: ${reportItem.missionId}")
        reportDialog.show(requireActivity().supportFragmentManager, "mission_report_dialog")
    }

    // 상세 다이얼로그
    private fun showDetailDialog(detailItem: Mission) {
        val detailDialog = MissionDetailDialogFragment(detailItem, requireContext())
        Log.d("showDetailDialog","detail ID: {$detailItem.id.toString()}")
        detailDialog.show(requireActivity().supportFragmentManager, "mission_detail_dialog")
    }

    private fun Int.dpToPx(): Int = (this * requireContext().resources.displayMetrics.density).toInt()
}