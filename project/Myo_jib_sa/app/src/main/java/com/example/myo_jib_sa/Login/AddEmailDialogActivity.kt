package com.example.myo_jib_sa.Login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myo_jib_sa.MainActivity
import com.example.myo_jib_sa.databinding.ActivityAddEmailDialogBinding
import com.example.myo_jib_sa.databinding.DialogSignupCompleteBinding


class AddEmailDialogActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddEmailDialogBinding
    private var emailCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddEmailDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 팝업 액티비티의 크기를 화면 비율의 0.9로 설정
        val displayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.9).toInt()
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        window.setLayout(width, height)



        //가입 완료 dialog 설정
        val binding_complete = DialogSignupCompleteBinding.inflate(layoutInflater)
        val dialog_complete = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE) // 타이틀 제거
            setContentView(binding_complete.root) // xml 레이아웃 파일과 연결
            // 다이얼로그의 크기 설정
            setDialogSize(this, 0.8, WindowManager.LayoutParams.WRAP_CONTENT)
        }

        // 가입하기 버튼 초기 상태 설정 (비활성화)
        binding.signUpAddEmailSignUpBtn.isEnabled = false
        binding.signUpAddEmailSignUpBtn.setBackgroundColor(Color.GRAY)

        binding.signUpAddEmailSendCodeBtn.setOnClickListener {
            val email = binding.signUpInputEmail.text.toString()
                // 이메일 보내기
            emailCode = createEmailCode()
            GMailSender(emailCode).sendEmail(email)
            Toast.makeText(applicationContext, "이메일을 성공적으로 전송했습니다.", Toast.LENGTH_SHORT).show()
        }
        binding.signUpAddEmailInputCodeBtn.setOnClickListener {
            // 인증코드(createEmailCode())와 binding.signUpInputCode에 입력한 내용이 일치할경우
            // 토스트 메세지로 인증에 성공하셨습니다. 일치하지 않을 경우 인증에 실패하셨습니다.
            Log.d("code",binding.signUpInputCode.text.toString())
            Log.d("code",emailCode)
            if (emailCode  == binding.signUpInputCode.text.toString()) {
                Toast.makeText(applicationContext, "인증에 성공하셨습니다.", Toast.LENGTH_SHORT).show()
                // 인증에 성공한 경우, 가입하기 버튼 활성화 및 색상 변경
                binding.signUpAddEmailSignUpBtn.isEnabled = true
                binding.signUpAddEmailSignUpBtn.setBackgroundColor(Color.BLACK)
            } else {
                Toast.makeText(applicationContext, "인증에 실패하셨습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signUpAddEmailSignUpBtn.setOnClickListener{
            dialog_complete.show()
            binding_complete.signUpCompleteBtn.setOnClickListener {
                // 메인으로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

    }
    // 이메일 인증코드 생성
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
    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_OUTSIDE) {
            return false
        }
        return true
    }

    // 안드로이드 백버튼 막기
    override fun onBackPressed() {

        return
    }

    fun setDialogSize(dialog: Dialog, widthPercentage: Double, height: Int) {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes) // 기존 속성 복사

        // 원하는 크기로 설정
        val displayMetrics = resources.displayMetrics
        val dialogWidth = (displayMetrics.widthPixels * widthPercentage).toInt() // 너비를 화면 너비의 특정 비율로 설정
        layoutParams.width = dialogWidth
        layoutParams.height = height

        dialog.window?.attributes = layoutParams // 속성 적용
    }
}