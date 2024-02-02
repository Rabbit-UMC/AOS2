package com.example.myo_jib_sa.community.post

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.databinding.FragmentPostPictureBinding



class PostPictureFragment : Fragment() {

    private lateinit var binding: FragmentPostPictureBinding
    private var isAttached = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostPictureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve data from arguments and set the picture
        val filePath = arguments?.getString("filePath")
        if (!filePath.isNullOrBlank()) {
            setPicture(filePath)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }

    override fun onDetach() {
        super.onDetach()
        isAttached = false
    }

    fun setPicture(filePath:String){
        Log.d("setPicture 파일 경로",filePath)
        if (isAttached) {
            Glide.with(requireContext())
                .load(filePath)
                .into(binding.postPictureImg)
        }
    }


}