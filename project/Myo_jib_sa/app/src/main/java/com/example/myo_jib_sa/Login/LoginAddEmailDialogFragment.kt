package com.example.myo_jib_sa.Login

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.DialogLoginAddEmailFragmentBinding

interface OnEmailEnteredInterface {
    fun onEmailEntered(email: String)
}

class LoginAddEmailDialogFragment(private val onEmailEnteredListener: OnEmailEnteredInterface) : DialogFragment() {

    private var emailCode: String = ""
    private var email: String = ""

    private lateinit var binding:DialogLoginAddEmailFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DialogLoginAddEmailFragmentBinding.inflate(inflater,container, false)

        // 가입하기 버튼 초기 상태 설정 (비활성화)
        binding.signUpAddEmailSignUpBtn.isEnabled = false
        binding.signUpAddEmailSignUpBtn.setBackgroundColor(Color.GRAY)

        binding.signUpAddEmailSendCodeBtn.setOnClickListener {
            email = binding.signUpInputEmail.text.toString()
            // 이메일 보내기
            emailCode = createEmailCode()
            GMailSender(emailCode).sendEmail(email)
            Toast.makeText(requireContext(), "이메일을 성공적으로 전송했습니다.", Toast.LENGTH_SHORT).show()
        }
        binding.signUpAddEmailInputCodeBtn.setOnClickListener {
            // 인증코드(createEmailCode())와 binding.signUpInputCode에 입력한 내용이 일치할경우
            // 토스트 메세지로 인증에 성공하셨습니다. 일치하지 않을 경우 인증에 실패하셨습니다.
            Log.d("code",binding.signUpInputCode.text.toString())
            Log.d("code",emailCode)
            if (emailCode  == binding.signUpInputCode.text.toString()) {
                Toast.makeText(requireContext(), "인증에 성공하셨습니다.", Toast.LENGTH_SHORT).show()
                // 인증에 성공한 경우, 가입하기 버튼 활성화 및 색상 변경
                binding.signUpAddEmailSignUpBtn.isEnabled = true
                binding.signUpAddEmailSignUpBtn.setBackgroundColor(Color.BLACK)
            } else {
                Toast.makeText(requireContext(), "인증에 실패하셨습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //가입하기 버튼
        binding.signUpAddEmailSignUpBtn.setOnClickListener{
            onEmailEnteredListener.onEmailEntered(email)
            dismiss()
        }

        return binding.root
    }

    //이메일 설정
    private fun createEmailCode(): String {
        val str = arrayOf(
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9"
        )
        val newCode = StringBuilder()

        for (x in 0 until 8) {
            val random = (Math.random() * str.size).toInt()
            newCode.append(str[random])
        }
        return newCode.toString()
    }

    // 바깥레이어 클릭 시 닫히지 않도록 함
    fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_OUTSIDE) {
            return false
        }
        return true
    }

    // 안드로이드 백버튼 막기
    fun onBackPressed() {

        return
    }



    //다이얼로그 설정
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onResume() {
        super.onResume()
        // 다이얼로그의 크기 설정
        dialog?.let { setDialogSize(it, 0.94, WindowManager.LayoutParams.WRAP_CONTENT) }
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