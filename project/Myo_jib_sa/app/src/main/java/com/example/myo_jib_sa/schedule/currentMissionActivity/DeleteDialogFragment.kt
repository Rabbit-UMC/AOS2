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
import com.example.myo_jib_sa.databinding.DialogFragmentCurrentMissionDeleteBinding
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.schedule.api.scheduleDelete.ScheduleDeleteResponse
import com.example.myo_jib_sa.schedule.api.scheduleDelete.ScheduleDeleteService
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionDelete.CurrentMissionDeleteResponse
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionDelete.CurrentMissionDeleteService
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DeleteDialogFragment(
    private val missionId: Long?,
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
            if (missionId != null)
                currentMissionDeleteApi(missionId)//미션삭제api
            if(scheduleIdList.isNotEmpty())
                scheduleDeleteApi(scheduleIdList)//스케줄 삭제 api
        }
        //취소
        binding.exitTv.setOnClickListener {
            dismiss()
        }
    }

    //currentMission api연결
    private fun currentMissionDeleteApi(missionId:Long) {
        // JWT 값 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", null)

        val service = RetrofitClient.getInstance().create(CurrentMissionDeleteService::class.java)
//        val url ="app/mission/my-missions/${deleteMissionIdList.joinToString(",")}"
//        Log.d("debug", "$url");
//        val listCall = service.currentMissionDelete(token, url)

        val listCall = service.currentMissionDelete(token, missionId)
        listCall.enqueue(object : Callback<CurrentMissionDeleteResponse> {
            override fun onResponse(
                call: Call<CurrentMissionDeleteResponse>,
                response: Response<CurrentMissionDeleteResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", "retrofit: "+response.body().toString());
                    val result = response.body()!!.result
                    deleteDialogListener.onDeleteListener("선택하신 항목이 삭제되었어요.", true)

                } else {
                    Log.e("retrofit", "currentMissionDeleteApi_onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "currentMissionDeleteApi_onResponse: Error Body $errorBody")
                    deleteDialogListener.onDeleteListener("미션 삭제 실패", true)

                }
            }
            override fun onFailure(call: Call<CurrentMissionDeleteResponse>, t: Throwable) {
                Log.e("retrofit", "currentMissionDeleteApi_onFailure: ${t.message}")
                deleteDialogListener.onDeleteListener("미션 삭제 실패", true)

            }
        })
    }

    //scheduleDelete api연결: 일정삭제
    private fun scheduleDeleteApi(deleteScheduleIdList:List<Long>) {
        // JWT 값 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", null)

        val service = RetrofitClient.getInstance().create(ScheduleDeleteService::class.java)
//        val url ="app/schedule/${deleteScheduleIdList.joinToString(", ")}"
//        Log.d("debug", "$url");
//        val listCall = service.scheduleDeleteModifyVer(token, url)
        val listCall = service.scheduleDeleteModifyVer(token, deleteScheduleIdList.joinToString(", "))

        listCall.enqueue(object : Callback<ScheduleDeleteResponse> {
            override fun onResponse(
                call: Call<ScheduleDeleteResponse>,
                response: Response<ScheduleDeleteResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", response.body().toString());



                        deleteDialogListener.onDeleteListener("선택하신 항목이 삭제되었어요.", false)


                    //scheduleAdaptar.removeTask(position)
                }else {
                    Log.e("retrofit", "scheduleDeleteApi_onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "scheduleDeleteApi_onResponse: Error Body $errorBody")

                    deleteDialogListener.onDeleteListener("일정 삭제 실패", false)
                }}
            override fun onFailure(call: Call<ScheduleDeleteResponse>, t: Throwable) {
                Log.e("retrofit", "scheduleDeleteApi_onFailure: ${t.message}")
                deleteDialogListener.onDeleteListener("일정 삭제 실패", false)
            }
        })
    }
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()


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