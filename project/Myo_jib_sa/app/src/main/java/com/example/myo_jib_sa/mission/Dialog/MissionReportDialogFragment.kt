package com.example.myo_jib_sa.mission.Dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.Login.API.RetrofitInstance
import com.example.myo_jib_sa.databinding.DialogMissionReportFragmentBinding
import com.example.myo_jib_sa.mission.api.MissionAPI
import com.example.myo_jib_sa.mission.api.MissionReportResponse
import com.example.myo_jib_sa.mission.api.Mission
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MissionReportDialogFragment(private val item: Mission) : DialogFragment() {
    private lateinit var binding: DialogMissionReportFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogMissionReportFragmentBinding.inflate(inflater, container, false)

        //미션 report api 호출
        val retrofit = RetrofitInstance.getInstance().create(MissionAPI::class.java)

        binding.missionReportNoBtn.setOnClickListener {
            dismiss()
        }


        binding.missionReportYesBtn.setOnClickListener {
            // 신고 api 연결
            retrofit.MissionReport(item.missionId).enqueue(object : Callback<MissionReportResponse> {
                override fun onResponse(call: Call<MissionReportResponse>, response: Response<MissionReportResponse>) {
                    if (response.isSuccessful) {
                        val reportResponse = response.body()
                        if (reportResponse != null) {
                            if (reportResponse.isSuccess) {
                                // 성공적으로 신고가 접수된 경우 처리
                                val message = reportResponse.message
                                // message를 사용하여 필요한 작업 수행
                                Toast.makeText(requireContext(), "신고완료", Toast.LENGTH_SHORT).show()
                                Log.d("report","신고 완료")
                            } else {
                                // 신고 접수에 실패한 경우 처리
                                val message = reportResponse.message
                                // message를 사용하여 실패 메시지 출력 등 필요한 작업 수행
                                Log.d("report",message)

                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // API 호출 실패 처리
                        Log.d("report","API 호출 실패")
                        Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MissionReportResponse>, t: Throwable) {
                    // 네트워크 등의 문제로 API 호출이 실패한 경우 처리
                    Log.d("report","API 호출 실패, on fail")
                    Toast.makeText(requireContext(), "API 호출 실패/on fail", Toast.LENGTH_SHORT).show()
                }
            })

        }

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
        dialog?.let { setDialogSize(it, 0.8, WindowManager.LayoutParams.WRAP_CONTENT) }
    }

    private fun setDialogSize(dialog: Dialog, widthPercentage: Double, height: Int) {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)

        val displayMetrics = resources.displayMetrics
        val dialogWidth = (displayMetrics.widthPixels * widthPercentage).toInt()
        layoutParams.width = dialogWidth
        layoutParams.height = height

        dialog.window?.attributes = layoutParams
    }
}

