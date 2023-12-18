package com.example.myo_jib_sa.mypage

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.Login.API.RetrofitInstance
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.FragmentMypageBinding
/*import com.example.myo_jib_sa.mission.MissionAdapter
import com.example.myo_jib_sa.mission.MissionItem*/
import com.example.myo_jib_sa.mypage.API.MyPageITFC
import com.example.myo_jib_sa.mypage.API.Post
import com.example.myo_jib_sa.mypage.API.getUserProfileResponse
import com.example.myo_jib_sa.mypage.API.putUserImageResponse
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MypageFragment : Fragment() {
    lateinit var binding: FragmentMypageBinding

    val retrofit = RetrofitInstance.getInstance().create(MyPageITFC::class.java)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMypageBinding.inflate(layoutInflater)

        val tabLayout = binding.myPageTab
        val viewPager = binding.myPageViewpager

        // 어댑터 생성
        val adapter = MypageViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "작성한 글"

                }
                1 -> {
                    tab.text = "댓글단 글"
                }
            }
        }.attach()

        /*UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("kakao", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                val kakaoEmail = user.kakaoAccount?.email
                Log.d("kakaoM", kakaoEmail.toString())

                // '@' 문자열을 기준으로 이메일을 분리
                val indexOfAtSymbol = kakaoEmail?.indexOf("@")
                val nickname = if (indexOfAtSymbol != null && indexOfAtSymbol > 0) {
                    kakaoEmail.substring(0, indexOfAtSymbol)
                } else {
                    kakaoEmail // '@' 문자열이 없으면 그대로 사용
                }

                binding.txtMyPageNinkName.text = nickname
            }
        }*/



        binding.myPageEditBtn.setOnClickListener{
            val intent = Intent(requireActivity(), EditMypageActivity::class.java)
            startActivity(intent)
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        ProfileAPI()
    }


    fun ProfileAPI() {
        val sharedPreferences = requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val jwt = sharedPreferences.getString("jwt", null)
        val userId = sharedPreferences.getLong("userId", 0L)



        if (jwt != null) {
            retrofit.getUserProfile(jwt).enqueue(object : Callback<getUserProfileResponse> {
                override fun onResponse(call: Call<getUserProfileResponse>, response: Response<getUserProfileResponse>) {
                    if (response.isSuccessful) {
                        val profileResponse = response.body()
                        val profileData = profileResponse?.result

                        if (profileData != null) {
                            //val userProfileImageUri = Uri.parse(profileData.userProfileImage)

                            Log.d("img",profileData.userProfileImage)
                            binding.txtMyPageNinkName.text = profileData.userName

                            // Glide를 사용하여 이미지 설정
                            Glide.with(requireContext())
                                .load(profileData.userProfileImage)
                                .error(R.drawable.ic_mypage_profile)
                                .into(binding.myPageProfileImg)
                        }

                    } else {
                        // API 요청 실패 처리
                        Toast.makeText(requireContext(), "API 요청 실패 처리", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<getUserProfileResponse>, t: Throwable) {
                    // 네트워크 등의 문제로 API 요청이 실패한 경우 처리
                    Toast.makeText(requireContext(), "서버 오류 발생", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }



}