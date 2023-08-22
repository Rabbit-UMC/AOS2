package com.example.myo_jib_sa.Login

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myo_jib_sa.BuildConfig
import com.example.myo_jib_sa.Login.API.*
import com.example.myo_jib_sa.MainActivity
import com.example.myo_jib_sa.databinding.*
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyoSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyoSignUpBinding
    private var returnCode: Int? = null
    private var kakaoEmail: String? = null

    // Retrofit 객체 가져오기
    val retrofit = RetrofitInstance.getInstance().create(LoginITFC::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyoSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // SharedPreferences 객체 가져오기
        val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        // JWT 값 가져오기
        var jwt = sharedPreferences.getString("jwt", null)
        Log.d("signToken","signUpjwt:{$jwt}")

        val ageCheckBox = binding.signUpAgeCheckBox
        val useCheckBox = binding.signUpUseCheckBox
        val privacyCheckBox = binding.signUpPrivacyCheckBox
        val mailCheckBox = binding.signUpMailCheckBox
        val identifyCheckBox = binding.signUpIdentidyCheckBox
        val signUpButton = binding.signUpSignUpBtn

        val userEmail=intent.getStringExtra("email").toString()

        if(jwt==null){
            jwt=intent.getStringExtra("jwtToken").toString()
        }


        Log.d("token","email:{$userEmail}")

        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("kakao", "사용자 정보 요청 실패", error)
            }
            else if (user != null) {
                kakaoEmail=user.kakaoAccount?.email
                Log.d("kakaoM",kakaoEmail.toString())

                //회원가입 API 연결
                if (kakaoEmail != null){
                    if(jwt!=null){
                        signUpUser(jwt.toString(),kakaoEmail,kakaoEmail)
                        Log.d("LoginResponse", "Kakao Email jwt: ${jwt}")
                    }
                    else{
                        Toast.makeText(this@MyoSignUpActivity, "서버 오류 발생", Toast.LENGTH_SHORT).show()
                        Log.d("LoginResponse", "Kakao Email jwt: ${jwt}")
                    }
                }
                //카카오 이메일 입력 받지 않은 경우는 따로 입력 받은 이메일 넣어주기
                else{
                    if(jwt!=null){
                        signUpUser(jwt.toString(),userEmail,userEmail)
                        Log.d("LoginResponse", "User Email jwt: ${jwt}")
                    }
                    else{
                        Toast.makeText(this@MyoSignUpActivity, "서버 오류 발생", Toast.LENGTH_SHORT).show()
                        Log.d("LoginResponse", "User Email jwt: ${jwt}")
                    }
                }
            }
        }




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

            dialog_complete.show()
            binding_complete.signUpCompleteBtn.setOnClickListener {
                // 메인으로 이동
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }

        }
    }

    fun signUpUser(jwtToken:String,email: String?, nickName: String?) {

        // SignUpRequest 객체 생성 및 데이터 설정
        val signUpRequest = SignUpRequest(
            email ?: "",
            nickName ?: "",)

        // Retrofit을 사용한 API 호출
        val call = retrofit.SignUp(jwtToken,signUpRequest)
        call.enqueue(object : Callback<SignUpResponse> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                val signUpResponse = response.body()
                if (response.isSuccessful) {
                    if (signUpResponse != null) {
                        returnCode = signUpResponse.code

                        // 응답 데이터 처리
                        Toast.makeText(this@MyoSignUpActivity, signUpResponse.message, Toast.LENGTH_SHORT).show()

                        Log.d("Retrofit",signUpResponse.message)
                        Log.d("signUp", "userEmail: ${email}")
                        Log.d("signUp", "userNickName: ${nickName}")
                    }
                } else {
                    // 에러 처리
                    val returnMsg =signUpResponse?.message
                    Log.d("Retrofit", returnMsg.toString())
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                // 네트워크 요청 실패 처리
                Toast.makeText(this@MyoSignUpActivity, "네트워크 요청 실패!", Toast.LENGTH_SHORT).show()
            }
        })
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
