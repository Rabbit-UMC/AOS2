package com.example.myo_jib_sa.schedule.currentMissionActivity

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.DialogFragmentCurrentMissionDeleteBinding
import com.example.myo_jib_sa.databinding.DialogFragmentScheduleDeleteBinding
import com.example.myo_jib_sa.schedule.adapter.ScheduleAdaptar
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.schedule.api.scheduleDelete.ScheduleDeleteResponse
import com.example.myo_jib_sa.schedule.api.scheduleDelete.ScheduleDeleteService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DeleteDialogFragment(
) : DialogFragment() {
    private lateinit var binding: DialogFragmentCurrentMissionDeleteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentCurrentMissionDeleteBinding.inflate(inflater, container, false)


        //requestWindowFeature(Window.FEATURE_NO_TITLE)
        initViews()

        return binding.root
    }

    private fun initViews() {

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        //확인
        binding.yesTv.setOnClickListener{
            dismiss()
            buttonClickListener.onClickYesBtn()
        }
        //취소
        binding.exitTv.setOnClickListener {
            dismiss()
            buttonClickListener.onClickExitBtn()
        }
    }

    // 인터페이스
    interface OnButtonClickListener {
        fun onClickYesBtn()
        fun onClickExitBtn()
    }
    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }
    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener



}