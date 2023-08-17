package com.example.myo_jib_sa.mission

import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.databinding.ItemMissionMissionBinding
import com.example.myo_jib_sa.mission.API.Home
import com.example.myo_jib_sa.mission.API.MissionHomeResponse

class MissionAdapter(
    private val context: Context,
    private val dataList: List<Home>,
    private val onItemClickListener: OnItemClickListener,
    private val onItemLongClickListener: OnItemLongClickListener
) : RecyclerView.Adapter<MissionAdapter.ViewHolder>() {


    interface OnItemLongClickListener {
        fun onItemLongClick(item:Home)
    }

    interface OnItemClickListener {
        fun onItemClick(item:Home)
    }

    inner class ViewHolder(
        private val binding: ItemMissionMissionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Home) {

            binding.missionTitleTxt.text = item.title

            //메모 15자 이내로 보이도록 설정
            val maxLength = 15
            val content = item.content // 원본 문자열

            val truncatedContent = if (content.length > maxLength) {
                content.substring(0, maxLength) + " ···" // 최대 길이까지 잘라서 '···' 추가
            } else {
                content // 원본 문자열 그대로 사용
            }

            //미션 홈에 날짜 mm-dd로 표시되도록
            val startAt = item.startAt
            val formattedStartAt = startAt.substring(5, 10)
            val endAt = item.endAt
            val formattedEndAt = endAt.substring(5, 10)

            binding.missionIntroTxt.text = truncatedContent
            Glide.with(binding.root.context)
                .load(item.image)
                .into(binding.missionImg)
            binding.missionStartTimeTxt.text = formattedStartAt
            binding.missionEndTimeTxt.text = formattedEndAt
            binding.missionChallengerTxt.text="${item.challengerCnt} 명"

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

}
