package com.example.myo_jib_sa.mypage.history

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myo_jib_sa.base.MyojibsaApplication
import com.example.myo_jib_sa.databinding.FragmentMypageHistoryFailBinding
import com.example.myo_jib_sa.mypage.adapter.MypageHistoryRVAdapter
import com.example.myo_jib_sa.mypage.api.GetHistoryResponse
import com.example.myo_jib_sa.mypage.api.MypageAPI
import com.example.myo_jib_sa.mypage.api.UserMissionResDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageHistoryFailFragment(private val nickname: String?) : Fragment() {
    lateinit var binding: FragmentMypageHistoryFailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageHistoryFailBinding.inflate(layoutInflater)

        binding.mypageHistoryFailNicknameTv.text = nickname

        getFailureHistory()

        return binding.root
    }

    private fun getFailureHistory() {
        MyojibsaApplication.sRetrofit.create(MypageAPI::class.java)
            .getFailureHistory()
            .enqueue(object : Callback<GetHistoryResponse> {
                override fun onResponse(
                    call: Call<GetHistoryResponse>,
                    response: Response<GetHistoryResponse>
                ) {
                    val historyResponse = response.body()
                    if(historyResponse != null && historyResponse.isSuccess){
                        val items = historyResponse.result.userMissionResDtos.ifEmpty {
                            listOf(getEmptyDto())
                        }

                        setViewpager(items)
                        binding.mypageHistoryFailCntTv.text = historyResponse.result.targetCnt.toString()+"개"
                        binding.mypageHistoryFailWholeMissionTv.text = historyResponse.result.missionCnt.toString()+"개"
                        binding.mypageHistoryFailMissionFailCntTv.text = historyResponse.result.targetCnt.toString()+"개"
                    }
                }

                override fun onFailure(call: Call<GetHistoryResponse>, t: Throwable) {
                    Log.d("getFailureHistory", "onFailure : $t")
                }

            })
    }

    private fun setViewpager(items : List<UserMissionResDto>) {
        val adapter = MypageHistoryRVAdapter(items, false)
        binding.mypageHistoryFailVp.adapter = adapter
    }
    companion object {
        fun getEmptyDto() = UserMissionResDto(
            categoryId = -1,
            challengerCnt = -1,
            endAt = "",
            image = "",
            missionId = -1,
            startAt = "",
            successCnt = -1,
            title = ""
        )
    }
}