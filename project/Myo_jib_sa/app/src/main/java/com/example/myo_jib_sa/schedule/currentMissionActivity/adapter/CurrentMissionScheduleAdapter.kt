package com.example.myo_jib_sa.schedule.currentMissionActivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemCurrentMissionBinding
import com.example.myo_jib_sa.databinding.ItemCurrentMissionScheduleBinding

data class ScheduleAdapterData(
    var scheduleTitle:String,
    var scheduleDate:String
)

class CurrentMissionScheduleAdapter(private val missionList:ArrayList<ScheduleAdapterData>):
    RecyclerView.Adapter<CurrentMissionScheduleAdapter.ViewHolder>() {


    //화면 설정
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrentMissionScheduleAdapter.ViewHolder {
        val binding =
            ItemCurrentMissionScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrentMissionScheduleAdapter.ViewHolder(binding)
    }

//데이터 설정
override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(missionList[position])


    //미션 클릭 이벤트
    holder.itemView.setOnClickListener {

    }
}

class ViewHolder(private val binding: ItemCurrentMissionScheduleBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: ScheduleAdapterData) {
        binding.scheduleTitleTv.text = data.scheduleTitle
        binding.scheduleDateTv.text =data.scheduleDate

    }
}


override fun getItemCount(): Int {
    return missionList.size
}
}