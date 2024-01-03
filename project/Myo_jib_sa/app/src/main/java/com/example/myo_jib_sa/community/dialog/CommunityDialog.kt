package com.example.myo_jib_sa.community.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import com.example.myo_jib_sa.databinding.DialogCommunityOkBinding

class CommunityDialog(context: Context
                      , private val title:String
                      , private val detail:String
                      , private val okTxt:String
                      , private val cancleTxt:String
                      , private val backgroundColor:String
                      , private val okColor:String) : Dialog(context) {
    private lateinit var binding: DialogCommunityOkBinding


    interface CustomDialogListener {
        fun onPositiveButtonClicked(value: Boolean)
    }

    private var listener: CustomDialogListener? = null

    fun setCustomDialogListener(listener: CustomDialogListener) {
        this.listener = listener
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DialogCommunityOkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.communityDialogConstraint.clipToOutline=true

        // 다이얼로그가 포커스를 가지도록 설정
        window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)

        val cancelButton: Button = binding.communityPopupCancelBtn
        val okButton: Button = binding.communityPopupOkBtn

        //text 설정
        binding.communityPopupTxt.text= title
        binding.communityPopupDetailTxt.text=detail
        binding.communityPopupOkBtn.text=okTxt
        binding.communityPopupNoBtn.text=cancleTxt

        //색상 설정
        binding.communityDialogConstraint.setBackgroundColor(Color.parseColor(backgroundColor))
        binding.communityPopupOkBtn.setBackgroundColor(Color.parseColor(okColor))

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