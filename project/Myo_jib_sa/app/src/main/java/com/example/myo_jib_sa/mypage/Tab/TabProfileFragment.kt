package com.example.myo_jib_sa.mypage.Tab

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myo_jib_sa.databinding.FragmentTabProfileBinding
import com.example.myo_jib_sa.mypage.EditMypageActivity

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

        return binding.root
    }

}