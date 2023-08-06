package com.example.myo_jib_sa.schedule.currentMissionActivity.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemCurrentMissionBinding
import com.example.myo_jib_sa.databinding.ItemCurrentMissionScheduleBinding
import com.example.myo_jib_sa.databinding.ItemCurrentMissionScheduleDeleteBinding

data class ScheduleDeleteAdapterData(
    var scheduleTitle:String,
    var scheduleDate:String,
    var scheduleId:Long,
    var selected:Boolean = false
)

class CurrentMissionScheduleDeleteAdapter(private val missionList:ArrayList<ScheduleDeleteAdapterData>):
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
        return ViewHolder(binding)
    }

    //데이터 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(missionList[position])


        //미션 클릭 이벤트
        holder.itemView.setOnClickListener {

        }
    }

    inner class ViewHolder(private val binding: ItemCurrentMissionScheduleDeleteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ScheduleDeleteAdapterData) {
            binding.scheduleTitleTv.text = data.scheduleTitle
            binding.scheduleDateTv.text = data.scheduleDate
            if (data.selected) {
                binding.seletedV.setBackgroundColor(Color.parseColor("#C7DAFA"))
            } else {
                binding.seletedV.setBackgroundColor(Color.parseColor("#D9D9D9"))
            }
            //체크박스 클릭 설정
            binding.seletedV.setOnClickListener {
                toggleItemSelected(adapterPosition);
            }
        }
    }

    //클릭 처리
    private fun toggleItemSelected(position: Int) {
        if (missionList[position].selected) {//선택 해제
            missionList[position].selected = false
            notifyItemChanged(position)
        } else {//선택
            missionList[position].selected = true
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return missionList.size
    }

    //선택된 아이템 스케줄id 반환--나중에 반환값 변경
    private fun getSelectedItemPosition(): MutableList<Long> {
        var selectedItemPosition: MutableList<Long> = mutableListOf()
        var j = 0 //selectedItemPosition의 index
        for (i in 0 until missionList.size) {
            if (missionList[i].selected) {
                selectedItemPosition[j] = missionList[i].scheduleId
                j++
            }
        }
        return selectedItemPosition
    }
}