package com.example.myo_jib_sa.mission

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
import com.example.myo_jib_sa.mission.API.Home
import com.example.myo_jib_sa.mission.API.MissionHomeResponse
import com.example.myo_jib_sa.mission.API.MissionITFC
import com.example.myo_jib_sa.mission.Dialog.MissionDetailDialogFragment
import com.example.myo_jib_sa.mission.Dialog.MissionReportDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MissionFragment : Fragment() {

    private lateinit var binding:FragmentMissionBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var missionAdapter: MissionAdapter
    // private lateinit var categoryId:Int

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
        val retrofit = RetrofitInstance.getInstance().create(MissionITFC::class.java)

        retrofit.MissionHome().enqueue(object : Callback<MissionHomeResponse> {
            override fun onResponse(call: Call<MissionHomeResponse>, response: Response<MissionHomeResponse>) {
                if (response.isSuccessful) {
                    val missionHomeResponse = response.body()
                    val dataList = missionHomeResponse?.result ?: emptyList()
                    //categoryId=dataList[0].categoryId

                    /* Log.d("home", "MissionHomeResponse data count: ${dataList.size}")
                     for (item in dataList) {
                         Log.d("home", "Mission id: ${item.missionId}")
                     }*/

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



        //floating 버튼 설정
        binding.newMissionFloatingBtn.setOnClickListener{

            val intent = Intent(activity, MissionWriteMissionActivity::class.java)
            startActivity(intent)
        }

        //자유
        binding.missionBoardFree.setOnClickListener{

        }
        //운동
        binding.missionBoardExcs.setOnClickListener{

        }
        //예술
        binding.missionBoardArt.setOnClickListener{

        }


    }

    fun setUpMissionAdapter(dataList: List<Home>) {
        missionAdapter = MissionAdapter(
            requireContext(),
            dataList,
            onItemLongClickListener = object : MissionAdapter.OnItemLongClickListener {
                override fun onItemLongClick(reportItem: Home) {
                    showReportDialog(reportItem)
                }
            },
            onItemClickListener = object : MissionAdapter.OnItemClickListener {
                override fun onItemClick(detailItem: Home) {
                    showDetailDialog(detailItem)
                }
            }
        )

        recyclerView.adapter = missionAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 아이템 간격 설정 (옵션)
        missionAdapter.setItemSpacing(recyclerView, 15)
    }


    private fun showReportDialog(reportItem: Home) {
        val reportDialog = MissionReportDialogFragment(reportItem)
        Log.d("home","Mreport ID: {$reportItem.id.toString()}")
        reportDialog.show(requireActivity().supportFragmentManager, "mission_report_dialog")

    }

    private fun showDetailDialog(detailItem: Home) {
        val detailDialog = MissionDetailDialogFragment(detailItem)
        Log.d("home","Mdetail ID: {$detailItem.id.toString()}")
        detailDialog.show(requireActivity().supportFragmentManager, "mission_detail_dialog")
    }



}
