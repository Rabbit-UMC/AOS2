package com.example.myo_jib_sa.schedule.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.databinding.DialogFragmentScheduleDetailBinding

class ScheduleDetailDialogFragment : DialogFragment() {
    private lateinit var binding : DialogFragmentScheduleDetailBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentScheduleDetailBinding.inflate(inflater, container, false)

        // 레이아웃 배경을 투명하게 해줌
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        //x누르면 dialog종료
        binding.exitTv.setOnClickListener{
            dismiss()
        }

        binding.editBtn.setOnClickListener {
            var bundle = Bundle()
            ScheduleEditDialogFragment().arguments = bundle
            buttonClickListener.onClickEditBtn()
        }


        return binding.root
    }

    // 인터페이스
    interface OnButtonClickListener {
        fun onClickEditBtn()
    }
    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }
    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener
}