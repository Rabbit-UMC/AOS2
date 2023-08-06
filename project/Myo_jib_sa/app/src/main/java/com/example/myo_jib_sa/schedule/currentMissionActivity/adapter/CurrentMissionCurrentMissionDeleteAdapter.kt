package com.example.myo_jib_sa.schedule.currentMissionActivity.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemCurrentMissionDeleteBinding

data class CurrentMissionDeleteData(
    var missionTitle:String,
    var missionDday:String,
    var missionChallengerCnt:Int,
    var missionImg:Int,
    var missionId:Long,
    var selected:Boolean = false
)

class CurrentMissionCurrentMissionDeleteAdapter (private val missionList:ArrayList<CurrentMissionDeleteData>):
    RecyclerView.Adapter<CurrentMissionCurrentMissionDeleteAdapter.ViewHolder>() {


    //화면 설정
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemCurrentMissionDeleteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
            //itemClickListener.onLongClick(position)
            true // 반드시 true를 반환해야 합니다.
        }
    }

    inner class ViewHolder(private val binding: ItemCurrentMissionDeleteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CurrentMissionDeleteData) {
            binding.missionTitleTv.text = data.missionTitle
            binding.missionDdayTv.text = data.missionDday
            binding.missionChallengerTv.text = "${data.missionChallengerCnt}명"
            binding.missionImg.setImageResource(data.missionImg)  //이미지 설정
            //체크박스 설정
            if(data.selected){
                binding.seletedV.setBackgroundColor(Color.parseColor("#C7DAFA"))
            }else{
                binding.seletedV.setBackgroundColor(Color.parseColor("#D9D9D9"))
            }
            //체크박스 클릭 설정
            binding.seletedV.setOnClickListener {
                toggleItemSelected(adapterPosition);
            }
        }
    }

    //클릭 이벤트 처리 ==============================================
    //리스너 인터페이스
    interface OnItemClickListener{
        fun onClick(currentMissionData: CurrentMissionDeleteData)
        //fun onLongClick(position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
    //==============================================================


    //클릭시 화면 처리
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

    //선택된 아이템 미션id 반환--나중에 반환값 변경
    private fun getSelectedItemPosition(): MutableList<Long>{
        var selectedItemPosition : MutableList<Long> = mutableListOf()
        var j = 0 //selectedItemPosition의 index
        for(i in 0 until missionList.size){
            if(missionList[i].selected){
                selectedItemPosition[j] = missionList[i].missionId
                j++
            }
        }
        return selectedItemPosition
    }
}
