package com.example.myo_jib_sa.mission.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.databinding.DialogMissionErrorFragmentBinding


class MissionErrorDialogFragment : DialogFragment() {
    private lateinit var binding:DialogMissionErrorFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DialogMissionErrorFragmentBinding.inflate(inflater, container, false)

        // 레이아웃 배경을 투명하게 해줌
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        binding.yesTv.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
}