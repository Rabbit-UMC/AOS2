package com.example.myo_jib_sa.schedule.currentMissionActivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemCurrentMissionBinding

data class CurrentMissionData(
    var missionTitle:String,
    var missionDday:String,
    var missionChallengerCnt:Int,
    var missionImg:Int
)

class CurrentMissionCurrentMissionAdapter (private val missionList:ArrayList<CurrentMissionData>):
    RecyclerView.Adapter<CurrentMissionCurrentMissionAdapter.ViewHolder>() {


    //화면 설정
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemCurrentMissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    //데이터 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(missionList[position])


        //미션 클릭 이벤트
        holder.itemView.setOnClickListener {

        }
    }

    class ViewHolder(private val binding: ItemCurrentMissionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CurrentMissionData) {
            binding.missionTitleTv.text = data.missionTitle
            binding.missionDdayTv.text = data.missionDday
            binding.missionChallengerTv.text = "${data.missionChallengerCnt}명"
            binding.missionImg.setImageResource(data.missionImg)  //이미지 설정
        }
    }


    override fun getItemCount(): Int {
        return missionList.size
    }
}
