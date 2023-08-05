package com.example.myo_jib_sa.mission.Dialog

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.DialogMissionSubjectFragmentBinding

interface DataTransferInterface {
    fun onDataTransfer(data: String)
}

class MissionSubjectDialogFragment(private val dataTransferListener: DataTransferInterface) : DialogFragment()
{
    private lateinit var binding:DialogMissionSubjectFragmentBinding
    private val subjectArr = arrayListOf("자유","운동","예술")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=DialogMissionSubjectFragmentBinding.inflate(inflater, container, false)


        //NumberPicker 설정
        binding.missionSubjectPicker.apply{
            this.wrapSelectorWheel=true
            this.displayedValues=subjectArr.toTypedArray()
            this.minValue=0
            this.maxValue= subjectArr.size-1
        }

        //완료 버튼 누르면 픽커에서 선택한 주제 activity에 연결
        binding.selectCompleteTxt.setOnClickListener {
            val subject = subjectArr[binding.missionSubjectPicker.value]
            dataTransferListener.onDataTransfer(subject)
            dismiss()
        }
        binding.selectCancelTxt.setOnClickListener {
            dismiss()
        }
        return binding.root
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onResume() {
        super.onResume()
        // 다이얼로그의 크기 설정
        dialog?.let { setDialogSize(it, 0.9, WindowManager.LayoutParams.WRAP_CONTENT) }
    }

    private fun setDialogSize(dialog: Dialog, widthPercentage: Double, height: Int) {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)

        val displayMetrics = resources.displayMetrics
        val dialogWidth = (displayMetrics.widthPixels * widthPercentage).toInt()
        layoutParams.width = dialogWidth
        layoutParams.height = height

        dialog.window?.attributes = layoutParams
    }



}