package com.example.myo_jib_sa.schedule.currentMissionActivity

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.base.MyojibsaApplication.Companion.sRetrofit
import com.example.myo_jib_sa.databinding.DialogFragmentCurrentMissionDeleteBinding
import com.example.myo_jib_sa.schedule.api.DeleteMyMissionNScheduleResponse
import com.example.myo_jib_sa.schedule.api.MissionAPI
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DeleteDialogFragment(
    private var missionId: Long?,
    private val scheduleIdList: MutableList<Long>
) : DialogFragment() {
    private lateinit var binding: DialogFragmentCurrentMissionDeleteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentCurrentMissionDeleteBinding.inflate(inflater, container, false)


        //requestWindowFeature(Window.FEATURE_NO_TITLE)
        initViews()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        resizeDialog()
    }

    private fun initViews() {

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        //확인
        binding.yesTv.setOnClickListener{
            dismiss()
            currentMissionNSchedulesDeleteApi(missionId, scheduleIdList)
//            if (missionId != null)
//                currentMissionDeleteApi(missionId)//미션삭제api
//            if(scheduleIdList.isNotEmpty())
//                scheduleDeleteApi(scheduleIdList)//스케줄 삭제 api
        }
        //취소
        binding.exitTv.setOnClickListener {
            dismiss()
        }
    }

    private fun currentMissionNSchedulesDeleteApi(missionId:Long?, deleteScheduleIdList:List<Long>){
        Log.d("retrofit", "missionId : "+missionId)
        Log.d("retrofit", "deleteScheduleIdList : "+deleteScheduleIdList.joinToString(", "))

        sRetrofit.create(MissionAPI::class.java).deleteMyMissionNSchedule(
            missionId.toString(),
            deleteScheduleIdList.joinToString(", "))
            .enqueue(object : Callback<DeleteMyMissionNScheduleResponse> {
            override fun onResponse(
                call: Call<DeleteMyMissionNScheduleResponse>,
                response: Response<DeleteMyMissionNScheduleResponse>
            ) {
                if (response.isSuccessful) {
                    if(response.body() != null && response.body()!!.isSuccess) {
                        Log.d("retrofit", response.body().toString());
                        var missionFlag = true
                        if(missionId == null) {
                            missionFlag = false
                        }
                        deleteDialogListener.onDeleteListener("선택하신 항목이 삭제되었어요.", missionFlag)
                    }else{
                        deleteDialogListener.onDeleteListener("[ERROR] ${response.body()?.errorMessage}", false)
                        Log.e("retrofit", "currentMissionNSchedulesDeleteApi_onResponse: code: ${response.body()?.errorCode}")
                        Log.e("retrofit", "currentMissionNSchedulesDeleteApi_onResponse: message : ${response.body()?.errorMessage}")
                    }

                    //scheduleAdaptar.removeTask(position)
                }else {
                    Log.e("retrofit", "currentMissionNSchedulesDeleteApi_onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "currentMissionNSchedulesDeleteApi_onResponse: Error Body $errorBody")

                    deleteDialogListener.onDeleteListener("삭제 실패", false)
                }}
            override fun onFailure(call: Call<DeleteMyMissionNScheduleResponse>, t: Throwable) {
                Log.e("retrofit", "currentMissionNSchedulesDeleteApi_onFailure: ${t.message}")
                deleteDialogListener.onDeleteListener("삭제 실패", false)
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

        var height = (deviceWidth * 0.89 * 0.77).toInt()
        var minHeight = ConvertDPtoPX(requireContext(), 248)
        if(minHeight > height){
            params?.height = minHeight
        } else{
            params?.height = height

        }
        params?.width = (deviceWidth * 0.89).toInt()
//        params?.height = (deviceWidth * 0.9 * 1.13).toInt()

        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    //dp -> px
    fun ConvertDPtoPX(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    // 인터페이스
    interface DeleteDialogListener {
        fun onDeleteListener(message:String, missionFlag : Boolean)
    }
    // 클릭 이벤트 설정
    fun setButtonClickListener(deleteDialogListener: DeleteDialogListener) {
        this.deleteDialogListener = deleteDialogListener
    }
    // 클릭 이벤트 실행
    private lateinit var deleteDialogListener: DeleteDialogListener



}