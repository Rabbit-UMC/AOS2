package com.example.myo_jib_sa.mission.adapter

import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ItemMissionMissionBinding
import com.example.myo_jib_sa.mission.api.Mission

class MissionRVAdapter(
    private var dataList: List<Mission>,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<MissionRVAdapter.MissionViewHolder>() {
    interface OnClickListener {
        fun onReportClick(item:Mission)
        fun onItemClick(item:Mission)
    }

    inner class MissionViewHolder(val binding: ItemMissionMissionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Mission) {
            with(binding) {
                setTitleImage(missionImgIv, item.categoryId)
                missionTitleTxt.text = item.title
                missionCntTxt.text = item.challengerCnt.toString()
                missionDateTxt.text = item.dday

                // 신고 클릭 이벤트 처리
                missionReportTxt.setOnClickListener {
                    //클릭된 아이템의 위치 가져오기
                    val itemPosition = dataList[adapterPosition]
                    onClickListener.onReportClick(itemPosition)
                    Log.d("home","report ID: {$itemPosition.id.toString()}")
                    true
                }

                // 클릭 이벤트 처리(미션 상세보기)
                missionItemCl.setOnClickListener {
                    val itemPosition = dataList[adapterPosition]
                    onClickListener.onItemClick(itemPosition)
                    Log.d("home","detail ID: {$itemPosition.id.toString()}")
                }
            }
        }

        fun getReportTextView(): TextView {
            return binding.missionReportTxt
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionViewHolder {
        val binding = ItemMissionMissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MissionViewHolder(binding)
    }

    //뷰홀더 데이터 설정
    override fun onBindViewHolder(holder: MissionViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    //아이템 개수
    override fun getItemCount(): Int {
        return dataList.size
    }


    fun updateData(newMissions: List<Mission>) {
        dataList = newMissions
        notifyDataSetChanged() // 데이터가 변경되었음을 어댑터에 알려줍니다.
    }

    fun setTitleImage(imageView: ImageView, category: Long) {
        imageView.setImageResource(
            when(category){
                FREE -> R.drawable.ic_mission_list_free
                EXERCISE -> R.drawable.ic_mission_list_exercise
                ART -> R.drawable.ic_mission_list_art
                else -> R.drawable.ic_curmission_myo_icon
            }
        )

    }
    companion object {
        const val FREE = 1L
        const val EXERCISE = 2L
        const val ART = 3L
    }
}