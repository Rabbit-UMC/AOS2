package com.example.myo_jib_sa.mypage.tab

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.databinding.FragmentMyPageTabCommentBinding
import com.example.myo_jib_sa.mypage.API.UserAPI
import com.example.myo_jib_sa.mypage.MyPagePostRVAdapter
import com.example.myo_jib_sa.mypage.API.GetMyPostResponse
import com.example.myo_jib_sa.mypage.API.GetMyPostResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyPageTabPostFragment : Fragment() {

    lateinit var binding: FragmentMyPageTabCommentBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var myPostAdapter: MyPagePostRVAdapter
    private var decoration: RecyclerView.ItemDecoration? = null

    val retrofit: UserAPI = sRetrofit.create(UserAPI::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMyPageTabCommentBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        recyclerView = binding.myPageTabCommentRv

        getMyPostAPI()
    }

    private fun getMyPostAPI() {
        retrofit.getMyPost(0).enqueue(object : Callback<GetMyPostResponse> {
            override fun onResponse(call: Call<GetMyPostResponse>, response: Response<GetMyPostResponse>) {
                if (response.isSuccessful) {
                    val postResponse = response.body()
                    val dataList = postResponse?.result
                    if (postResponse != null) {
                        if(postResponse.errorCode == "403"){
                            val placeholderData = listOf(
                                GetMyPostResult(0L, "Placeholder Title", "2023-08-25 13:30", 2, 3),
                                GetMyPostResult(1L, "Another Placeholder Title", "2023-08-21 15:11", 4, 0)
                            )
                            setRecyclerview(placeholderData)
                        }
                        else{
                            if (dataList != null) {
                                Log.d("mypost", postResponse.toString())
                                // 어댑터 생성 및 리사이클러뷰에 설정
                                setRecyclerview(dataList)
                            }
                        }
                    }
                } else {
                    // API 요청 실패 처리
                    Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
                    val placeholderData = listOf(
                        GetMyPostResult(0L, "Placeholder Title", "2023-08-25 13:30", 2, 3),
                        GetMyPostResult(1L, "Another Placeholder Title", "2023-08-21 15:11", 4, 0)
                    // 원하는 만큼의 Placeholder 데이터를 추가할 수 있습니다.
                    )
                    setRecyclerview(placeholderData)
                }
            }
            override fun onFailure(call: Call<GetMyPostResponse>, t: Throwable) {
                // 네트워크 등의 문제로 API 요청이 실패한 경우 처리
                Toast.makeText(requireContext(), "서버 오류 발생", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun setRecyclerview(dataList: List<GetMyPostResult>) {
        decoration?.let { recyclerView.removeItemDecoration(it) }

        myPostAdapter = MyPagePostRVAdapter(
            requireContext(),
            dataList
        )

        recyclerView.adapter = myPostAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 아이템 간격 설정 (옵션)
        decoration = MyPagePostRVAdapter.CustomItemDecoration(15) // decoration 변수 초기화
        recyclerView.addItemDecoration(decoration as MyPagePostRVAdapter.CustomItemDecoration)

        // 어댑터 설정 후 데이터 변경을 알림
        myPostAdapter.notifyDataSetChanged()
    }


}