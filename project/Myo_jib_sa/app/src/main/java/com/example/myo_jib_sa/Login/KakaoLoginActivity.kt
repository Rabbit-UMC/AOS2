package com.example.myo_jib_sa.Login

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.myo_jib_sa.MainActivity
import com.example.myo_jib_sa.databinding.ActivityKakaoLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

class KakaoLoginActivity : AppCompatActivity(),OnEmailEnteredInterface {
    private lateinit var binding: ActivityKakaoLoginBinding

    var kakaoEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKakaoLoginBinding.inflate(layoutInflater)
        installSplashScreen()
        setContentView(binding.root)

        binding.kakaoLoginBtn.setOnClickListener {
            /*
            val intent = Intent(this@KakaoLoginActivity, MainActivity::class.java)
            startActivity(intent)
            */

            //kakao로그인

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
                    Log.d("kakaoM", kakaoEmail.toString())
                    if (kakaoEmail != null) {
                        // 카카오이메일 있으면 바로 묘집사 회원가입으로 이동
                        val intent = Intent(this@KakaoLoginActivity, MyoSignUpActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // 카카오이메일 없으면 추가 이메일 입력 다이얼로그로 이동
                        showAddEmailDialog()
                    }
                }
            }
            Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
        }
    }

    // 이메일 입력 완료 시 호출되는 콜백 함수
    override fun onEmailEntered(email: String) {
        // 이메일 값 처리 후 MyoSignUpActivity로 이동
        val intent = Intent(this@KakaoLoginActivity, MyoSignUpActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }
    private fun showAddEmailDialog() {
        val dialog = LoginAddEmailDialogFragment(this)
        //dialog.setOnEmailEnteredListener(this)
        dialog.show(supportFragmentManager, "add_email_dialog")
    }

}