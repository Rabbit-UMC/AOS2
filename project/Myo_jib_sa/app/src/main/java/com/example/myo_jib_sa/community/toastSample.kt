package com.example.myo_jib_sa.community

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ToastRedBlackBinding

//todo : 토스트 메시지 생성 오브젝트 만들기
object toastSample {
    /*fun createRedBlackToast(context: Context, message: String): Toast? {
        val inflater = LayoutInflater.from(context)
        val binding: ToastRedBlackBinding =
            DataBindingUtil.inflate(inflater, R.layout.toast_red_black, null, false)

        //텍스트 설정

        return Toast(context).apply {
            setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 16.toPx())
            duration = Toast.LENGTH_LONG
            view = binding.root
        }
    }*/

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}