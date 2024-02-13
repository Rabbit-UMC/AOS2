package com.example.myo_jib_sa.mypage.history

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.databinding.FragmentMypageHistorySuccessBinding
import com.example.myo_jib_sa.mypage.adapter.MypageHistoryRVAdapter
import com.example.myo_jib_sa.mypage.api.GetHistoryResponse
import com.example.myo_jib_sa.mypage.api.MypageAPI
import com.example.myo_jib_sa.mypage.api.UserMissionResDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageHistorySuccessFragment(private val nickname: String?) : Fragment() {
    lateinit var binding: FragmentMypageHistorySuccessBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageHistorySuccessBinding.inflate(layoutInflater)

        binding.mypageHistorySuccessNicknameTv.text = nickname


        return binding.root
    }

    override fun onResume() {
        super.onResume()

        getSuccessHistory()
    }

    private fun getSuccessHistory() {
        sRetrofit.create(MypageAPI::class.java)
            .getSuccessHistory()
            .enqueue(object : Callback<GetHistoryResponse> {
                override fun onResponse(
                    call: Call<GetHistoryResponse>,
                    response: Response<GetHistoryResponse>
                ) {
                    val historyResponse = response.body()
                    if(historyResponse != null && historyResponse.isSuccess){
                        setViewpager(historyResponse.result.userMissionResDtos)
                        binding.mypageHistorySuccessCntTv.text = historyResponse.result.targetCnt.toString()+"개"
                        binding.mypageHistorySuccessWholeMissionTv.text = historyResponse.result.missionCnt.toString()+"개"
                        binding.mypageHistorySuccessMissionSuccessCntTv.text = historyResponse.result.targetCnt.toString()+"개"
                    }
                }

                override fun onFailure(call: Call<GetHistoryResponse>, t: Throwable) {
                    Log.d("getSuccessHistory", "onFailure : $t")
                }

            })
    }

    private fun setViewpager(items : List<UserMissionResDto>) {
        val adapter = MypageHistoryRVAdapter(items)
        binding.mypageHistorySuccessVp.adapter = adapter
    }
}