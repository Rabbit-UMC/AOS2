package com.example.myo_jib_sa.community.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.api.communityHome.MainMission
import com.example.myo_jib_sa.databinding.ItemCommunityMissionBinding

class HomeMissionAdapter(
    private val context: Context,
    private val dataList:List<MainMission>)
    : RecyclerView.Adapter<HomeMissionAdapter.ViewHolder>(){

    //뷰홀더
    inner class ViewHolder(
        private val binding: ItemCommunityMissionBinding)
        :RecyclerView.ViewHolder(binding.root){
            fun bind(item:MainMission){

                //모양 설정
                binding.MissionItemConstraintLayout.apply {
                    background = ContextCompat.getDrawable(context, R.drawable.background_item_mission_layout)
                    clipToOutline = true

                    val cornerRadius = context.resources.displayMetrics.density * 8 // 8dp를 픽셀 단위로 변환

                    outlineProvider = object : ViewOutlineProvider() {
                        override fun getOutline(view: View, outline: Outline) {
                            outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
                        }
                    }
                }

                Log.d("리사이클러뷰","linkMrecyclr 어댑터 뷰홀더")
                binding.homeMissionItemNameTxt.text=item.mainMissionTitle
                //binding.homeMissionItemBoardNameTxt.text=item.categoryName
                binding.homeMissionItemDdayTxt.text=item.dday



                //클릭 이벤트
                binding.MissionItemConstraintLayout.setOnClickListener{
                    //클릭 이벤트 처리
                    //미션 터치시 해당 미션 이동
                    missionMove(item.mainMissionId, binding.MissionItemConstraintLayout)
               }
                //클릭이벤트
                binding.MissionItemConstraintLayout.setOnTouchListener { view, event ->
                    when (event.action) {
                        MotionEvent.ACTION_UP -> {
                            // 터치 다운 이벤트 처리
                            missionMove(item.mainMissionId, binding.MissionItemConstraintLayout)
                            true // 이벤트 소비됨
                        }
                        else -> false // 다른 이벤트 무시
                    }
                }

                //색 설정
                Log.d("linkMrecyclr 포지션","${adapterPosition}")
                setColor((adapterPosition+1).toLong(), binding)
        }
    }

    // 뷰홀더를 생성하여 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommunityMissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Log.d("리사이클러뷰","linkMrecyclr 어댑터 뷰홀더")
        return ViewHolder(binding)
    }

    //뷰홀더 데이터 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    //아이템 개수
    override fun getItemCount(): Int {
        Log.d("리사이클러뷰 사이즈", "${dataList.size}")
        return dataList.size
    }

    //간격 설정을 위한 클래스
    class CustomItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.left = spaceHeight // 아이템 사이의 간격을 설정 (왼쪽)
            outRect.right = spaceHeight // 아이템 사이의 간격을 설정 (왼쪽)
        }
    }
    // 아이템 간격 설정을 위한 메소드
    fun setItemSpacing(recyclerView: RecyclerView, spacing: Int) {
        recyclerView.addItemDecoration(CustomItemDecoration(spacing))
    }

    //게시판 이름에 따라 각 미션으로 이동 todo:게시판 카테고리 알아야함
    private fun missionMove(id:Long, layout: ConstraintLayout ){
        /*when(name){
            "예술 게시판"-> {
                val intent = Intent(layout.context, BoardExerciseActivity::class.java )
                intent.putExtra("boardId", Constance.ART_ID)
                layout.context.startActivity(intent)
            }
            "운동 게시판"-> {
                val intent = Intent(layout.context, BoardExerciseActivity::class.java )
                intent.putExtra("boardId", Constance.EXERCISE_ID)
                layout.context.startActivity(intent)
            }
            "자유 게시판"-> {
                val intent = Intent(layout.context, BoardExerciseActivity::class.java )
                intent.putExtra("boardId", Constance.FREE_ID)
                layout.context.startActivity(intent)
            }
            "예술"-> {
                val intent = Intent(layout.context, BoardExerciseActivity::class.java )
                intent.putExtra("boardId", Constance.ART_ID)
                layout.context.startActivity(intent)
            }
            "운동"-> {
                val intent = Intent(layout.context, BoardExerciseActivity::class.java )
                intent.putExtra("boardId", Constance.EXERCISE_ID)
                layout.context.startActivity(intent)
            }
            "자유"-> {
                val intent = Intent(layout.context, BoardExerciseActivity::class.java )
                intent.putExtra("boardId", Constance.FREE_ID)
                layout.context.startActivity(intent)
            }

        }*/
    }
   //todo : 기본 색 설정
    private fun setColor(boardId:Long, binding: ItemCommunityMissionBinding){
        when(boardId){
            Constance.ART_ID->{
                binding.MissionItemConstraintLayout.setBackgroundColor(Color.parseColor("#FFC436"))
                binding.homeMissionItemHostNameTxt.setTextColor(Color.parseColor("#FFC436"))
                binding.homeMissionItemCrownImg.setColorFilter(Color.parseColor("#FFFFFF"))
            }
            Constance.EXERCISE_ID->{
                binding.MissionItemConstraintLayout.setBackgroundColor(Color.parseColor("#234BD9"))
                binding.homeMissionItemHostNameTxt.setTextColor(Color.parseColor("#234BD9"))
            }
            Constance.FREE_ID->{
                binding.MissionItemConstraintLayout.setBackgroundColor(Color.parseColor("#C1C1C1"))
                binding.homeMissionItemHostNameTxt.setTextColor(Color.parseColor("#C1C1C1"))
            }

        }
    }
}