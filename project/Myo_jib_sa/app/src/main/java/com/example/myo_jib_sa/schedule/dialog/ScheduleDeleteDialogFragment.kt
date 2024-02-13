package com.example.myo_jib_sa.schedule.dialog

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
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.databinding.DialogFragmentScheduleDeleteBinding
import com.example.myo_jib_sa.schedule.adapter.ScheduleAdaptar
import com.example.myo_jib_sa.schedule.api.DeleteScheduleResponse
import com.example.myo_jib_sa.schedule.api.ScheduleAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ScheduleDeleteDialogFragment(
    val scheduleId:Long
) : DialogFragment() {
    val retrofit:ScheduleAPI = sRetrofit.create(ScheduleAPI::class.java)
    private lateinit var binding: DialogFragmentScheduleDeleteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentScheduleDeleteBinding.inflate(inflater, container, false)

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
        isCancelable = false;//외부 터치 금지

        //확인
        binding.yesTv.setOnClickListener{
            dismiss()
            scheduleDeleteApi()//: 일정삭제 api
        }

        //취소
        binding.exitTv.setOnClickListener {
            dismiss()
            buttonClickListener.onClickExitBtn()
        }
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
    interface OnButtonClickListener {
        fun onClickExitBtn()
    }
    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }
    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener




    //scheduleDelete api연결: 일정삭제
    fun scheduleDeleteApi() {
        retrofit.deleteSchedule(scheduleId).enqueue(object : Callback<DeleteScheduleResponse> {
            override fun onResponse(
                call: Call<DeleteScheduleResponse>,
                response: Response<DeleteScheduleResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", response.body().toString());
                    //scheduleAdaptar.removeTask(position)
                    buttonClickListener.onClickExitBtn()
                }else {
                    Log.e("retrofit", "onResponse: Error ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("retrofit", "onResponse: Error Body $errorBody")
                }}
            override fun onFailure(call: Call<DeleteScheduleResponse>, t: Throwable) {
                Log.e("retrofit", "onFailure: ${t.message}")
            }
        })
    }


}