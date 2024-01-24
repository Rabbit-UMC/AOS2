package com.example.myo_jib_sa.signup

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.MainActivity
import com.example.myo_jib_sa.databinding.DialogSignupCompleteBinding

class SignUpCompleteDialog() : DialogFragment() {
    private lateinit var binding:DialogSignupCompleteBinding
    private var listener: CompleteListener? = null
    interface CompleteListener {
        fun completeListener()
    }

    fun initCompleteListener(listener: CompleteListener) {
        this.listener = listener
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DialogSignupCompleteBinding.inflate(inflater,container, false)

        //가입하기 버튼
        binding.signUpCompleteBtn.setOnClickListener{
            listener?.completeListener()
        }

        return binding.root
    }
    override fun onResume() {
        super.onResume()
        // 다이얼로그의 크기 설정
        dialog?.let { setDialogSize(it) }
    }

    private fun setDialogSize(dialog: Dialog) {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)

        val displayMetrics = resources.displayMetrics
        val dpToPx = displayMetrics.density
        val marginPx = (20 * dpToPx).toInt() * 2 // 양옆 마진 10dp를 픽셀로 변환 후 양쪽을 고려하여 곱하기 2
        val dialogWidth = displayMetrics.widthPixels - marginPx
        layoutParams.width = dialogWidth
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialog.window?.attributes = layoutParams
    }

}