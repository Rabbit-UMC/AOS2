package com.example.myo_jib_sa.signup

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.databinding.DialogSignupCompleteBinding

class SignUpCompleteDialog(private val nickname: String) : DialogFragment() {
    private lateinit var binding:DialogSignupCompleteBinding
    private var listener: CompleteListener? = null
    companion object {
        const val DIALOG_MARGIN_DP = 20
        const val DIALOG_HEIGHT_DP = 411
    }

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

        binding.signUpCompleteNickname.text = nickname

        //가입하기 버튼
        binding.signUpCompleteBtn.setOnClickListener{
            listener?.completeListener()
        }

        return binding.root
    }
    override fun onResume() {
        super.onResume()
        // 다이얼로그의 크기 설정
        setDialogSize()
    }

    private fun setDialogSize() {
        dialog?.let { dialog ->
            val metrics = resources.displayMetrics
            val density = metrics.density
            val marginPx = (DIALOG_MARGIN_DP * density * 2).toInt()
            val width = metrics.widthPixels - marginPx
            val height = (DIALOG_HEIGHT_DP * density).toInt()

            val layoutParams = WindowManager.LayoutParams().apply {
                copyFrom(dialog.window?.attributes)
                this.width = width
                this.height = height
            }

            dialog.window?.apply {
                attributes = layoutParams
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }

}