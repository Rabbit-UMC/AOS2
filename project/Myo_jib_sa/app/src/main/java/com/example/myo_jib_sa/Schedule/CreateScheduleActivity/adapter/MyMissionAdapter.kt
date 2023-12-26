package com.example.myo_jib_sa.Schedule.CreateScheduleActivity.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ItemScheduleMissionBinding
import com.example.myo_jib_sa.Schedule.CreateScheduleActivity.api.getMissionList.GetMyMissionResult


class MyMissionAdapter(private val dayList:ArrayList<GetMyMissionResult>):
    RecyclerView.Adapter<MyMissionAdapter.ViewHolder>() {


    //화면 설정
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemScheduleMissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    //데이터 설정
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dayList.get(position))
    }

    inner class ViewHolder(private val binding: ItemScheduleMissionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: GetMyMissionResult) {
            binding.missionTitleTv.text = data.title
            binding.missionDdayTv.text = data.dday
            when(data.categoryId){
                1L -> binding.rootLayout.setBackgroundResource(R.drawable.view_round_r16_gray4)//일반
                2L -> binding.rootLayout.setBackgroundResource(R.drawable.view_round_r16_main2)//운동
                3L -> binding.rootLayout.setBackgroundResource(R.drawable.view_round_r16_main4)//예술
            }

            binding.root.setOnClickListener {
                itemClickListener.onClick(data)
            }

        }
    }



    //클릭 이벤트 처리 ==============================================
    //리스너 인터페이스
    interface  OnItemClickListener{
        fun onClick(data: GetMyMissionResult)

    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
    //==============================================================



    override fun getItemCount(): Int {
        return dayList?.size ?: 0
    }
}