package com.example.myo_jib_sa.schedule.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.DialogFragmentScheduleDeleteBinding
import com.example.myo_jib_sa.schedule.adapter.ScheduleAdaptar
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.schedule.api.scheduleDelete.ScheduleDeleteResponse
import com.example.myo_jib_sa.schedule.api.scheduleDelete.ScheduleDeleteService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ScheduleDeleteDialogFragment(
    context: Context,
    val scheduleAdaptar: ScheduleAdaptar,
    val position:Int
) : DialogFragment() {
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

    private fun initViews() {

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        //확인
        binding.yesTv.setOnClickListener{

            dismiss()
            scheduleDeleteApi()//: 일정삭제 api

        }
        //취소
        binding.exitBtn.setOnClickListener {
            dismiss()
            //buttonClickListener.onClickExitBtn()
        }
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
        // SharedPreferences 객체 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("getJwt", Context.MODE_PRIVATE)
        // JWT 값 가져오기
        val token = sharedPreferences.getString("jwt", null)

        //val token : String = BuildConfig.API_TOKEN
//        Log.d("retrofit", "token = "+token+"l");

        val sDataList = scheduleAdaptar.getItem()
        val service = RetrofitClient.getInstance().create(ScheduleDeleteService::class.java)
        val listCall = service.scheduleDelete(token, sDataList[position].scheduleId)

        listCall.enqueue(object : Callback<ScheduleDeleteResponse> {
            override fun onResponse(
                call: Call<ScheduleDeleteResponse>,
                response: Response<ScheduleDeleteResponse>
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
            override fun onFailure(call: Call<ScheduleDeleteResponse>, t: Throwable) {
                Log.e("retrofit", "onFailure: ${t.message}")
            }
        })
    }


}