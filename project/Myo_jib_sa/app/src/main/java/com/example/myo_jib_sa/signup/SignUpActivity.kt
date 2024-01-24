package com.example.myo_jib_sa.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.myo_jib_sa.MainActivity
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.databinding.*
import com.example.myo_jib_sa.login.api.SignUpTFC
import com.example.myo_jib_sa.signup.api.SignUpRequest
import com.example.myo_jib_sa.signup.api.SignUpResponse
import com.example.myo_jib_sa.mypage.api.GetCheckDuplicationResponse
import com.example.myo_jib_sa.mypage.api.MypageAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var nickname: String = ""
    private var isDuplicationChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners() {
        with(binding) {
            signUpBackBtn.setOnClickListener {
                onBackPressed()
            }

            signUpNicknameEt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    signUpDuplicateBtn.isEnabled =
                        signUpNicknameEt.text.isNotEmpty() && checkNicknameValid(signUpNicknameEt.text.toString())
                }
            })

            // 중복 버튼
            signUpDuplicateBtn.setOnClickListener {
                checkNicknameDuplicate(signUpNicknameEt.text.toString())
            }

            // 체크박스 텍스트 클릭 이벤트
            signUpOldTxt.setOnClickListener {
                signUpOldCheckbox.isChecked = !signUpOldCheckbox.isChecked
                checkSignUpBtnEnable()
            }
            signUpTermsOfUseTxt.setOnClickListener {
                signUpTermsOfUseCheckbox.isChecked = !signUpTermsOfUseCheckbox.isChecked
                checkSignUpBtnEnable()
            }
            signUpPrivacyTxt.setOnClickListener {
                signUpPrivacyCheckbox.isChecked = !signUpPrivacyCheckbox.isChecked
                checkSignUpBtnEnable()
            }
            signUpMailTxt.setOnClickListener {
                signUpMailCheckbox.isChecked = !signUpMailCheckbox.isChecked
            }
            signUpIdentificationTxt.setOnClickListener {
                signUpIdentificationCheckbox.isChecked = !signUpIdentificationCheckbox.isChecked
            }


            // 이용약관 디테일
            signUpOldDetailBtn.setOnClickListener {
                showTermsOfUseDetailDialog(getString(R.string.sine_up_use_old))
            }
            signUpTermsOfUseDetailBtn.setOnClickListener {
                showTermsOfUseDetailDialog(getString(R.string.sine_up_use_old))
            }
            signUpPrivacyDetailBtn.setOnClickListener {
                showTermsOfUseDetailDialog(getString(R.string.sine_up_use_old))
            }
            signUpMailDetailBtn.setOnClickListener {
                showTermsOfUseDetailDialog(getString(R.string.sine_up_use_old))
            }
            signUpIdentificationDetailBtn.setOnClickListener {
                showTermsOfUseDetailDialog(getString(R.string.sine_up_use_old))
            }


            //전체 동의
            signUpWholeCheckbox.setOnCheckedChangeListener { _, isChecked ->
                signUpOldCheckbox.isChecked = isChecked
                signUpTermsOfUseCheckbox.isChecked = isChecked
                signUpPrivacyCheckbox.isChecked = isChecked
                signUpMailCheckbox.isChecked = isChecked
                signUpIdentificationCheckbox.isChecked = isChecked
            }
            signUpWholeTxt.setOnClickListener {
                signUpWholeCheckbox.isChecked = !signUpWholeCheckbox.isChecked
            }

            // 가입하기
            signUpSignUpBtn.setOnClickListener {
                postSignUp()
            }
        }
    }


    private fun checkSignUpBtnEnable() {
        with(binding) {
            signUpSignUpBtn.isEnabled =
                signUpOldCheckbox.isChecked && signUpTermsOfUseCheckbox.isChecked && signUpPrivacyCheckbox.isChecked
                        && isDuplicationChecked
        }
    }
    private fun checkNicknameValid(text: String): Boolean {
        return text.matches(Regex("[a-zA-Z0-9가-힣]{1,6}"))
    }

    private fun checkNicknameDuplicate(nickName: String) {
        sRetrofit.create(MypageAPI::class.java).getCheckDuplication(nickName).enqueue(object : Callback<GetCheckDuplicationResponse> {
            override fun onResponse(call: Call<GetCheckDuplicationResponse>, response: Response<GetCheckDuplicationResponse>) {
                if (response.body() != null) {
                    binding.signUpDuplicateStateTxt.visibility = View.VISIBLE
                    if(response.body()!!.result) {
                        binding.signUpDuplicateStateTxt.apply {
                            text = "사용 가능한 닉네임이에요."
                            setTextColor(getColor(R.color.complete))
                            this@SignUpActivity.nickname = binding.signUpNicknameEt.text.toString()
                            isDuplicationChecked = true
                            checkSignUpBtnEnable()
                        }
                    }
                    else {
                        binding.signUpDuplicateStateTxt.apply {
                            text = "사용 불가능한 닉네임이에요."
                            setTextColor(getColor(R.color.alert))
                            isDuplicationChecked = false
                            checkSignUpBtnEnable()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetCheckDuplicationResponse>, t: Throwable) {
                // 네트워크 요청 실패 처리
                Log.d("checkNicknameDuplicate", "$t")
            }
        })
    }

    private fun showTermsOfUseDetailDialog(desc: String) {
        SignUpTermsOfUseDetailDialog(desc).show(supportFragmentManager, "SignUpDetailDialog")
    }
    private fun postSignUp() {
        sRetrofit.create(SignUpTFC::class.java)
            .postSignUp(SignUpRequest(nickname)).enqueue(object :Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    if(response.body()?.isSuccess == true) {
                        showCompleteDialog()
                    }
                }
                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }

            })
    }
    private fun showCompleteDialog() {
        SignUpCompleteDialog().apply {
            isCancelable = false
            initCompleteListener(object : SignUpCompleteDialog.CompleteListener {
                override fun completeListener() {
                    finishAffinity()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    dismiss()
                }

            })
            show(supportFragmentManager, "SignUpCompleteDialog")
        }
    }

}