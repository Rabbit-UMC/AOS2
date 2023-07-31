package com.example.myo_jib_sa.mypage.Tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.FragmentTabMyCommentBinding

class TabMyCommentFragment : Fragment() {

    lateinit var binding: FragmentTabMyCommentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentTabMyCommentBinding.inflate(layoutInflater)



        return binding.root}

}