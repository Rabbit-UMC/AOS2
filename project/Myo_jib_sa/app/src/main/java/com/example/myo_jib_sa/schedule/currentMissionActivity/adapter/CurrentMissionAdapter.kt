package com.example.myo_jib_sa.schedule.currentMissionActivity.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ItemCurrentMissionBinding
import com.example.myo_jib_sa.schedule.api.currentMission.CurrentMissionResult



class CurrentMissionAdapter (
    private val missionList:ArrayList<CurrentMissionResult>,
    private val onItemClickListener: OnItemClickListener,):
    RecyclerView.Adapter<CurrentMissionAdapter.ViewHolder>() {


    interface OnItemClickListener{
        fun onMissionClick(missionId: Long)
        fun onScheduleClick(currentMissionData: CurrentMissionResult)
    }

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
    }

    inner class ViewHolder(private val binding: ItemCurrentMissionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: CurrentMissionResult) {
            Log.d("debug", "categoryId"+data.categoryId)
            //1번이 자유, 2번이 운동, 3번이 예술
            when(data.categoryId){
                1L->binding.constraintlayout1.setBackgroundResource(R.drawable.view_round_r8_left_gray4)
                2L->binding.constraintlayout1.setBackgroundResource(R.drawable.view_round_r8_left_main2)
                3L->binding.constraintlayout1.setBackgroundResource(R.drawable.view_round_r8_left_main4)
                else -> binding.constraintlayout1.setBackgroundResource(R.drawable.view_round_r8_left_gray4)
            }


            //todo
            //binding.missionImg.clipToOutline = true //이미지 모서리 둥글게
            binding.missionTitleTv.text = data.missionTitle
            binding.missionDdayTv.text = data.dday
            binding.missionChallengerTv.text = "${data.challengerCnt}"

            //mission클릭 이벤트
            binding.missionTitleTv.setOnClickListener {
                onItemClickListener.onMissionClick(data.missionId)
            }

            //작성한 일지 클릭 이벤트
            binding.missionScheduleTv.setOnClickListener {
                onItemClickListener.onScheduleClick(data)
            }


                //todo
//            Glide.with(itemView)
//                .load(data.image)
//                .error(R.drawable.ic_currentmission_free) //에러시 보여줄 이미지
//                .fallback(R.drawable.ic_currentmission_free) //load할 url이 비어있을 경우 보여줄 이미지
//                .into(binding.missionImg)//이미지 설정
        }
    }

    override fun getItemCount(): Int {
        return missionList.size
    }
}


