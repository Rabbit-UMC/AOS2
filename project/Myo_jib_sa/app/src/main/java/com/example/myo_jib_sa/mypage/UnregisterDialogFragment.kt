package com.example.myo_jib_sa.mypage

import android.app.Dialog
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
import com.example.myo_jib_sa.databinding.DialogMissionReportFragmentBinding
import com.example.myo_jib_sa.databinding.DialogUnregisterFragmentBinding
import com.example.myo_jib_sa.mission.api.Mission
import com.example.myo_jib_sa.mission.api.MissionAPI
import com.example.myo_jib_sa.mission.api.MissionReportResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UnregisterDialogFragment() : DialogFragment() {
    private lateinit var binding: DialogUnregisterFragmentBinding
    private val retrofit = MyojibsaApplication.sRetrofit.create(MissionAPI::class.java)
    companion object {
        const val DIALOG_MARGIN_DP = 20
        const val DIALOG_HEIGHT_DP = 248
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogUnregisterFragmentBinding.inflate(inflater, container, false)

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
        //미션 report api 호출
        binding.unregisterNoBtn.setOnClickListener {
            dismiss()
        }

        binding.unregisterYesBtn.setOnClickListener {
            // 신고 api 연결
            /*retrofit.postMissionReport(item.missionId).enqueue(object :
                Callback<MissionReportResponse> {
                override fun onResponse(call: Call<MissionReportResponse>, response: Response<MissionReportResponse>) {
                    if (response.isSuccessful) {
                        val reportResponse = response.body()
                        if (reportResponse != null) {
                            if (reportResponse.isSuccess) {
                            } else {
                                Log.d("report",reportResponse.errorMessage)
                            }
                        }
                    } else {
                        // API 호출 실패 처리
                        Log.d("report","API 호출 실패")
                    }
                }

                override fun onFailure(call: Call<MissionReportResponse>, t: Throwable) {
                    // 네트워크 등의 문제로 API 호출이 실패한 경우 처리
                    Log.d("report","onFailure : $t")
                }
            })*/

        }
    }
}