package com.example.myo_jib_sa.schedule.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.schedule.api.scheduleOfDay.ScheduleOfDayResult
import com.example.myo_jib_sa.databinding.ItemScheduleScheduleBinding

class ScheduleAdaptar (private val scheduleList:ArrayList<ScheduleOfDayResult>):
    RecyclerView.Adapter<ScheduleAdaptar.ViewHolder>() {

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
            binding.scheduleStartTimeTv.text = data.scheduleStart
            binding.scheduleEndTimeTv.text = data.scheduleEnd

            binding.scheduleLayout.setOnClickListener {
                itemClickListener.onClick(data)
            }

            binding.deleteTv.setOnClickListener {
                itemClickListener.onDeleteClick(data.scheduleId)
            }
        }
    }

    //클릭 이벤트 처리 ==============================================
    //리스너 인터페이스
    interface  OnItemClickListener{
        fun onClick(scheduleData: ScheduleOfDayResult)
        fun onDeleteClick(scheduleId: Long)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
    //==============================================================


    //swipe시 내부 데이터 값 제거
    fun removeTask(position: Int) {
        scheduleList.removeAt(position)

        notifyDataSetChanged()
    }



    fun getItem():ArrayList<ScheduleOfDayResult> {
        return scheduleList
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }
}