package com.example.myo_jib_sa.schedule.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import com.example.myo_jib_sa.databinding.DialogScheduleDeleteBinding
import com.example.myo_jib_sa.schedule.adapter.ScheduleAdaptar

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
        }
    }
}