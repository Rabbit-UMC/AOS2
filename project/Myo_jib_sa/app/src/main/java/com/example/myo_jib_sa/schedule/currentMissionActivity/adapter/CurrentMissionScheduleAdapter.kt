package com.example.myo_jib_sa.schedule.currentMissionActivity.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemScheduleScheduleBinding
import com.example.myo_jib_sa.databinding.ItemSubScheduleBinding
import com.example.myo_jib_sa.schedule.currentMissionActivity.api.currentMissionSchedule.CurrentMissionScheduleResult


data class ScheduleDeleteAdapterData(
    var currentMissionScheduleResult: CurrentMissionScheduleResult,
    var selected :Boolean = false
)
class CurrentMissionScheduleAdapter(private val scheduleList:ArrayList<ScheduleDeleteAdapterData>,
                                    private val onItemClickListener: OnItemClickListener,
                                    private val height:Int):
    RecyclerView.Adapter<CurrentMissionScheduleAdapter.ViewHolder>() {


    interface OnItemClickListener {
        fun onClick(itemBinding: ItemSubScheduleBinding, goingDelete: Boolean)
    }


    //화면 설정
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemSubScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        binding.root.layoutParams.height = (height * 0.088).toInt()
        return ViewHolder(binding)
    }

    //데이터 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scheduleList[position])
    }

    inner class ViewHolder(private val binding: ItemSubScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ScheduleDeleteAdapterData) {
            binding.scheduleTitleTv.text = data.currentMissionScheduleResult.scheduleTitle

            //스케줄 클릭 이벤트
            binding.scheduleLayout.setOnClickListener {
                Log.d("debug", ""+position+" : "+data.selected)
                data.selected = !data.selected; //클릭 flag
                onItemClickListener.onClick(binding, data.selected)
            }

            if(!data.selected) {
                binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
            else {
                binding.root.setBackgroundColor(Color.parseColor("#1A234BD9"))
            }
        }
    }


    override fun getItemCount(): Int {
        return scheduleList.size
    }

    //선택된 아이템 스케줄id 반환--나중에 반환값 변경
    fun getSelectedItemScheduleId(): MutableList<Long> {
        var selectedScheduleId: MutableList<Long> = mutableListOf()
        var j = 0 //selectedItemPosition의 index
        for (i in 0 until scheduleList.size) {
            if (scheduleList[i].selected) {
                //selectedItemPosition[j] = missionList[i].currentMissionScheduleResult.scheduleId
                //j++
                selectedScheduleId.add(scheduleList[i].currentMissionScheduleResult.scheduleId)
            }
        }
        return selectedScheduleId
    }
}