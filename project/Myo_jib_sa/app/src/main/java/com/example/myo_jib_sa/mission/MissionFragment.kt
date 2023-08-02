package com.example.myo_jib_sa.mission

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.FragmentMissionBinding
import com.example.myo_jib_sa.mission.Dialog.MissionDetailDialogFragment
import com.example.myo_jib_sa.mission.Dialog.MissionReportDialogFragment

class MissionFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var missionAdapter: MissionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMissionBinding.inflate(inflater, container, false)
        recyclerView = binding.missionMissionRecycler

        // 데이터 리스트 생성 -> api연결시 api response data로 바꾸기
        val dataList = listOf(
            RecyclerMissionData("필라테스", "건강을 위하여", R.drawable.ic_mission_exercise,10,"07.10","08.01"),
            RecyclerMissionData("크로키", "함께 크로키로 해요!", R.drawable.ic_mission_art,15,"07.15","08.01"),
            RecyclerMissionData("넷플릭스 도장깨기", "넷플 시리즈 같이 봐요", R.drawable.ic_mission_free,30,"07.15","09.30")
        )

        // 어댑터 생성 및 리사이클러뷰에 설정
        val missionAdapter = MissionAdapter(
            requireContext(),
            dataList,
            onItemLongClickListener = object : MissionAdapter.OnItemLongClickListener {
                override fun onItemLongClick(item: RecyclerMissionData) {
                    showReportDialog(item)
                }
            },
            onItemClickListener = object : MissionAdapter.OnItemClickListener {
                override fun onItemClick(item: RecyclerMissionData) {
                    showDetailDialog(item)
                }
            }
        )
        recyclerView.adapter = missionAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 아이템 간격 설정 (옵션)
        missionAdapter.setItemSpacing(recyclerView,15)


        //floating 버튼 설정
        binding.newMissionFloatingBtn.setOnClickListener{

            val intent = Intent(activity, MissionWriteMissionActivity::class.java)
            startActivity(intent)
        }


        return binding.root
    }

    private fun showReportDialog(item: RecyclerMissionData) {
        val reportDialog = MissionReportDialogFragment()
        reportDialog.show(requireActivity().supportFragmentManager, "mission_report_dialog")

    }

   private fun showDetailDialog(item: RecyclerMissionData) {
       val detailDialog = MissionDetailDialogFragment()
       detailDialog.show(requireActivity().supportFragmentManager, "mission_detail_dialog")
   }



}