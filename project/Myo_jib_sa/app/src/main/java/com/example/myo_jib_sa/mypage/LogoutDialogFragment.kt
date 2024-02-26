package com.example.myo_jib_sa.mypage

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.base.MyojibsaApplication
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.spfManager
import com.example.myo_jib_sa.databinding.DialogLogoutFragmentBinding
import com.example.myo_jib_sa.databinding.DialogMissionReportFragmentBinding
import com.example.myo_jib_sa.databinding.DialogUnregisterFragmentBinding
import com.example.myo_jib_sa.mission.api.Mission
import com.example.myo_jib_sa.mission.api.MissionAPI
import com.example.myo_jib_sa.mission.api.MissionReportResponse
import com.example.myo_jib_sa.mypage.api.LogoutResponse
import com.example.myo_jib_sa.mypage.api.MypageAPI
import com.example.myo_jib_sa.mypage.api.UnregisterResponse
import com.example.myo_jib_sa.signup.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogoutDialogFragment() : DialogFragment() {
    private lateinit var binding: DialogLogoutFragmentBinding
    private val retrofit = MyojibsaApplication.sRetrofit.create(MypageAPI::class.java)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogLogoutFragmentBinding.inflate(inflater, container, false)

        initListener()

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onResume() {
        super.onResume()
        // 다이얼로그의 크기 설정
        setDialogSize()
    }

    private fun setDialogSize() {
        dialog?.let { dialog ->
            val metrics = resources.displayMetrics
            val density = metrics.density
            val marginPx = (DIALOG_MARGIN_DP * density * 2).toInt()
            val width = metrics.widthPixels - marginPx
            val height = (DIALOG_HEIGHT_DP * density).toInt()

            val layoutParams = WindowManager.LayoutParams().apply {
                copyFrom(dialog.window?.attributes)
                this.width = width
                this.height = height
            }

            dialog.window?.apply {
                attributes = layoutParams
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }

    private fun initListener() {
        binding.logoutNoBtn.setOnClickListener {
            dismiss()
        }

        binding.logoutYesBtn.setOnClickListener {
            retrofit.getLogout(spfManager.getAccessToken()).enqueue(object :
                Callback<LogoutResponse> {
                override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                    if (response.isSuccessful) {
                        val logoutResponse = response.body()
                        if (logoutResponse != null) {
                            if (logoutResponse.isSuccess) {
                                spfManager.spfClear()
                                dismiss()
                                activity?.finish()
                                startActivity(Intent(activity, LoginActivity::class.java))
                            } else {
                                Log.d("report",logoutResponse.errorMessage)
                            }
                        }
                    } else {
                        // API 호출 실패 처리
                        Log.d("unregister","API 호출 실패")
                    }
                }

                override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                    // 네트워크 등의 문제로 API 호출이 실패한 경우 처리
                    Log.d("unregister","onFailure : $t")
                }
            })
        }
    }

    companion object {
        const val DIALOG_MARGIN_DP = 20
        const val DIALOG_HEIGHT_DP = 248
    }
}