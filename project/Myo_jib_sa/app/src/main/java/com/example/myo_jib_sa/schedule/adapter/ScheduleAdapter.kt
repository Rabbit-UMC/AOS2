package com.example.myo_jib_sa.schedule.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemScheduleScheduleBinding
import com.example.myo_jib_sa.schedule.api.ScheduleOfDayResult
import com.example.myo_jib_sa.schedule.utils.Formatter
import java.text.DecimalFormat

class ScheduleAdaptar (
    private val scheduleList:ArrayList<ScheduleOfDayResult>
    ,private val onItemClickListener : OnItemClickListener
): RecyclerView.Adapter<ScheduleAdaptar.ViewHolder>() {

    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScheduleScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    //데이터 설정
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(scheduleList[position])
    }


    inner class ViewHolder(private val binding: ItemScheduleScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: ScheduleOfDayResult) {
            binding.scheduleTitleTv.text = data.scheduleTitle
            binding.scheduleStartTimeTv.text = Formatter().scheduleTimeFormatter(data.scheduleStart)
            binding.scheduleEndTimeTv.text = Formatter().scheduleTimeFormatter(data.scheduleEnd)

            binding.scheduleLayout.setOnClickListener {
                onItemClickListener.onClick(data)
            }

            binding.deleteTv.setOnClickListener {
                onItemClickListener.onDeleteClick(data.scheduleId)
            }
        }
    }

    //클릭 이벤트 처리
    interface  OnItemClickListener{
        fun onClick(scheduleData: ScheduleOfDayResult)
        fun onDeleteClick(scheduleId: Long)
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

}