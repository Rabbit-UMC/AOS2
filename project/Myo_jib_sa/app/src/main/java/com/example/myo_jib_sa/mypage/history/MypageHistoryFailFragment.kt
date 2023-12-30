package com.example.myo_jib_sa.mypage.history

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
import com.example.myo_jib_sa.databinding.FragmentMypageHistoryFailBinding
import com.example.myo_jib_sa.databinding.FragmentMypageHistorySuccessBinding
import com.example.myo_jib_sa.mypage.EditProfileActivity
import com.example.myo_jib_sa.mypage.adapter.MypageViewPagerAdapter
import com.example.myo_jib_sa.mypage.api.GetUserProfileResponse
/*import com.example.myo_jib_sa.mission.MissionAdapter
import com.example.myo_jib_sa.mission.MissionItem*/
import com.example.myo_jib_sa.mypage.api.MypageAPI
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MypageHistoryFailFragment : Fragment() {
    lateinit var binding: FragmentMypageHistoryFailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageHistoryFailBinding.inflate(layoutInflater)

        return binding.root
    }

}