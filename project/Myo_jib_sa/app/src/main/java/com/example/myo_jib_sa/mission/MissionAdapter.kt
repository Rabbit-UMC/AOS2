package com.example.myo_jib_sa.mission

import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.view.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.databinding.ItemMissionMissionBinding
class MissionAdapter(
    private val context: Context,
    private val dataList: List<RecyclerMissionData>,
    private val onItemClickListener: OnItemClickListener,
    private val onItemLongClickListener: OnItemLongClickListener
) : RecyclerView.Adapter<MissionAdapter.ViewHolder>() {


    interface OnItemLongClickListener {
        fun onItemLongClick(item: RecyclerMissionData)
    }

    interface OnItemClickListener {
        fun onItemClick(item: RecyclerMissionData)
    }

    inner class ViewHolder(
        private val binding: ItemMissionMissionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RecyclerMissionData) {
            binding.missionTitleTxt.text = item.title
            binding.missionIntroTxt.text = item.intro
            binding.missionImg.setImageResource(item.imageSrc)
            binding.missionStartTimeTxt.text = item.start
            binding.missionEndTimeTxt.text = item.end

            // 롱클릭 이벤트 처리
            binding.root.setOnLongClickListener {
                onItemLongClickListener.onItemLongClick(item)
                true // 이벤트가 소비되었음을 반환
            }

            // 간단한 클릭 이벤트 처리
            binding.root.setOnClickListener {
                onItemClickListener.onItemClick(item)
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


}
