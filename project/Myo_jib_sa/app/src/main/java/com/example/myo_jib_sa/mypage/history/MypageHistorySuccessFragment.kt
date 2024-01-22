package com.example.myo_jib_sa.mypage.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myo_jib_sa.community.api.manager.ManagerMissionJoinRequest
import com.example.myo_jib_sa.databinding.FragmentMypageHistorySuccessBinding
import com.example.myo_jib_sa.mypage.adapter.MypageHistoryRVAdapter
import com.example.myo_jib_sa.mypage.api.GetHistoryResponse

class MypageHistorySuccessFragment : Fragment() {
    lateinit var binding: FragmentMypageHistorySuccessBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageHistorySuccessBinding.inflate(layoutInflater)

        val myItems: List<GetHistoryResponse> = arrayListOf(GetHistoryResponse("1"), GetHistoryResponse("1"), GetHistoryResponse("1"))
        val adapter = MypageHistoryRVAdapter(myItems)
        binding.mypageHistorySuccessVp.adapter = adapter

        return binding.root
    }

}