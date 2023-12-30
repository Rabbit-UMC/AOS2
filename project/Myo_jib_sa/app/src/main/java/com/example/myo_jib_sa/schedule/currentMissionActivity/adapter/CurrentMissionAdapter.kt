package com.example.myo_jib_sa.Schedule.currentMissionActivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemCurrentMissionBinding
import com.example.myo_jib_sa.Schedule.api.currentMission.CurrentMissionResult



class CurrentMissionAdapter (private val missionList:ArrayList<CurrentMissionResult>,
                             private val width:Int, private val height:Int):
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
        //binding.root.layoutParams = ConstraintLayout.LayoutParams((width*0.46).toInt(), (width*0.46*1.2).toInt()) todo
        //binding.frameLayout.layoutParams = ConstraintLayout.LayoutParams((width*0.40).toInt(), (height*0.16).toInt())
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


