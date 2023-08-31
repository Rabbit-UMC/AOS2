package com.example.myo_jib_sa.mypage.Tab

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.Login.API.RetrofitInstance
import com.example.myo_jib_sa.databinding.FragmentTabMyPostBinding
import com.example.myo_jib_sa.mission.MissionAdapter
import com.example.myo_jib_sa.mypage.API.MyPageITFC
import com.example.myo_jib_sa.mypage.API.Post
import com.example.myo_jib_sa.mypage.API.getMyPostResponse
import com.example.myo_jib_sa.mypage.MyPostAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TabMyPostFragment : Fragment() {

    lateinit var binding: FragmentTabMyPostBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var myPostAdapter: MyPostAdapter
    private var decoration: RecyclerView.ItemDecoration? = null

    val retrofit = RetrofitInstance.getInstance().create(MyPageITFC::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentTabMyPostBinding.inflate(layoutInflater)

/*        recyclerView=binding.mypagePostRecycler
        MyPostAPI()*/


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        recyclerView=binding.mypagePostRecycler

        MyPostAPI()
    }

    fun MyPostAPI() {
        val sharedPreferences = requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val jwt = sharedPreferences.getString("jwt", null)
        val userId = sharedPreferences.getLong("userId", 0L)

        if (jwt != null) {
            retrofit.getMyPost(jwt, 0, userId).enqueue(object : Callback<getMyPostResponse> {
                override fun onResponse(call: Call<getMyPostResponse>, response: Response<getMyPostResponse>) {
                    if (response.isSuccessful) {
                        val postResponse = response.body()
                        val dataList = postResponse?.result

                        if (postResponse != null) {
                            if(postResponse.code==403){
                                val placeholderData = listOf(
                                    Post(0L, "Placeholder Title", "2023-08-25 13:30", 2, 3),
                                    Post(1L, "Another Placeholder Title", "2023-08-21 15:11", 4, 0)
                                )
                                setUpMissionAdapter(placeholderData)
                            }
                            else{
                                if (dataList != null) {
                                    Log.d("mypost", postResponse.toString())
                                    // 어댑터 생성 및 리사이클러뷰에 설정
                                    setUpMissionAdapter(listOf(dataList))

                                }
                            }
                        }

                    } else {
                        // API 요청 실패 처리
                        Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
                        val placeholderData = listOf(
                            Post(0L, "Placeholder Title", "2023-08-25 13:30", 2, 3),
                            Post(1L, "Another Placeholder Title", "2023-08-21 15:11", 4, 0)
                            // 원하는 만큼의 Placeholder 데이터를 추가할 수 있습니다.
                        )
                        setUpMissionAdapter(placeholderData)
                    }
                }

                override fun onFailure(call: Call<getMyPostResponse>, t: Throwable) {
                    // 네트워크 등의 문제로 API 요청이 실패한 경우 처리
                    Toast.makeText(requireContext(), "서버 오류 발생", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun setUpMissionAdapter(dataList: List<Post>) {
        decoration?.let { recyclerView.removeItemDecoration(it) }

        myPostAdapter = MyPostAdapter(
            requireContext(),
            dataList
        )

        recyclerView.adapter = myPostAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 아이템 간격 설정 (옵션)
        decoration = MyPostAdapter.CustomItemDecoration(15) // decoration 변수 초기화
        recyclerView.addItemDecoration(decoration as MyPostAdapter.CustomItemDecoration)

        // 어댑터 설정 후 데이터 변경을 알림
        myPostAdapter.notifyDataSetChanged()
    }


}