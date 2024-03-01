package com.example.myo_jib_sa.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.myo_jib_sa.signup.api.MemeberApi
import com.example.myo_jib_sa.signup.api.LoginResponse
import com.example.myo_jib_sa.MainActivity
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.spfManager
import com.example.myo_jib_sa.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var accessToken:String
    // Retrofit 객체 가져오기
    private val retrofit = sRetrofit.create(MemeberApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.kakaoLoginBtn.setOnClickListener {
            kakaoLogin()
        }


    }

    private fun kakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("LOGIN", "Login fail with kakao account", error)
            } else if (token != null) {
                Log.i("LOGIN", "Login success with kakao account : ${token.accessToken}")
                getLogin(token.accessToken!!)
            }
        }
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e("LOGIN", "Login fail with kakaoTalk", error)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {
                    Log.i("LOGIN", "Login success with kakao account : ${token.accessToken}")
                    getLogin(token.accessToken!!)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    private fun autoLogin(jwtToken: String) {
        // 로그인 성공 시 메인 화면으로 이동
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun getLogin(accessToken: String) {
        retrofit.getLogin(accessToken).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    Log.d("getLogin", "${response.body()}")
                    if(response.body()?.isSuccess == true) {
                        // 토큰 설정
                        response.body()!!.result?.let {
                            spfManager.setAccessToken(it.jwtAccessToken)
                            spfManager.setRefreshToken(it.jwtRefreshToken)
                        }
                        // 메인 액티비티로 이동
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this@LoginActivity, SignUpActivity::class.java)
                            .putExtra("kakaoToken", accessToken))
                    }
                } else {
                    Log.e("LoginResponse", "API 호출 실패: $response")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginResponse", "onFailure API 호출 실패: ${t.message}")
            }
        })
    }

}