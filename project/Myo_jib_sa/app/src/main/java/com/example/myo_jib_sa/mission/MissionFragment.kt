package com.example.myo_jib_sa.mission

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.databinding.FragmentMissionBinding
import com.example.myo_jib_sa.databinding.ToastMissionBinding
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
    private lateinit var missionRVAdapter: MissionRVAdapter
    private lateinit var missionList: List<Mission>
    private var categoryList = emptyList<MissionCategoryListResult>()

    private var currentPage = 0
    private var isLoading = false
    private var isLastPage = false

    // ActivityResultLauncher 초기화
    private val missionCreateLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringExtra("resultMessage")?.let { message ->
                val isSuccess = result.data?.getBooleanExtra("isSuccess", false) ?: false
                if (isSuccess) {
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                    showSnackbar("작성하신 일반 미션이 저장되었어요!",
                        R.drawable.ic_schedule_create_check,
                        binding.root)
                }
            }
        }
    }

    private val retrofit: MissionAPI = sRetrofit.create(MissionAPI::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMissionBinding.inflate(inflater, container, false)

        initView()

        //floating 버튼 설정
        binding.newMissionFloatingBtn.setOnClickListener{
            missionCreateLauncher.launch(Intent(requireContext(), MissionCreateActivity::class.java))
        }

        return binding.root
    }

    private fun initView() {
        setRecyclerView()
        setMissionCategoryList()
        setScrollListener()
        getMissionList(0, currentPage)
    }
    // 리사이클러뷰 어댑터 설정
    private fun setRecyclerView() {
        binding.missionMissionRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MissionRVAdapter(emptyList(), object : MissionRVAdapter.OnClickListener {
                override fun onReportClick(reportItem: Mission) {
                    showReportDialog(reportItem)
                }

                override fun onItemClick(detailItem: Mission) {
                    showDetailDialog(detailItem)
                }
            }).also { missionRVAdapter = it }
        }

        ItemTouchHelper(SwipeHelper().apply{ setClamp(83f) }).attachToRecyclerView(binding.missionMissionRecycler)

    }

    // 미션 카테고리 리스트 조회
    private fun setMissionCategoryList(){
        retrofit.getCategoryList().enqueue(object : Callback<MissionCategoryListResponse> {
            override fun onResponse(call: Call<MissionCategoryListResponse>, response: Response<MissionCategoryListResponse>) {
                if (response.isSuccessful) {
                    categoryList = response.body()?.result ?: emptyList()
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
    private fun setCategoryTabs(categories: List<MissionCategoryListResult>) {
        binding.missionCategoryTl.apply {
            addTab(newTab().setText("전체").setId(0))
            categories.forEach { category ->
                addTab(newTab().setText(category.title).setId(category.id.toInt()))
            }

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    currentPage = 0
                    isLastPage = false
                    val categoryId = if (tab.position == 0) 0 else categories[tab.position - 1].id.toInt()
                    getMissionList(categoryId, currentPage)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    // 미션 리스트 조회
    private fun getMissionList(categoryId: Int, page: Int) {
        isLoading = true
        val call = if (categoryId == 0) retrofit.getMissionList(page) else retrofit.getMissionListByCategory(categoryId, page)
        call.enqueue(object : Callback<MissionListResponse> {
            override fun onResponse(call: Call<MissionListResponse>, response: Response<MissionListResponse>) {
                if (response.body()?.isSuccess == true) {
                    updateMissionList(response.body()?.result ?: emptyList(), page == 0)
                } else isLastPage = true
                isLoading = false
            }

            override fun onFailure(call: Call<MissionListResponse>, t: Throwable) {
                Log.e("MissionFragment", "Failed to load missions", t)
                isLoading = false
            }
        })
    }

    // 리사이클러뷰 아이템 업데이트
    private fun updateMissionList(newMissions: List<Mission>, isReset: Boolean) {
        if (isReset) missionList = newMissions
        else missionList += newMissions
        missionRVAdapter.updateData(missionList)
        isLastPage = newMissions.isEmpty()
    }

    // 스크롤 리스너
    private fun setScrollListener() {
        binding.missionScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            // 페이징
            if (!isLoading && !isLastPage && scrollY > oldScrollY) {
                val view = binding.missionScroll.getChildAt(binding.missionScroll.childCount - 1)
                val diff = (view.bottom - (binding.missionScroll.height + binding.missionScroll.scrollY))
                if (diff == 0) {
                    val selectedTab = binding.missionCategoryTl.getTabAt(binding.missionCategoryTl.selectedTabPosition)
                    val categoryId = if (selectedTab?.position == 0) 0 else categoryList[selectedTab!!.position - 1].id.toInt()
                    getMissionList(categoryId, ++currentPage)
                }
            }

            // 탭 레이아웃 고정
            val stickyOffset = 20.dpToPx()

            if (scrollY > binding.missionCategoryTl.top - stickyOffset) {
                // 스크롤 위치가 탭 레이아웃의 상단 위치를 넘어설 때
                val offset = scrollY - (binding.missionCategoryTl.top - stickyOffset)
                binding.missionCategoryTl.translationY = offset.toFloat()
            } else {
                // 그렇지 않으면 기본 위치로
                binding.missionCategoryTl.translationY = 0f
            }
        })
    }
    // 신고 다이얼로그
    private fun showReportDialog(reportItem: Mission) {
        val reportDialog = MissionReportDialogFragment(reportItem)
        reportDialog.setReportDialogListener(object : MissionReportDialogFragment.ReportDialogListener {
            override fun onReportSubmitted(message: String, isSuccess: Boolean) {
                Log.d("onReportSubmitted", message)
                val iconResId = if (isSuccess) R.drawable.ic_report_toast_check else R.drawable.ic_toast_fail
                showSnackbar(message, iconResId, binding.root)
                reportDialog.dismiss()
            }
        })
        Log.d("showReportDialog","report ID: ${reportItem.missionId}")
        reportDialog.show(requireActivity().supportFragmentManager, "mission_report_dialog")
    }

    // 상세 다이얼로그
    private fun showDetailDialog(detailItem: Mission) {
        val detailDialog = MissionDetailDialogFragment(detailItem, requireContext())
        Log.d("showDetailDialog","detail ID: ${detailItem.missionId}")

        detailDialog.setDetailDialogListener(object : MissionDetailDialogFragment.DetailDialogListener {
            override fun onMissionWith(message: String, isSuccess: Boolean) {
                val iconResId = if (isSuccess) R.drawable.ic_schedule_create_check else R.drawable.ic_toast_fail

                showSnackbar(
                    message, iconResId,
                    if (isSuccess) binding.root else detailDialog.view?.rootView ?: binding.root,
                    isSuccess
                )

                if (isSuccess) detailDialog.dismiss()
            }
        })
        detailDialog.show(requireActivity().supportFragmentManager, "mission_detail_dialog")
    }

    // 스낵바 생성
    private fun showSnackbar(message: String, iconResId: Int, rootView: View, isSuccess: Boolean? = null) {
        val snackBarBinding = ToastMissionBinding.inflate(layoutInflater)
        snackBarBinding.toastMissionReportTxt.text = message
        snackBarBinding.toastMissionReportIv.setImageResource(iconResId)

        val translationYValue = when (isSuccess) {
            true -> -70.dpToPx().toFloat()  // 상세 다이얼로그 같이하기 성공했을 때
            false -> 0f                     // 실패했을 때
            else -> -70.dpToPx().toFloat()  // 신고 다이얼로그
        }

        val snackBar = Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT).apply {
            animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
            (view as Snackbar.SnackbarLayout).apply {
                setBackgroundColor(Color.TRANSPARENT)
                addView(snackBarBinding.root, 0)
                translationY = translationYValue
                elevation = 0f
            }
        }
        snackBar.show()
    }

    private fun Int.dpToPx(): Int = (this * requireContext().resources.displayMetrics.density).toInt()
}