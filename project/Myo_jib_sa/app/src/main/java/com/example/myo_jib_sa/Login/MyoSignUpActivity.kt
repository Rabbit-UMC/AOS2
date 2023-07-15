package com.example.myo_jib_sa.Login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.CompoundButton
import com.example.myo_jib_sa.MainActivity
import com.example.myo_jib_sa.databinding.*
import com.kakao.sdk.user.UserApiClient

class MyoSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyoSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyoSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //카카오 이메일
        var kakaoEmail: String? = null

        val ageCheckBox = binding.signUpAgeCheckBox
        val useCheckBox = binding.signUpUseCheckBox
        val privacyCheckBox = binding.signUpPrivacyCheckBox
        val mailCheckBox = binding.signUpMailCheckBox
        val identifyCheckBox = binding.signUpIdentidyCheckBox
        val signUpButton = binding.signUpSignUpBtn


        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("kakao", "사용자 정보 요청 실패", error)
            }
            else if (user != null) {
                kakaoEmail=user.kakaoAccount?.email
                Log.d("kakaoM",kakaoEmail.toString())
            }
        }

        //닉네임 중복 체크



        //내용 보기 밑줄, 클릭 시 상세 설명 팝업
        //use
        val binding_use = DialogSignupUseBinding.inflate(layoutInflater)
        val dialog_use = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE) // 타이틀 제거
            setContentView(binding_use.root) // xml 레이아웃 파일과 연결

            // 다이얼로그의 크기 설정
            setDialogSize(this, 0.8,800)
        }

        binding.txtUseContent.setOnClickListener {
            dialog_use.show()
            binding_use.dialogExitBtn.setOnClickListener {
                // 다이얼로그 닫기
                dialog_use.dismiss()
            }
        }

        //privacy
        val binding_privacy = DialogSignupPrivacyBinding.inflate(layoutInflater)
        val dialog_privacy = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE) // 타이틀 제거
            setContentView(binding_privacy.root) // xml 레이아웃 파일과 연결

            // 다이얼로그의 크기 설정
            setDialogSize(this, 0.8, 800)
        }

        binding.txtPrivacyContent.setOnClickListener {
            dialog_privacy.show()
            binding_privacy.dialogExitBtn.setOnClickListener {
                // 다이얼로그 닫기
                dialog_privacy.dismiss()
            }
        }

        //mail
        val binding_mail = DialogSignupMailBinding.inflate(layoutInflater)
        val dialog_mail = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE) // 타이틀 제거
            setContentView(binding_mail.root) // xml 레이아웃 파일과 연결

            // 다이얼로그의 크기 설정
            setDialogSize(this, 0.8, WindowManager.LayoutParams.WRAP_CONTENT)
        }

        binding.txtMailContent.setOnClickListener {
            dialog_mail.show()
            binding_mail.dialogExitBtn.setOnClickListener {
                // 다이얼로그 닫기
                dialog_mail.dismiss()
            }
        }

        //identify
        val binding_identify = DialogSignupIdentifyBinding.inflate(layoutInflater)
        val dialog_identify = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE) // 타이틀 제거
            setContentView(binding_identify.root) // xml 레이아웃 파일과 연결

            // 다이얼로그의 크기 설정
            setDialogSize(this, 0.8, WindowManager.LayoutParams.WRAP_CONTENT)
        }

        binding.txtIdentifyContent.setOnClickListener {
            dialog_identify.show()
            binding_identify.dialogExitBtn.setOnClickListener {
                // 다이얼로그 닫기
                dialog_identify.dismiss()
            }
        }


        binding.signUpAllCheckCheckBox.setOnCheckedChangeListener { _, isChecked ->
            // 모든 체크 박스들의 체크 상태를 업데이트
            ageCheckBox.isChecked = isChecked
            useCheckBox.isChecked = isChecked
            privacyCheckBox.isChecked = isChecked
            mailCheckBox.isChecked = isChecked
            identifyCheckBox.isChecked = isChecked
        }
        // 체크 박스 상태 변경 시 이벤트 처리
        val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            val isAllChecked =
                ageCheckBox.isChecked && useCheckBox.isChecked && privacyCheckBox.isChecked

            // 버튼 활성화 여부에 따라 색상 변경
            signUpButton.isEnabled = isAllChecked
            if (isAllChecked) {
                signUpButton.setBackgroundColor(Color.BLACK) // 버튼이 활성화된 경우의 색상
            } else {
                signUpButton.setBackgroundColor(Color.GRAY) // 버튼이 비활성화된 경우의 색상
            }
        }

        // 체크 박스들에 이벤트 리스너 등록
        ageCheckBox.setOnCheckedChangeListener(onCheckedChangeListener)
        useCheckBox.setOnCheckedChangeListener(onCheckedChangeListener)
        privacyCheckBox.setOnCheckedChangeListener(onCheckedChangeListener)

        // 초기 상태에서 버튼의 활성화 여부 설정
        val isAllChecked =
            ageCheckBox.isChecked && useCheckBox.isChecked && privacyCheckBox.isChecked
        signUpButton.isEnabled = isAllChecked
        if (isAllChecked) {
            signUpButton.setBackgroundColor(Color.BLACK) // 버튼이 활성화된 경우의 색상
        } else {
            signUpButton.setBackgroundColor(Color.GRAY) // 버튼이 비활성화된 경우의 색상
        }

        //가입하기 버튼 누르면 다이얼로그 표시후 메인으로 이동
        val binding_complete = DialogSignupCompleteBinding.inflate(layoutInflater)
        val dialog_complete = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE) // 타이틀 제거
            setContentView(binding_complete.root) // xml 레이아웃 파일과 연결

            // 다이얼로그의 크기 설정
            setDialogSize(this, 0.8, WindowManager.LayoutParams.WRAP_CONTENT)
        }

        //가입하기 버튼
        binding.signUpSignUpBtn.setOnClickListener {

            if (kakaoEmail != null) {
                // 이메일 동의 항목을 체크한 경우에 대한 처리
                dialog_complete.show()
                binding_complete.signUpCompleteBtn.setOnClickListener {
                    // 메인으로 이동
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            } else {

                val intent = Intent(this, AddEmailDialogActivity::class.java)
                startActivity(intent)

                /*

                // 이메일 동의 항목을 체크하지 않은 경우에 대한 처리
                val binding_addEmail = DialogSignupAddEmailBinding.inflate(layoutInflater)
                val dialog_addEmail = Dialog(this).apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE) // 타이틀 제거
                    setContentView(binding_addEmail.root) // xml 레이아웃 파일과 연결
                    // 다이얼로그의 크기 설정
                    setDialogSize(this, 0.9, WindowManager.LayoutParams.WRAP_CONTENT)
                }

                dialog_addEmail.show()

                StrictMode.setThreadPolicy(
                    StrictMode.ThreadPolicy.Builder()
                        .permitDiskReads()
                        .permitDiskWrites()
                        .permitNetwork()
                        .build()
                )
                binding_addEmail.signUpAddEmailSignUpBtn.setOnClickListener {
                    val email = binding_addEmail.signUpInputEmail.text.toString()
                    val mailServer = SendMail()
                    mailServer.sendSecurityCode(applicationContext,email)
                }

                binding_addEmail.signUpAddEmailSignUpBtn.setOnClickListener{
                    dialog_complete.show()
                    binding_complete.signUpCompleteBtn.setOnClickListener {
                        // 메인으로 이동
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
                */
            }

            }
        }


    //다이얼로그 크기 설정
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
