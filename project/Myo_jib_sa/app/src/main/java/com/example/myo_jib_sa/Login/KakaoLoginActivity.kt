package com.example.myo_jib_sa.login

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.myo_jib_sa.BuildConfig
import com.example.myo_jib_sa.login.api.LoginResponse
import com.example.myo_jib_sa.MainActivity
import com.example.myo_jib_sa.databinding.ActivityKakaoLoginBinding
import com.example.myo_jib_sa.login.api.LoginITFC
import com.example.myo_jib_sa.login.api.RetrofitInstance
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KakaoLoginActivity : AppCompatActivity(),OnEmailEnteredInterface {
    private lateinit var binding: ActivityKakaoLoginBinding
    private lateinit var accessToken:String
    var kakaoEmail: String? = null
    // Retrofit 객체 가져오기
    val retrofit = RetrofitInstance.getInstance().create(LoginITFC::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKakaoLoginBinding.inflate(layoutInflater)
        installSplashScreen()
        setContentView(binding.root)



        // 앱 시작 시 자동 로그인 체크

        val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)

//여기 추가함================================
        val getJwt = sharedPreferences.edit()
// JWT 저장
        val jwt = "eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ.eyJ1c2VySWR4IjoxLCJpYXQiOjE3MDI2NjM2MTgsImV4cCI6MTcwNDEzNDg0N30.va4im_CNjnmyRRZxtPxxHc7v4b2SstL3XLebcJ1EXm0"
//if (jwt != null) {
        getJwt.putString("jwt", jwt)
//}
        getJwt.apply()
//여기 추가함================================

        val jwtToken = sharedPreferences.getString("jwt", null)
        if (jwtToken != null) {
            // 저장된 토큰이 있다면 자동으로 로그인
            autoLogin(jwtToken)
        }




        binding.kakaoLoginBtn.setOnClickListener {


            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@KakaoLoginActivity)) {
                UserApiClient.instance.loginWithKakaoTalk(this@KakaoLoginActivity, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this@KakaoLoginActivity, callback = callback)
            }
        }


    }

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e("kakao", "사용자 정보 요청 실패", error)
                } else if (user != null) {
                    kakaoEmail = user.kakaoAccount?.email
                    accessToken = token.accessToken
                    Log.d("token", accessToken)

                    LoginApi(accessToken)
                    val intent = Intent(this@KakaoLoginActivity, MyoSignUpActivity::class.java)
                    intent.putExtra("accessToken", accessToken)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    Log.d("token", accessToken)

                  /*  if (kakaoEmail != null) {
                        // 카카오이메일 있으면 바로 묘집사 회원가입으로 이동
                        LoginApi(accessToken)
                        val intent = Intent(this@KakaoLoginActivity, MyoSignUpActivity::class.java)
                        intent.putExtra("accessToken", accessToken)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        Log.d("token", accessToken)
                    } else {
                        // 카카오이메일 없으면 추가 이메일 입력 다이얼로그로 이동
                        showAddEmailDialog()
                    }*/
                }
            }
            Log.d("LoginResponse", "카카오계정으로 로그인 성공 ${token.accessToken}")
        }
    }
    private fun autoLogin(jwtToken: String) {
        // 로그인 성공 시 메인 화면으로 이동
        val intent = Intent(this@KakaoLoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun LoginApi(accessToken: String) {

        retrofit.getLogin(accessToken).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        Log.d("LoginResponse", "code: ${it.errorCode}")
                        Log.d("LoginResponse", "message: ${it.errorMessage}")
                        it.result?.let { loginResult ->
                            val jwtToken = loginResult.jwtAccessToken
                            Log.d("LoginResponse", "id: ${loginResult.id}")
                            Log.d("LoginResponse", "jwt: ${jwtToken}")

                            // jwtToken을 sharedPreference에 저장하기
                            val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            // JWT 저장
                            val jwt = jwtToken
                            if (jwt != null) {
                                editor.putString("jwt", jwt)
                            }
                            // userId 저장
                            val userId = loginResult.id // 이 부분을 실제 userId 값으로 바꿔주세요
                            editor.putLong("userId", userId)

                            editor.apply()
                           /* val sharedPreferences = getSharedPreferences("getJwt", Context.MODE_PRIVATE)
                            val getJwt = sharedPreferences.edit()
                            // JWT 저장
                            val jwt ="eyJ0eXBlIjoiand0IiwiYWxnIjoiSFMyNTYifQ.eyJ1c2VySWR4IjoxLCJpYXQiOjE2OTE2NjUwMzIsImV4cCI6MTY5MzEzNjI2MX0.mlOx8WnywdEYGNDHkhlqP3agL3rVMyvwgwWP8VlvsXM"
                            if (jwt != null) {
                                getJwt.putString("jwt", jwt)
                            }
                            getJwt.apply()*/
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginResponse", "API 호출 실패: ${response.code()}, ${response.message()}")
                    Log.e("LoginResponse", "Error Body: $errorBody")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginResponse", "onFail API 호출 실패: ${t.message}")
            }
        })
    }


    // 이메일 입력 완료 시 호출되는 콜백 함수
    override fun onEmailEntered(email: String) {
        // 이메일 값 처리 후 MyoSignUpActivity로 이동
        val intent = Intent(this@KakaoLoginActivity, MyoSignUpActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra("email", email)
        intent.putExtra("accessToken",accessToken)
        startActivity(intent)
    }
    private fun showAddEmailDialog() {
        val dialog = LoginAddEmailDialogFragment(this)
        //dialog.setOnEmailEnteredListener(this)
        dialog.show(supportFragmentManager, "add_email_dialog")
    }

}
