package com.example.myo_jib_sa.Login

import android.app.Application
import com.example.myo_jib_sa.R
import com.kakao.sdk.common.KakaoSdk

class KakaoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, getString(R.string.kakao_native_key))
    }
}