package com.example.myo_jib_sa.mission

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.DialogMissionSubjectFragmentBinding

class MissionSubjectDialogFragment : Fragment() {
    private lateinit var binding:DialogMissionSubjectFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=DialogMissionSubjectFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


}