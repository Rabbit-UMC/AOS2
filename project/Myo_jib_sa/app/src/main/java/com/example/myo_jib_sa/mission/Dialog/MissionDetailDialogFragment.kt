package com.example.myo_jib_sa.mission.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.BuildConfig
import com.example.myo_jib_sa.Login.API.RetrofitInstance
import com.example.myo_jib_sa.databinding.DialogMissionDetailFragmentBinding
import com.example.myo_jib_sa.mission.API.*
import com.example.myo_jib_sa.mission.MissionItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate

class MissionDetailDialogFragment(private val item: MissionItem) : DialogFragment() {

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
        val retrofit = RetrofitInstance.getInstance().create(MissionITFC::class.java)

        val sharedPreferences = requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val jwt = sharedPreferences.getString("jwt", null)


        //상세보기 api 호출해서 상세보기 내역이랑 바인딩하기
        // API 호출 및 데이터 처리
        val accessToken = jwt.toString()
        val missionId = item.missionId
        Log.d("detail",missionId.toString())

        retrofit.MissionDetail(accessToken, missionId).enqueue(object : Callback<MissionDetailResponse> {
            override fun onResponse(
                call: Call<MissionDetailResponse>,
                response: Response<MissionDetailResponse>
            ) {
                if (response.isSuccessful) {
                    val detailData = response.body()?.result // 첫 번째 상세 데이터 가져오기
                    //날짜 계산
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                    val startDateString = detailData?.startAt
                    val endDateString = detailData?.endAt

                    if (startDateString != null && endDateString != null) {
                        val startDate = dateFormat.parse(startDateString).time
                        val endDate = dateFormat.parse(endDateString).time
                        val calculaterDate = (endDate - startDate) / (24 * 60 * 60 * 1000)
                        binding.missionTotalDateTxt.text = "${calculaterDate}일"
                    } else {
                        Toast.makeText(requireContext(), "null data", Toast.LENGTH_SHORT).show()
                    }
                    // 상세 데이터를 binding에 설정
                    binding.missionSubjectTxt.text = detailData?.title
                    binding.missionCategoryTxt.text = detailData?.categoryTitle
                    binding.missionStartDateTxt.text = detailData?.startAt
                    binding.missionEndDateTxt.text = detailData?.endAt
                    binding.missionMemoTxt.text = detailData?.content

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
            retrofit.MissionWith(accessToken,item.missionId).enqueue(object : Callback<MissionWithResponse> {
                override fun onResponse(call: Call<MissionWithResponse>, response: Response<MissionWithResponse>) {
                    if (response.isSuccessful) {
                        val withResponse = response.body()
                        if (withResponse != null) {
                            if (withResponse.isSuccess) {
                                // 성공적으로 미션 같이하기가 완료된 경우 처리
                                val message = withResponse.message

                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            } else {
                                // 미션 같이하기에 실패한 경우 처리
                                val message = withResponse.message

                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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

    override fun onResume() {
        super.onResume()
        resizeDialog()
        // 다이얼로그의 크기 설정
        //dialog?.let { setDialogSize(it, 0.94, WindowManager.LayoutParams.WRAP_CONTENT) }
    }

    fun resizeDialog() {
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        val deviceHeight = size.y

        var height = (deviceWidth * 0.95 * 1.13).toInt()
        var minHeight = ConvertDPtoPX(requireContext(), 380)
        if(minHeight > height){
            params?.height = minHeight
        } else{
            params?.height = height

        }
        params?.width = (deviceWidth * 0.95).toInt()
//        params?.height = (deviceWidth * 0.9 * 1.13).toInt()

        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    //dp -> px
    fun ConvertDPtoPX(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    private fun setDialogSize(dialog: Dialog, widthPercentage: Double, height: Int) {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)

        val size = Point()
        val deviceWidth = size.x
        val deviceHeight = size.y

        val displayMetrics = resources.displayMetrics
        val dialogWidth = (displayMetrics.widthPixels * widthPercentage).toInt()
        layoutParams.width = dialogWidth
        layoutParams.height = height

        dialog.window?.attributes = layoutParams
    }
}