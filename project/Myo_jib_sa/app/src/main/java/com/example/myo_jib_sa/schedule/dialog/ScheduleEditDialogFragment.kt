package com.example.myo_jib_sa.schedule.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.databinding.DialogFragmentScheduleEditBinding
import java.util.regex.Pattern

class ScheduleEditDialogFragment : DialogFragment() {
    private lateinit var binding: DialogFragmentScheduleEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentScheduleEditBinding.inflate(inflater, container, false)

        // 레이아웃 배경을 투명하게 해줌
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)


        binding.exitTv.setOnClickListener{
            dismiss()
            buttonClickListener.onClickEditBtn()
        }

        //일정 제목 특수문자 제어
        binding.scheduleTitleEtv.filters = arrayOf(editTextFilter)

        //미션 제목 클릭시
        binding.missionTitleTv.setOnClickListener {
            setSpinnerDialog(0)
        }

        //날짜 클릭시
        binding.scheduleDateTv.setOnClickListener {
            setSpinnerDialog(1)
        }

        //시간 클릭시
        binding.scheduleStartTimeTv.setOnClickListener {
            setSpinnerDialog(2)
        }
        binding.scheduleEndTimeTv.setOnClickListener {
            setSpinnerDialog(3)
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

    /**
    1. 정규식 패턴 ^[a-z] : 영어 소문자 허용
    2. 정규식 패턴 ^[A-Z] : 영어 대문자 허용
    3. 정규식 패턴 ^[ㄱ-ㅣ가-힣] : 한글 허용
    4. 정규식 패턴 ^[0-9] : 숫자 허용
    5. 정규식 패턴 ^[ ] or ^[\\s] : 공백 허용
     **/
    private val editTextFilter = InputFilter { source, start, end, dest, dstart, dend ->
        val ps = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣a-z-A-Z0-9()&_\\s-]+$")
        if (!ps.matcher(source).matches()) {
            ""
        } else source
    }

    private fun setSpinnerDialog(position:Int){
        var bundle = Bundle()
        bundle.putInt("position", position)
        val scheduleSpinnerDialogFragment = ScheduleSpinnerDialogFragment()
        scheduleSpinnerDialogFragment.arguments = bundle
        scheduleSpinnerDialogFragment.show(requireActivity().supportFragmentManager, "ScheduleEditDialog")
    }



}