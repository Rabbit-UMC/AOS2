package com.example.myo_jib_sa.Schedule.currentMissionActivity.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemCurrentMissionScheduleDeleteBinding
import com.example.myo_jib_sa.Schedule.currentMissionActivity.api.currentMissionSchedule.CurrentMissionScheduleResult

data class ScheduleDeleteAdapterData(
    var currentMissionScheduleResult: CurrentMissionScheduleResult,
    var selected:Boolean = false
)

class CurrentMissionScheduleDeleteAdapter(private val scheduleList:ArrayList<ScheduleDeleteAdapterData>,
                                          private val height:Int):
    RecyclerView.Adapter<CurrentMissionScheduleDeleteAdapter.ViewHolder>() {


    //화면 설정
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrentMissionScheduleDeleteAdapter.ViewHolder {
        val binding =
            ItemCurrentMissionScheduleDeleteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        binding.root.layoutParams.height = (height*0.088).toInt()
        return ViewHolder(binding)
    }

    //데이터 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scheduleList[position])


        //미션 클릭 이벤트
        holder.itemView.setOnClickListener {
            toggleItemSelected(position);

        }
    }

    inner class ViewHolder(private val binding: ItemCurrentMissionScheduleDeleteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ScheduleDeleteAdapterData) {
            binding.scheduleTitleTv.text = data.currentMissionScheduleResult.scheduleTitle
            binding.scheduleDateTv.text = data.currentMissionScheduleResult.scheduleWhen
            if (data.selected) {
                binding.seletedV.setBackgroundColor(Color.parseColor("#C7DAFA"))
            } else {
                binding.seletedV.setBackgroundColor(Color.parseColor("#D9D9D9"))
            }
            //체크박스 클릭 설정
            binding.seletedV.setOnClickListener {
                //toggleItemSelected(adapterPosition);
            }
        }
    }

    //클릭 처리
    private fun toggleItemSelected(position: Int) {
        if (scheduleList[position].selected) {//선택 해제
            scheduleList[position].selected = false
            notifyItemChanged(position)
        } else {//선택
            scheduleList[position].selected = true
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    //선택된 아이템 스케줄id 반환--나중에 반환값 변경
    fun getSelectedItemScheduleId(): MutableList<Long> {
        var selectedItemPosition: MutableList<Long> = mutableListOf()
        var j = 0 //selectedItemPosition의 index
        for (i in 0 until scheduleList.size) {
            if (scheduleList[i].selected) {
                //selectedItemPosition[j] = missionList[i].currentMissionScheduleResult.scheduleId
                //j++
                selectedItemPosition.add(scheduleList[i].currentMissionScheduleResult.scheduleId)
            }
        }
        return selectedItemPosition
    }
}