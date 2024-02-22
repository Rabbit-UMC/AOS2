package com.example.myo_jib_sa.schedule.dialog

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.community.toastSample
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.databinding.DialogFragmentScheduleDetailBinding
import com.example.myo_jib_sa.mission.adapter.MissionRVAdapter
import com.example.myo_jib_sa.schedule.api.ScheduleAPI
import com.example.myo_jib_sa.schedule.api.ScheduleDetailResponse
import com.example.myo_jib_sa.schedule.api.ScheduleDetailResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleDetailDialogFragment(
    private val scheduleId : Long
    , private val onButtonClickListener: OnButtonClickListener
    ) : DialogFragment(){
    val retrofit: ScheduleAPI = sRetrofit.create(ScheduleAPI::class.java)
    private lateinit var binding: DialogFragmentScheduleDetailBinding
    private lateinit var mContext:Context //requireContext 대신 사용
    private var result: ScheduleDetailResult = ScheduleDetailResult(
        scheduleId = 0,
        missionId = 0,
        missionTitle = "",
        scheduleTitle= "",
        startAt= "",
        endAt= "",
        content= "",
        scheduleWhen= ""
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentScheduleDetailBinding.inflate(inflater, container, false)

        //scheduleDetail api연결
        scheduleDetailApi(scheduleId)

        // 레이아웃 배경을 투명하게 해줌
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        //메모 tv scrollable
        binding.scheduleMemoTv.movementMethod = ScrollingMovementMethod()


        CoroutineScope(Dispatchers.Main).launch {
            delay(100)

            binding.editBtn.setOnClickListener {
                Log.d(
                    "debug",
                    "scheduleData!!.missionId: ${result!!.missionId}, scheduleData!!.scheduleId: ${result!!.scheduleId}"
                )

                //sharedPreference저장
                val sharedPreference =
                    requireContext().getSharedPreferences("scheduleData", MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putString("scheduleTitle", binding.scheduleTitleTv.text.toString())
                editor.putString("scheduleDate", result.scheduleWhen)
                editor.putString("missionTitle", binding.missionTitleTv.text.toString())
                editor.putString("scheduleStartTime", result?.startAt)
                editor.putString("scheduleEndTime", result?.endAt)
                editor.putString("scheduleMemo", binding.scheduleMemoTv.text.toString())
                if(binding.scheduleMemoTv.text.toString() == "없음") {
                    editor.putLong("missionId", -1)
                }else{
                    result?.missionId?.let { it1 -> editor.putLong("missionId", it1) }
                }
                result?.scheduleId?.let { it1 -> editor.putLong("scheduleId", it1) }
                editor.apply()

                Log.d("timeDebug", "scheduleStartTime = ${result?.startAt}")
                Log.d("Datedebug", "Detail = ${binding.scheduleDateTv.text.toString()}")

                //buttonClickListener.onClickEditBtn()
                val scheduleEditDialog = ScheduleEditDialogFragment()
                scheduleEditDialogItemClickEvent(scheduleEditDialog)//scheduleEditDialog Item클릭 이벤트 setting
                scheduleEditDialog.show(requireActivity().supportFragmentManager, "ScheduleEditDialog")
            }
        }


        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onResume() {
        super.onResume()
        resizeDialog()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onButtonClickListener.whenDismiss()
        Log.d("debug","dismiss")

    }


    private fun scheduleEditDialogItemClickEvent(dialog: ScheduleEditDialogFragment){
        dialog.setButtonClickListener(object: ScheduleEditDialogFragment.OnButtonClickListener{
            override fun onClickEditBtn(scheduleData: ScheduleDetailResult) {
                Log.d("debug", "일정 상세로 데이터 넘김"+scheduleData.toString())
                result.scheduleTitle = scheduleData.scheduleTitle
                result.scheduleWhen = scheduleData.scheduleWhen
                result.missionTitle = scheduleData.missionTitle
                result.missionId = scheduleData.missionId
                result.startAt = scheduleData.startAt
                result.endAt = scheduleData.endAt
                result.content = scheduleData.content

                Log.d("Datedebug", "DetailCallback = ${result.scheduleWhen}")


                //화면에 반영
                binding.scheduleTitleTv.text = scheduleData.scheduleTitle
                binding.scheduleDateTv.text = scheduleWhenFormatter(scheduleData.scheduleWhen)
                if(scheduleData.missionTitle == null)
                    binding.missionTitleTv.text = "없음"
                else {
                    binding.missionTitleTv.text = scheduleData.missionTitle
                }
                binding.scheduleStartAtTv.text = scheduleTimeFormatter(scheduleData.startAt)
                binding.scheduleEndAtTv.text = scheduleTimeFormatter(scheduleData.endAt)
                binding.scheduleMemoTv.text = scheduleData.content


            }
        })
    }

    //scheduleDetail api연결
    private fun scheduleDetailApi(scheduleId: Long) {
        retrofit.getScheduleDetail(scheduleId).enqueue(object : Callback<ScheduleDetailResponse> {
            override fun onResponse(
                call: Call<ScheduleDetailResponse>,
                response: Response<ScheduleDetailResponse>
            ) {
                if (response.isSuccessful&&response.body()!!.isSuccess) {
                    Log.d("debug", "retrofit: "+response.body().toString());
                    result = response.body()!!.result

                    binding.scheduleTitleTv.text = result!!.scheduleTitle
                    binding.scheduleDateTv.text = scheduleWhenFormatter(result!!.scheduleWhen)
                    if(result!!.missionTitle == null)
                        binding.missionTitleTv.text = "없음"
                    else {
                        binding.missionTitleTv.text = result!!.missionTitle
                    }
                    binding.scheduleStartAtTv.text = scheduleTimeFormatter(result!!.startAt)
                    binding.scheduleEndAtTv.text = scheduleTimeFormatter(result!!.endAt)
                    binding.scheduleMemoTv.text = result!!.content

                } else {
                    Toast.makeText(requireActivity(), "서버 연결 실패", Toast.LENGTH_SHORT)
                    dismiss()
                    Log.e("retrofit", "onResponse: Error Msg ${response.body()!!.errorMessage}")
                    Log.e("retrofit", "onResponse: Error Code ${response.body()!!.errorCode}}")
                }
            }

            override fun onFailure(call: Call<ScheduleDetailResponse>, t: Throwable) {
                Toast.makeText(requireActivity(), "서버 연결 실패", Toast.LENGTH_LONG).show()
                dismiss()
                Log.e("retrofit", "onFailure: ${t.message}")
            }
        })
    }


    //dialog크기 조절
    fun resizeDialog() {
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        val deviceHeight = size.y

        params?.width = (deviceWidth * 0.89).toInt()
        params?.height = (deviceWidth * 0.89 * 0.9).toInt()

        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    //dp -> px
    fun ConvertDPtoPX(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    // 인터페이스
    interface OnButtonClickListener {
        //fun onClickEditBtn()
        fun whenDismiss()
    }

    private fun scheduleWhenFormatter(scheduleWhen: String?): String {
        val date = scheduleWhen!!.split("-")
        val year = date[0]
        val month = date[1]
        val day = date[2]

        return year+"년 "+month+"월 "+day+"일"
    }
    //startTime, endTime 포맷
    fun scheduleTimeFormatter(startAt: String?): String {
        val formatter = DecimalFormat("00")

        val time = startAt!!.split(":")
        val hour = time[0].toInt()
        val minute = time[1].toInt()
        if (hour < 12) {
            return "오전 ${formatter.format(hour)}:${formatter.format(minute)}"
        } else {
            if (hour == 12)
                return "오후 ${formatter.format(hour)}:${formatter.format(minute)}"
            else
                return "오후 ${formatter.format(hour - 12)}:${formatter.format(minute)}"
        }
    }

}
