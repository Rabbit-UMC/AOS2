package com.example.myo_jib_sa.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.databinding.FragmentMypageBinding
import com.example.myo_jib_sa.mypage.adapter.MypageViewPagerAdapter
import com.example.myo_jib_sa.mypage.api.GetUserProfileResponse
/*import com.example.myo_jib_sa.mission.MissionAdapter
import com.example.myo_jib_sa.mission.MissionItem*/
import com.example.myo_jib_sa.mypage.api.MypageAPI
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MypageFragment : Fragment() {
    lateinit var binding: FragmentMypageBinding

    val retrofit: MypageAPI = sRetrofit.create(MypageAPI::class.java)
    val isNotDuplication : Boolean = false
    var nickname: String = ""
    var uri: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(layoutInflater)

        initTabLayout()
        initListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getProfileInfo()
    }


    private fun initTabLayout() {
        // 어댑터 생성
        val adapter = MypageViewPagerAdapter(childFragmentManager, lifecycle)
        binding.myPageViewpager.adapter = adapter

        TabLayoutMediator(binding.myPageTab, binding.myPageViewpager) { tab, position ->
            when (position) {
                0 -> tab.text = "작성한 글"
                1 -> tab.text = "댓글단 글"
            }
        }.attach()
    }

    private fun initListener() {
        binding.myPageHistoryBtn.setOnClickListener {

        }
        binding.myPageEditProfileBtn.setOnClickListener{
            startActivity(Intent(requireActivity(), EditProfileActivity::class.java)
                .putExtra("nickname", nickname)
                .putExtra("uri", uri)
            )
        }
    }
    private fun getProfileInfo() {
        retrofit.getUserProfile().enqueue(object : Callback<GetUserProfileResponse> {
            override fun onResponse(
                call: Call<GetUserProfileResponse>,
                response: Response<GetUserProfileResponse>
            ) {
                if (response.isSuccessful) {
                    val profileData = response.body()?.result
                    if (profileData != null) {
                        //val userProfileImageUri = Uri.parse(profileData.userProfileImage)
                        Log.d("img", profileData.userProfileImage)
                        binding.myPageNicknameTxt.text = profileData.userName
                        Glide.with(requireContext())
                            .load(profileData.userProfileImage)
                            .error(R.drawable.ic_app)
                            .into(binding.myPageProfileImg)

                        nickname = profileData.userName
                        uri = profileData.userProfileImage
                    }
                } else {
                    // API 요청 실패 처리
                    Toast.makeText(requireContext(), "API 요청 실패 처리", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetUserProfileResponse>, t: Throwable) {
                // 네트워크 등의 문제로 API 요청이 실패한 경우 처리
                Toast.makeText(requireContext(), "서버 오류 발생", Toast.LENGTH_SHORT).show()
            }
        })
    }
}