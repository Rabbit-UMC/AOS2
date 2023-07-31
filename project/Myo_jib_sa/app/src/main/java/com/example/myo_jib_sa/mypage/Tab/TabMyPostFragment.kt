package com.example.myo_jib_sa.mypage.Tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.FragmentTabMyPostBinding


class TabMyPostFragment : Fragment() {

    lateinit var binding: FragmentTabMyPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentTabMyPostBinding.inflate(layoutInflater)



        return binding.root
    }


}