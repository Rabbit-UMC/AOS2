package com.example.myo_jib_sa.mission

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.Login.API.RetrofitInstance
import com.example.myo_jib_sa.databinding.FragmentMissionBinding
import com.example.myo_jib_sa.mission.API.MissionCategoryResponse
import com.example.myo_jib_sa.mission.API.MissionHomeResponse
import com.example.myo_jib_sa.mission.API.MissionITFC
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
    private var category:Int=1
    val retrofit = RetrofitInstance.getInstance().create(MissionITFC::class.java)
    private var decoration: RecyclerView.ItemDecoration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMissionBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        recyclerView = binding.missionMissionRecycler

        //미션 home api 호출
        //missionHomeAPI()

        // 탭 레이아웃 교체 시 리사이클러뷰 리스트 교체
        setupCategoryTabs()

        //floating 버튼 설정
        binding.newMissionFloatingBtn.setOnClickListener{
            val intent = Intent(activity, MissionWriteMissionActivity::class.java)
            startActivity(intent)
        }


    }
    // Retrofit 요청을 함수로 추출하여 재사용성 높이기
    fun missionHomeAPI() {
        retrofit.MissionHome().enqueue(object : Callback<MissionHomeResponse> {
            override fun onResponse(call: Call<MissionHomeResponse>, response: Response<MissionHomeResponse>) {
                if (response.isSuccessful) {
                    val missionHomeResponse = response.body()
                    val dataList = missionHomeResponse?.result ?: emptyList()

                    Log.d("home",missionHomeResponse.toString())
                    // 어댑터 생성 및 리사이클러뷰에 설정
                    setUpMissionAdapter(dataList)

                } else {
                    // API 요청 실패 처리
                }
            }

            override fun onFailure(call: Call<MissionHomeResponse>, t: Throwable) {
                // 네트워크 등의 문제로 API 요청이 실패한 경우 처리
            }
        })
    }


    //미션 카테고리 API 연결
    fun missionCategoryApi(category:Int){

        val sharedPreferences = requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val jwt = sharedPreferences.getString("jwt", null)

        retrofit.MissionCategory(jwt.toString(),category).enqueue(object : Callback<MissionCategoryResponse> {
            override fun onResponse(call: Call<MissionCategoryResponse>, response: Response<MissionCategoryResponse>) {
                if (response.isSuccessful) {
                    val missionCategoryResponse = response.body()
                    val cateDataList = missionCategoryResponse?.result ?: emptyList()
                    Log.d("home",missionCategoryResponse.toString())
                    // 어댑터 생성 및 리사이클러뷰에 설정
                    setUpMissionAdapter(cateDataList)

                } else {
                    // API 요청 실패 처리
                }
            }

            override fun onFailure(call: Call<MissionCategoryResponse>, t: Throwable) {
                // 네트워크 등의 문제로 API 요청이 실패한 경우 처리
            }
        })
    }


    private fun setUpMissionAdapter(dataList: List<MissionItem>) {
        decoration?.let { recyclerView.removeItemDecoration(it) }

        missionRVAdapter = MissionRVAdapter(
            requireContext(),
            dataList,
            onItemLongClickListener = object : MissionRVAdapter.OnItemLongClickListener {
                override fun onItemLongClick(reportItem: MissionItem) {
                    showReportDialog(reportItem)
                }
            },
            onItemClickListener = object : MissionRVAdapter.OnItemClickListener {
                override fun onItemClick(detailItem: MissionItem) {
                    showDetailDialog(detailItem)
                }
            }
        )

        recyclerView.adapter = missionRVAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 아이템 간격 설정 (옵션)
        decoration = MissionRVAdapter.CustomItemDecoration(15) // decoration 변수 초기화
        recyclerView.addItemDecoration(decoration as MissionRVAdapter.CustomItemDecoration)
    }

    private fun setupCategoryTabs() {
        binding.missionCategoryTl.addTab(binding.missionCategoryTl.newTab().setText("전체"))
        binding.missionCategoryTl.addTab(binding.missionCategoryTl.newTab().setText("자유"))
        binding.missionCategoryTl.addTab(binding.missionCategoryTl.newTab().setText("운동"))
        binding.missionCategoryTl.addTab(binding.missionCategoryTl.newTab().setText("예술"))

        binding.missionCategoryTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> missionRVAdapter.updateData()
                    1 -> missionRVAdapter.updateData()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }



    private fun showReportDialog(reportItem: MissionItem) {
        val reportDialog = MissionReportDialogFragment(reportItem)
        Log.d("home","Mreport ID: {$reportItem.id.toString()}")
        reportDialog.show(requireActivity().supportFragmentManager, "mission_report_dialog")

    }

    private fun showDetailDialog(detailItem: MissionItem) {
        val detailDialog = MissionDetailDialogFragment(detailItem)
        Log.d("home","Mdetail ID: {$detailItem.id.toString()}")
        detailDialog.show(requireActivity().supportFragmentManager, "mission_detail_dialog")
    }



}