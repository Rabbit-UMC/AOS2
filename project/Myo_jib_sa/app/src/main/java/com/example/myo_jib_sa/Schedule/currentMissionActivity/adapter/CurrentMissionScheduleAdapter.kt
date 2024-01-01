package com.example.myo_jib_sa.Schedule.currentMissionActivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemScheduleScheduleBinding
import com.example.myo_jib_sa.Schedule.currentMissionActivity.api.currentMissionSchedule.CurrentMissionScheduleResult


data class ScheduleAdapterData(
    var currentMissionScheduleResult: CurrentMissionScheduleResult,
    var goingDelete :Boolean = false
)
class CurrentMissionScheduleAdapter(private val scheduleList:ArrayList<ScheduleAdapterData>,
                                    private val height:Int):
    RecyclerView.Adapter<CurrentMissionScheduleAdapter.ViewHolder>() {

    //클릭 이벤트 처리 ==============================================
//리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(scheduleItem: ConstraintLayout, isClicked: Boolean)
    }

    //(3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    // (4) setItemClickListener로 설정한 함수 실행
    lateinit private var itemClickListener: OnItemClickListener
    //==============================================================

    //화면 설정
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemScheduleScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.layoutParams.height = (height * 0.088).toInt()
        return ViewHolder(binding)
    }

    //데이터 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scheduleList[position])


    }

    private var isClicked = false

    inner class ViewHolder(private val binding: ItemScheduleScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ScheduleAdapterData) {
            binding.scheduleTitleTv.text = data.currentMissionScheduleResult.scheduleTitle
            //binding.sch.text =data.scheduleWhen

            //스케줄 클릭 이벤트
            itemView.setOnClickListener {
                itemClickListener.onClick(binding.scheduleItem, isClicked)
                isClicked = !isClicked; //클릭 flag
            }
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
            if (scheduleList[i].goingDelete) {
                //selectedItemPosition[j] = missionList[i].currentMissionScheduleResult.scheduleId
                //j++
                selectedItemPosition.add(scheduleList[i].currentMissionScheduleResult.scheduleId)
            }
        }
        return selectedItemPosition
    }
}