package com.example.myo_jib_sa.community.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Button
import com.example.myo_jib_sa.databinding.DialogCommunityRedBlackBinding

class CommunityDialogRedBlack(context: Context
                              , private val title:String
                              , private val detail:String
                              , private val okTxt:String
                              , private val cancleTxt:String) : Dialog(context) {
    private lateinit var binding: DialogCommunityRedBlackBinding


    interface CustomDialogListener {
        fun onPositiveButtonClicked(value: Boolean)
    }

    private var listener: CustomDialogListener? = null

    fun setCustomDialogListener(listener: CustomDialogListener) {
        this.listener = listener
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DialogCommunityRedBlackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.communityDialogConstraint.clipToOutline = true
        binding.communityDialogConstraint.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                // radius: 8dp
                outline?.setRoundRect(0, 0, view?.width ?: 0, view?.height ?: 0, convertDpToPixel(8f, view?.context ?: return))

            }
        }


        // 다이얼로그가 포커스를 가지도록 설정
        window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)


        //text 설정
        binding.communityPopupTxt.text= title
        binding.communityPopupDetailTxt.text=detail
        binding.communityPopupOkBtn.text=okTxt
        binding.communityPopupNoBtn.text=cancleTxt


        // 취소 버튼 클릭 리스너 설정
        binding.communityPopupNoBtn.setOnClickListener {
            // 다이얼로그 닫기
            dismiss()
        }

        // 확인 버튼 클릭 리스너 설정
        binding.communityPopupOkBtn.setOnClickListener {
            // 확인 버튼 클릭 이벤트 처리
            listener?.onPositiveButtonClicked(true)
            // 다이얼로그 닫기
            dismiss()
        }
    }
    // dp를 픽셀로 변환하는 메서드
    private fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * context.resources.displayMetrics.density
    }
}