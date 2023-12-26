package com.example.myo_jib_sa.Schedule.historyActivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemMissionHistoryBinding
import com.example.myo_jib_sa.Schedule.currentMissionActivity.api.currentMission.HistoryMissionList
import java.text.DecimalFormat

data class MissionData(
    var missionTitle:String,
    var missionSuccessCnt:Int,
    var missionChallengerCnt:Int,
    var missionStartYear:String,
    var missionStartDate:String,
    var missionEndDate:String
)

class MissionHistoryAdapter (private val missionList:ArrayList<HistoryMissionList>,
                             private val width:Int, private val height:Int):
    RecyclerView.Adapter<MissionHistoryAdapter.ViewHolder>() {


    //화면 설정
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemMissionHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.constraintlayout.layoutParams = ConstraintLayout.LayoutParams((width*0.43).toInt(), (width*0.43*0.295).toInt())
        //binding.frameLayout.layoutParams = ConstraintLayout.LayoutParams((width*0.40).toInt(), (height*0.16).toInt())
        return ViewHolder(binding)
    }

    //데이터 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(missionList[position])



    }

    class ViewHolder(private val binding: ItemMissionHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: HistoryMissionList) {

            binding.missionTitleTv.text = data.missionTitle
            binding.successCntTv.text = data.successCnt.toString()
            binding.challengerCntTv.text =data.challengerCnt.toString()
            binding.missionStartYearTv.text = dateFormatter(data.startAt, 1)
            binding.missionStartDateTv.text = dateFormatter(data.startAt, 2)
            binding.missionEndDateTv.text = dateFormatter(data.endAt, 2)
        }
    }

    //클릭 이벤트 처리 ==============================================
    //리스너 인터페이스
    interface OnItemClickListener{
        fun onClick(currentMissionData: MissionData)
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

private fun dateFormatter(date:String, index:Int): String{
    val formatter = DecimalFormat("00")

    val splitDate = date!!.split("-")
    val year = splitDate[0].toInt()
    val month = splitDate[1].toInt()
    val day = splitDate[2].toInt()


    return when (index){
        1->{//년도 반환
            "$year"
        }
        else ->{//월 일 반환환
            "${formatter.format(month)}.${formatter.format(day)}"
        }
    }
}
