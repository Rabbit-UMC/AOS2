package com.example.myo_jib_sa.community.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.CommunityPopupOkBinding

class CommunityPopupOk(context: Context, private val txt:String) : Dialog(context) {

    private lateinit var binding:CommunityPopupOkBinding

    interface CustomDialogListener {
        fun onPositiveButtonClicked(value: Boolean)
    }

    private var listener: CustomDialogListener? = null

    fun setCustomDialogListener(listener: CustomDialogListener) {
        this.listener = listener
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= CommunityPopupOkBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 다이얼로그가 포커스를 가지도록 설정
        window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)

        val cancelButton: Button = binding.communityPopupCancelBtn
        val okButton: Button = binding.communityPopupOkBtn
        binding.communityPopupTxt.text= txt

        // 취소 버튼 클릭 리스너 설정
        cancelButton.setOnClickListener {
            // 다이얼로그 닫기
            dismiss()
        }

        // 확인 버튼 클릭 리스너 설정
        okButton.setOnClickListener {
            // 확인 버튼 클릭 이벤트 처리
            listener?.onPositiveButtonClicked(true)
            // 다이얼로그 닫기
            dismiss()
        }
    }

}
