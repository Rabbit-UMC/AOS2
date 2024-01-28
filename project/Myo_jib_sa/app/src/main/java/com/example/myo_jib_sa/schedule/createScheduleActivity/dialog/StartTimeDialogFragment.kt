package com.example.myo_jib_sa.schedule.createScheduleActivity.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.example.myo_jib_sa.databinding.DialogFragmentStartTimeBinding
import java.text.DecimalFormat

class StartTimeDialogFragment(
    private var setOnClickListener : SetOnClickListener,
    var startHour: Int?,
    var startMinute: Int?) : DialogFragment() {
   private lateinit var binding:DialogFragmentStartTimeBinding

    interface SetOnClickListener{
        fun onClick(startHour : Int, startMinute : Int)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentStartTimeBinding.inflate(inflater, container, false)
        // 레이아웃 배경을 투명하게 해줌
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        isCancelable = false //외부 클릭시 dismiss 막기


        val formatter = DecimalFormat("00")

        //timepicker 초기값 설정
        if(startHour == null) {
            binding.startTimePicker.hour = 9
            binding.startTimePicker.minute = 0
            startHour = 9
            startMinute = 0
        }else{
            binding.startTimePicker.hour = startHour as Int
            binding.startTimePicker.minute = startMinute!!
        }
        binding.startTimePicker.descendantFocusability =
            NumberPicker.FOCUS_BLOCK_DESCENDANTS //editText가 눌리는 것을 막는다
        binding.startTimePicker.setOnTimeChangedListener(object: TimePicker.OnTimeChangedListener {
            override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
                Log.d("debug", "startTime : 시:분 | ${formatter.format(hourOfDay)} : ${formatter.format(minute)}")
                startHour = hourOfDay
                startMinute = minute
            }
        })

        binding.startTimeCompleteTv.setOnClickListener {
            setOnClickListener.onClick(startHour!!, startMinute!!)
            dismiss()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        resizeDialog()
    }

    private fun resizeDialog() {
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        val deviceHeight = size.y

        params?.width = deviceWidth.toInt()

        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }
    
}