package com.example.myo_jib_sa.schedule.currentMissionActivity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemCurrentMissionBinding
import com.example.myo_jib_sa.schedule.adapter.CalendarData

data class CurrentMissionData(
    var missionTitle:String,
    var missionDday:String,
    var missionChallengerCnt:Int,
    var missionImg:Int,
    var missionId:Long
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
            itemClickListener.onClick(missionList[position])
        }

        //미션 롱클릭 이벤트
        holder.itemView.setOnLongClickListener {
            // 사용자 정의 동작 구현
            itemClickListener.onLongClick(position)
            true // 반드시 true를 반환해야 합니다.
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

    //클릭 이벤트 처리 ==============================================
    //리스너 인터페이스
    interface OnItemClickListener{
        fun onClick(currentMissionData: CurrentMissionData)
        fun onLongClick(position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
    //==============================================================



    override fun getItemCount(): Int {
        return missionList.size
    }
}
