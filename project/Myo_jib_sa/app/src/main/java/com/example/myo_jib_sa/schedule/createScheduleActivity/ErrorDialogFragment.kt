package com.example.myo_jib_sa.schedule.createScheduleActivity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.DialogFragmentErrorBinding
import com.example.myo_jib_sa.databinding.DialogFragmentScheduleDetailBinding


class ErrorDialogFragment : DialogFragment() {
    private lateinit var binding:DialogFragmentErrorBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DialogFragmentErrorBinding.inflate(inflater, container, false)

        // 레이아웃 배경을 투명하게 해줌
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        binding.yesTv.setOnClickListener {
            dismiss()
        }

        return binding.root
    }


}