package com.example.myo_jib_sa.mission

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemMissionMissionBinding

sealed class MissionItem {
    abstract val missionId: Long
    abstract val title: String
    abstract val challengerCnt: Int
    abstract val startAt: String
    abstract val endAt: String
    abstract val content: String
    abstract val categoryId: Long
    abstract val image: String
}

data class Home(
    override val missionId: Long,
    override val title: String,
    override val challengerCnt: Int,
    override val startAt: String,
    override val endAt: String,
    override val content: String,
    override val categoryId: Long,
    override val image: String
) : MissionItem()

data class Category(
    override val missionId: Long,
    override val title: String,
    override val challengerCnt: Int,
    override val startAt: String,
    override val endAt: String,
    override val content: String,
    override val categoryId: Long,
    override val image: String
) : MissionItem()


class MissionRVAdapter(
    private val context: Context,
    private var dataList: List<MissionItem>,
    private val onItemClickListener: OnItemClickListener,
    private val onItemLongClickListener: OnItemLongClickListener
) : RecyclerView.Adapter<MissionRVAdapter.ViewHolder>() {

    interface OnItemLongClickListener {
        fun onItemLongClick(item:MissionItem)
    }

    interface OnItemClickListener {
        fun onItemClick(item:MissionItem)
    }

    inner class ViewHolder(private val binding: ItemMissionMissionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MissionItem) {

            binding.missionTitleTxt.text = item.title
            binding.missionCntTxt.text = item.challengerCnt.toString()
            binding.missionDateTxt.text = "D+60"

            Log.d("home",item.missionId.toString())

            // 롱클릭 이벤트 처리(신고)
            binding.root.setOnLongClickListener {
                //클릭된 아이템의 위치 가져오기
                val itemPosition = dataList[adapterPosition]
                onItemLongClickListener.onItemLongClick(itemPosition)
                Log.d("home","report ID: {$itemPosition.id.toString()}")
                true // 이벤트가 소비되었음을 반환
            }

            // 간단한 클릭 이벤트 처리(미션 상세보기)
            binding.root.setOnClickListener {
                val itemPosition = dataList[adapterPosition]
                onItemClickListener.onItemClick(itemPosition)
                Log.d("home","detail ID: {$itemPosition.id.toString()}")
            }
        }
    }

    // 뷰홀더를 생성하여 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMissionMissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    //뷰홀더 데이터 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    //아이템 개수
    override fun getItemCount(): Int {
        return dataList.size
    }

    //간격 설정을 위한 클래스
    class CustomItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.top = spaceHeight // 아이템 사이의 간격을 설정 (위 넓이)
            outRect.bottom=spaceHeight //아래
        }
    }
    // 아이템 간격 설정을 위한 메소드
    fun setItemSpacing(recyclerView: RecyclerView, spacing: Int) {
        recyclerView.addItemDecoration(CustomItemDecoration(spacing))
    }

    fun updateData(/*newItems: List<MissionItem>*/) {
        //dataList = newItems
        notifyDataSetChanged()
    }
}