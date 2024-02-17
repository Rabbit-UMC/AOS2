package com.example.myo_jib_sa.mypage

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.databinding.FragmentMypageBinding
import com.example.myo_jib_sa.databinding.ToastMissionBinding
import com.example.myo_jib_sa.mission.MissionCreateActivity
import com.example.myo_jib_sa.mypage.api.GetUserProfileResponse
import com.example.myo_jib_sa.mypage.api.MypageAPI
import com.example.myo_jib_sa.mypage.adapter.MypageTabVpAdapter
import com.example.myo_jib_sa.mypage.history.MypageHistoryActivity
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MypageFragment : Fragment() {
    lateinit var binding: FragmentMypageBinding

    val retrofit: MypageAPI = sRetrofit.create(MypageAPI::class.java)
    var nickname: String = ""

    // ActivityResultLauncher 초기화
    private val editProfileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringExtra("resultMessage")?.let { message ->
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                showSnackbar("작성하신 일반 미션이 저장되었어요!")
            }
        }
    }

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
        val adapter = MypageTabVpAdapter(childFragmentManager, lifecycle)
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
            startActivity(Intent(requireActivity(), MypageHistoryActivity::class.java))
        }
        binding.myPageEditProfileBtn.setOnClickListener{
            editProfileLauncher.launch(Intent(requireContext(), EditProfileActivity::class.java))
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
                            .error(R.drawable.ic_profile)
                            .into(binding.myPageProfileImg)

                        nickname = profileData.userName
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

    // 스낵바 생성
    private fun showSnackbar(message: String) {
        val snackBarBinding = ToastMissionBinding.inflate(layoutInflater)
        snackBarBinding.toastMissionReportIv.setImageResource(R.drawable.ic_schedule_create_check)
        snackBarBinding.toastMissionReportTxt.text = message

        val snackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_SHORT).apply {
            animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
            (view as Snackbar.SnackbarLayout).apply {
                setBackgroundColor(Color.TRANSPARENT)
                addView(snackBarBinding.root, 0)
                translationY = -30.dpToPx().toFloat()
                elevation = 0f
            }
        }
        snackBar.show()
    }

    private fun Int.dpToPx(): Int = (this * requireContext().resources.displayMetrics.density).toInt()
}