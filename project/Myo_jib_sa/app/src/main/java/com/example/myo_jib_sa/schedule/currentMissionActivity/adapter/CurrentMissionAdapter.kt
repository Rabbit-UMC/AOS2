package com.example.myo_jib_sa.schedule.currentMissionActivity.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ItemCurrentMissionBinding
import com.example.myo_jib_sa.schedule.api.currentMission.CurrentMissionResult



class CurrentMissionAdapter (private val missionList:ArrayList<CurrentMissionResult>):
    RecyclerView.Adapter<CurrentMissionAdapter.ViewHolder>() {

    //클릭 이벤트 처리 ==============================================
//리스너 인터페이스
    interface OnItemClickListener{
        fun onClick(currentMissionData: CurrentMissionResult)
        fun onLongClick(position: Int)
        fun onScheduleClick(currentMissionData: CurrentMissionResult)
    }
     //(3) 외부에서 클릭 시 이벤트 설정
     fun setItemClickListener(onItemClickListener: OnItemClickListener) {
         this.itemClickListener = onItemClickListener
     }
    // (4) setItemClickListener로 설정한 함수 실행
    lateinit private var itemClickListener : OnItemClickListener
    //==============================================================

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

        //작성한 일지 클릭 이벤트
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

            binding.missionScheduleTv.setOnClickListener {
                itemClickListener.onScheduleClick(data)
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


