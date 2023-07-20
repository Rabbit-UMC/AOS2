package com.example.myo_jib_sa.schedule.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.example.myo_jib_sa.databinding.DialogScheduleDeleteBinding
import com.example.myo_jib_sa.schedule.adapter.ScheduleAdaptar
import com.example.myo_jib_sa.schedule.api.RetrofitClient
import com.example.myo_jib_sa.schedule.api.scheduleDelete.ScheduleDeleteResponse
import com.example.myo_jib_sa.schedule.api.scheduleDelete.ScheduleDeleteService
import com.example.myo_jib_sa.schedule.api.scheduleModify.ScheduleModifyRequest
import com.example.myo_jib_sa.schedule.api.scheduleModify.ScheduleModifyResponse
import com.example.myo_jib_sa.schedule.api.scheduleModify.ScheduleModifyService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


var missionId:Long = -1

class scheduleDeleteDialog(
    context: Context,
    val scheduleAdaptar: ScheduleAdaptar,
    val position:Int,
) : Dialog(context) {
    private lateinit var binding: DialogScheduleDeleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DialogScheduleDeleteBinding.inflate(layoutInflater)

        //requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {

        getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val params: WindowManager.LayoutParams = getWindow()!!.getAttributes()

        //params.width = 700
        //params.height = 370

        //getWindow()?.setBackgroundDrawable(R.drawable.view_round_r15);

        binding.yesTv.setOnClickListener{
            dismiss()
            scheduleAdaptar.removeTask(position)
        }
        binding.exitBtn.setOnClickListener {
            dismiss()
            buttonClickListener.onClickExitBtn()
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


    //scheduleDelete api연결
    fun scheduleDeleteApi() {
//        val token : String = App.prefs.token.toString()
//        Log.d("retrofit", "token = "+token+"l");
//
//        val requestBody = ScheduleModifyRequest(
//            title = binding.missionTitleTv.text.toString(),
//            content = binding.scheduleMemoEtv.text.toString() ,//메모
//            startAt = binding.scheduleStartTimeTv.text.toString(),
//            endAt = binding.scheduleEndTimeTv.text.toString(),
//            missionId = 1 // 어떻게 처리할지 고민해보기
//        )

        val service = RetrofitClient.getInstance().create(ScheduleDeleteService::class.java)
        val listCall = service.scheduleDelete(missionId)

        listCall.enqueue(object : Callback<ScheduleDeleteResponse> {
            override fun onResponse(
                call: Call<ScheduleDeleteResponse>,
                response: Response<ScheduleDeleteResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("retrofit", response.body().toString());
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