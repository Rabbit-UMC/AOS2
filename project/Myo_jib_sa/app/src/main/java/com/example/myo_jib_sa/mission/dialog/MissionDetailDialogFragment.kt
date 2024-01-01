package com.example.myo_jib_sa.mission.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.login.api.RetrofitInstance
import com.example.myo_jib_sa.databinding.DialogMissionDetailFragmentBinding
import com.example.myo_jib_sa.mission.api.*
import com.example.myo_jib_sa.mission.api.Mission
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MissionDetailDialogFragment(private val item: Mission) : DialogFragment() {

    private lateinit var binding:DialogMissionDetailFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogMissionDetailFragmentBinding.inflate(inflater, container, false)

        //다이얼로그 모서리 둥글게 적용
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        //미션 report api 호출
        val retrofit = RetrofitInstance.getInstance().create(MissionAPI::class.java)

        val sharedPreferences = requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val jwt = sharedPreferences.getString("jwt", null)


        //상세보기 api 호출해서 상세보기 내역이랑 바인딩하기
        // API 호출 및 데이터 처리
        val accessToken = jwt.toString()
        val missionId = item.missionId
        Log.d("home","{상세보기 ID: $missionId.toString()}")

        retrofit.MissionDetail(accessToken, missionId).enqueue(object : Callback<MissionDetailResponse> {
            override fun onResponse(
                call: Call<MissionDetailResponse>,
                response: Response<MissionDetailResponse>
            ) {
                if (response.isSuccessful) {
                    val detailData = response.body()?.result // 첫 번째 상세 데이터 가져오기

                    // 상세 데이터를 binding에 설정
                    binding.missionDetailCategoryTxt.text = detailData?.title
                    binding.missionDetailTitleTxt.text = detailData?.categoryTitle
                    binding.missionDetailStartDateTxt.text = detailData?.startAt
                    binding.missionDetailEndDateTxt.text = detailData?.endAt
                    binding.missionDetailMemoTitleTxt.text = detailData?.content

                } else {
                    // API 호출 실패 시 처리
                    Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MissionDetailResponse>, t: Throwable) {
                // API 호출 실패 시 처리
                Toast.makeText(requireContext(), "API 호출 실패/on fail", Toast.LENGTH_SHORT).show()
            }
        })

        binding.missionWithBtn.setOnClickListener {
            // 미션 같이하기 api 연결 로직
            Log.d("home","{같이하기 ID: $missionId.toString()}")
            retrofit.MissionWith(accessToken,item.missionId).enqueue(object : Callback<MissionWithResponse> {
                override fun onResponse(call: Call<MissionWithResponse>, response: Response<MissionWithResponse>) {
                    if (response.isSuccessful) {
                        val withResponse = response.body()
                        if (withResponse != null) {
                            if (withResponse.isSuccess) {
                                // 성공적으로 미션 같이하기가 완료된 경우 처리
                                val message = withResponse.message
                                val result = withResponse.result

                                Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
                            } else {
                                // 미션 같이하기에 실패한 경우 처리
                                val message = withResponse.message
                                val result = withResponse.result

                                Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // API 호출 실패 처리
                        Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MissionWithResponse>, t: Throwable) {
                    // 네트워크 등의 문제로 API 호출이 실패한 경우 처리
                    Toast.makeText(requireContext(), "API 호출 실패/on fail", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.dialogExitBtn.setOnClickListener{
            dismiss()
        }


        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

}