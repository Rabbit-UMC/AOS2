package com.example.myo_jib_sa.signup

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.databinding.DialogSignupDetailBinding

class SignUpTermsDetailDialog(private val title: String, private val desc: String) : DialogFragment() {
    private lateinit var binding: DialogSignupDetailBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // 배경을 투명하게 설정

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogSignupDetailBinding.inflate(inflater, container, false)

        binding.signUpDetailTitleTxt.text = title
        binding.signUpDetailDescTxt.text = desc

        binding.dialogExitBtn.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setDialogSize()
    }

    private fun setDialogSize() {
        dialog?.window?.let { window ->
            val displayMetrics = resources.displayMetrics
            val widthPixels = displayMetrics.widthPixels
            val margin = (20 * displayMetrics.density).toInt() // 양옆 마진 20dp 총합
            val width = widthPixels - margin*2
            val maxHeight = (displayMetrics.heightPixels * 0.6).toInt() // 최대 높이를 화면의 60%로 설정

            // 너비 설정
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

            // 높이가 최대 높이를 초과하지 않도록 뷰를 다시 측정
            window.attributes = window.attributes.apply {
                gravity = Gravity.CENTER
            }

            // 뷰가 다 그려진 후에 최대 높이를 초과하는지 검사하고, 초과한다면 스크롤뷰의 높이를 조정
            window.decorView.post {
                if (binding.root.measuredHeight > maxHeight) {
                    binding.signUpDetailScroll.layoutParams =
                        binding.signUpDetailScroll.layoutParams.apply {
                            height = maxHeight
                        }
                    // 화면을 다시 그림
                    window.decorView.requestLayout()
                }
            }
        }
    }

}
