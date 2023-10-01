package com.example.myo_jib_sa.Login

import android.app.Application
import android.util.Log
import com.example.myo_jib_sa.BuildConfig
import com.example.myo_jib_sa.R
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility

class KakaoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
//        val keyHash = Utility.getKeyHash(this)
//        Log.d("Hash", keyHash)
        val nativeKey =BuildConfig.NATIVE_APP_KEY
        KakaoSdk.init(this, nativeKey)


    }
}