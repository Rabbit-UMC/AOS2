package com.example.myo_jib_sa.mypage.Tab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myo_jib_sa.databinding.FragmentTabProfileBinding
import com.example.myo_jib_sa.mypage.EditMypageActivity
import com.kakao.sdk.user.UserApiClient

class TabProfileFragment : Fragment() {

    lateinit var binding: FragmentTabProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentTabProfileBinding.inflate(layoutInflater)

        binding.myPageEditBtn.setOnClickListener{
            //EditMypageActivity로 이동
            val intent = Intent(requireActivity(), EditMypageActivity::class.java)
            startActivity(intent)
        }
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("kakao", "사용자 정보 요청 실패", error)
            }
            else if (user != null) {
                var kakaoEmail=user.kakaoAccount?.email
                Log.d("kakaoM",kakaoEmail.toString())
                binding.txtMyPageNinkName.text=kakaoEmail
                binding.txtMyPageEmail.text=kakaoEmail
            }
        }

        return binding.root
    }


}