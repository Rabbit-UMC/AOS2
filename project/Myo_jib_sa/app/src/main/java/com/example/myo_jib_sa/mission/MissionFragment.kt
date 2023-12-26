package com.example.myo_jib_sa.mission

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.login.api.RetrofitInstance
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.spfManager
import com.example.myo_jib_sa.databinding.FragmentMissionBinding
import com.example.myo_jib_sa.mission.api.Mission
import com.example.myo_jib_sa.mission.api.MissionCategoryListResponse
import com.example.myo_jib_sa.mission.api.MissionCategoryListResult
import com.example.myo_jib_sa.mission.api.MissionListResponse
import com.example.myo_jib_sa.mission.api.MissionAPI
import com.example.myo_jib_sa.mission.Dialog.MissionDetailDialogFragment
import com.example.myo_jib_sa.mission.Dialog.MissionReportDialogFragment
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MissionFragment : Fragment() {
    private lateinit var binding:FragmentMissionBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var missionRVAdapter: MissionRVAdapter
    private lateinit var missionList: List<Mission>
    val retrofit = RetrofitInstance.getInstance().create(MissionAPI::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMissionBinding.inflate(inflater, container, false)

        spfManager.setUserToken("eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ.eyJ1c2VySWR4IjoxLCJpYXQiOjE3MDI2NjM2MTgsImV4cCI6MTcwNDEzNDg0N30.va4im_CNjnmyRRZxtPxxHc7v4b2SstL3XLebcJ1EXm0")

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        recyclerView = binding.missionMissionRecycler

        binding.missionCategoryTl.removeAllTabs()
        missionList = emptyList()

        // 전체 리스트 탭
        binding.missionCategoryTl.addTab(binding.missionCategoryTl.newTab().setText("전체").setId(0))

        // 미션 리스트 조회 api 호출
        getMissionListAPI()

        // 카테고리 리스트 조회 api 호출
        getMissionCategoryListApi()

        //floating 버튼 설정
        binding.newMissionFloatingBtn.setOnClickListener{
            startActivity(Intent(activity, MissionCreateActivity::class.java))
        }

    }

    // 미션 리스트 조회
    private fun getMissionListAPI() {
        retrofit.getMissionList().enqueue(object : Callback<MissionListResponse> {
            override fun onResponse(call: Call<MissionListResponse>, response: Response<MissionListResponse>) {
                if (response.isSuccessful) {
                    missionList = response.body()?.result ?: emptyList()
                    Log.d("getMissionListAPI isSuccess",response.body().toString())
                    // 어댑터 생성 및 리사이클러뷰에 설정
                    setMissionAdapter(missionList)
                } else {
                    // API 요청 실패 처리
                    Log.d("getMissionListAPI isFail",response.body().toString())
                }
            }

            override fun onFailure(call: Call<MissionListResponse>, t: Throwable) {
                // 네트워크 등의 문제로 API 요청이 실패한 경우 처리
                Log.d("getMissionListAPI Fail", t.toString())
            }
        })
    }

    // 미션 리스트 리사이클러뷰 어댑터 구성
    private fun setMissionAdapter(dataList: List<Mission>) {
        missionRVAdapter = MissionRVAdapter(
            dataList,
            onReportClickListener = object : MissionRVAdapter.OnReportClickListener {
                override fun onReportClick(reportItem: Mission) {
                    showReportDialog(reportItem)
                }
            },
            onItemClickListener = object : MissionRVAdapter.OnItemClickListener {
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

    private fun showReportDialog(reportItem: Mission) {
        val reportDialog = MissionReportDialogFragment(reportItem)
        Log.d("home","Mreport ID: {$reportItem.id.toString()}")
        reportDialog.show(requireActivity().supportFragmentManager, "mission_report_dialog")

    }

    private fun showDetailDialog(detailItem: Mission) {
        val detailDialog = MissionDetailDialogFragment(detailItem)
        Log.d("home","Mdetail ID: {$detailItem.id.toString()}")
        detailDialog.show(requireActivity().supportFragmentManager, "mission_detail_dialog")
    }



}