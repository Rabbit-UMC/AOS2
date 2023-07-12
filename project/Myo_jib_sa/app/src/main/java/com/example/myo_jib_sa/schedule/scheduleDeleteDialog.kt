package com.example.myo_jib_sa.schedule

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
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
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        
        binding.yesTv.setOnClickListener{
            dismiss()
            scheduleAdaptar.removeTask(position)
        }
        binding.exitBtn.setOnClickListener {
            dismiss()
        }
    }
}