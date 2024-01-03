package com.example.myo_jib_sa.mission

import android.util.Log
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.databinding.ItemMissionMissionBinding
import com.example.myo_jib_sa.mission.api.Mission

class MissionRVAdapter(
    private var dataList: List<Mission>,
    private val onItemClickListener: OnItemClickListener,
    private val onReportClickListener: OnReportClickListener
) : RecyclerView.Adapter<MissionRVAdapter.MissionViewHolder>() {

    interface OnReportClickListener {
        fun onReportClick(item:Mission)
    }
    interface OnItemClickListener {
        fun onItemClick(item:Mission)
    }

    inner class MissionViewHolder(private val binding: ItemMissionMissionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Mission) {
            with(binding) {
                Glide.with(missionImgIv)
                    .load(item.image)
                    .into(missionImgIv)
                missionTitleTxt.text = item.title
                missionCntTxt.text = item.challengerCnt.toString()
                missionDateTxt.text = "D+60"

                Log.d("home",item.missionId.toString())

                // 신고 클릭 이벤트 처리
                missionReportTxt.setOnClickListener {
                    //클릭된 아이템의 위치 가져오기
                    val itemPosition = dataList[adapterPosition]
                    onReportClickListener.onReportClick(itemPosition)
                    Log.d("home","report ID: {$itemPosition.id.toString()}")
                    true
                }

                // 클릭 이벤트 처리(미션 상세보기)
                root.setOnClickListener {
                    val itemPosition = dataList[adapterPosition]
                    onItemClickListener.onItemClick(itemPosition)
                    Log.d("home","detail ID: {$itemPosition.id.toString()}")
                }
            }
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

    fun updateDataByCategory(wholeList:List<Mission>, newCategoryId: Int) {
        dataList = if (newCategoryId != 0){
            wholeList.filter { it.categoryId == newCategoryId.toLong() }
        } else {
            wholeList
        }
        notifyDataSetChanged()
    }
}