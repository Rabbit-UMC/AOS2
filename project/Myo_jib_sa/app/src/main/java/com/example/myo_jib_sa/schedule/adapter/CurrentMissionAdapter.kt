package com.example.myo_jib_sa.schedule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ItemScheduleCurrentMissionBinding
import com.example.myo_jib_sa.schedule.api.MyMissionResult

class CurrentMissionAdapter(private val missionList:MutableList<MyMissionResult>):
    RecyclerView.Adapter<CurrentMissionAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemScheduleCurrentMissionBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(data: MyMissionResult) {
            binding.missionTitleTv.text = data.missionTitle
            binding.missionNumOfchallengerTv.text = "${data.challengerCnt}명"
            binding.missionDDayTv.text = "${data.dday}"
        }
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding:ItemScheduleCurrentMissionBinding
        = ItemScheduleCurrentMissionBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(missionList[position])
    }

    override fun getItemCount(): Int {
        return missionList.size
    }
}