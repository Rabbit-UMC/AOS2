package com.example.myo_jib_sa.signup

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.example.myo_jib_sa.MainActivity
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.signUpRetrofit
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.spfManager
import com.example.myo_jib_sa.databinding.*
import com.example.myo_jib_sa.signup.api.MemeberApi
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
    private lateinit var kakaoToken : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kakaoToken = intent.getStringExtra("kakaoToken").toString()
        initListeners()
    }

    private fun initListeners() {
        with(binding) {
            signUpBackBtn.setOnClickListener {
                finish()
            }

            // 가입하기
            signUpSignUpBtn.setOnClickListener {
                postSignUp()
            }
        }

        initNicknameListener()
        initTermsListener()
    }

    private fun initNicknameListener() {
        with(binding) {
            signUpNicknameEt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    signUpDuplicateBtn.isEnabled = isValidNickname(signUpNicknameEt.text.toString())
                    signUpDuplicateStateTxt.visibility = View.GONE
                    isDuplicationChecked = false
                    checkSignUpBtnEnable()
                }
            })

            // 중복 버튼
            signUpDuplicateBtn.setOnClickListener {
                checkNicknameDuplicate(signUpNicknameEt.text.toString())
            }
        }
    }

    private fun initTermsListener() {
        with(binding) {
            // 체크박스 클릭 이벤트
            signUpOldCheckbox.setOnClickListener { checkSignUpBtnEnable() }
            signUpTermsOfUseCheckbox.setOnClickListener { checkSignUpBtnEnable() }
            signUpPrivacyCheckbox.setOnClickListener { checkSignUpBtnEnable() }

            // 체크 박스 텍스트 클릭 이벤트
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
            signUpMarketingTxt.setOnClickListener {
                signUpMarketingCheckbox.isChecked = !signUpMarketingCheckbox.isChecked
            }


            // 이용약관 디테일
            signUpTermsOfUseDetailBtn.setOnClickListener {
                showTermsOfUseDetailDialog("서비스 이용약관", getString(R.string.sign_up_terms_content))
            }
            signUpPrivacyDetailBtn.setOnClickListener {
                showTermsOfUseDetailDialog("개인정보 처리방침", getString(R.string.sign_up_privacy_content))
            }
            signUpMarketingDetailBtn.setOnClickListener {
                showTermsOfUseDetailDialog("마케팅 정보 제공 및 수신",getString(R.string.sign_up_marketing_content))
            }


            //전체 동의
            signUpWholeCheckbox.setOnCheckedChangeListener { _, isChecked ->
                signUpOldCheckbox.isChecked = isChecked
                signUpTermsOfUseCheckbox.isChecked = isChecked
                signUpPrivacyCheckbox.isChecked = isChecked
                signUpMarketingCheckbox.isChecked = isChecked

                checkSignUpBtnEnable()
            }
            signUpWholeTxt.setOnClickListener {
                signUpWholeCheckbox.isChecked = !signUpWholeCheckbox.isChecked
            }
        }
    }


    private fun checkSignUpBtnEnable() {
        with(binding) {
            signUpSignUpBtn.isEnabled =
                signUpOldCheckbox.isChecked && signUpTermsOfUseCheckbox.isChecked
                        && signUpPrivacyCheckbox.isChecked && isDuplicationChecked
        }
    }
    private fun isValidNickname(nickName: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9가-힣]{1,6}$")
        return regex.matches(nickName)
    }

    private fun checkNicknameDuplicate(nickName: String) {
        sRetrofit.create(MypageAPI::class.java)
            .getCheckDuplication(nickName, false)
            .enqueue(object : Callback<GetCheckDuplicationResponse> {
                override fun onResponse(call: Call<GetCheckDuplicationResponse>, response: Response<GetCheckDuplicationResponse>) {
                    if (response.body() != null) {
                        binding.signUpDuplicateStateTxt.visibility = View.VISIBLE
                        if(!response.body()!!.result) {
                            binding.signUpDuplicateStateTxt.apply {
                                text = "사용 가능한 닉네임이에요."
                                setTextColor(getColor(R.color.complete))
                                nickname = binding.signUpNicknameEt.text.toString()
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

    private fun showTermsOfUseDetailDialog(title: String, desc: String) {
        SignUpTermsDetailDialog(title, desc).show(supportFragmentManager, "SignUpDetailDialog")
    }
    private fun postSignUp() {
        signUpRetrofit.create(MemeberApi::class.java)
            .postSignUp(kakaoToken, SignUpRequest(nickname)).enqueue(object :Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    if(response.body()?.isSuccess == true) {
                        showCompleteDialog()
                        response.body()!!.result?.let {
                            spfManager.setAccessToken(it.jwtAccessToken)
                            spfManager.setRefreshToken(it.jwtRefreshToken)
                        }
                    }
                }
                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }

            })
    }
    private fun showCompleteDialog() {
        SignUpCompleteDialog(nickname).apply {
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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}