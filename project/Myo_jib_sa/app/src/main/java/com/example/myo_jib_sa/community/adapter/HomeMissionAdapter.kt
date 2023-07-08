package com.example.myo_jib_sa.community.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivities
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.BoardArtActivity
import com.example.myo_jib_sa.community.BoardExerciseActivity
import com.example.myo_jib_sa.community.BoardFreeActivity
import com.example.myo_jib_sa.community.Retrofit.communityHome.MainMission
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
                Log.d("리사이클러뷰","linkMrecyclr 어댑터 뷰홀더")
                binding.homeMissionItemNameTxt.text=item.mainMissionName
                binding.homeMissionItemBoardNameTxt.text=item.catagoryName
                binding.homeMissionItemDdayTxt.text=item.endTime.toString()

                //이미지 설정
               /* Glide.with(context)
                    .load(item.missionImage)
                    .into(binding.homeMissionItemImgImg)*/

                //클릭 이벤트
                binding.MissionItemConstraintLayout.setOnClickListener{
                    //클릭 이벤트 처리
                    //미션 터치시 해당 게시판 이동
                    boardMove(item.catagoryName, binding.homeMissionItemImgImg)
               }
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

    //게시판 이름에 따라 각 게시판으로 이동
    private fun boardMove(name:String, imageView: ImageView ){
        when(name){
            "예술 게시판"-> {
                val intent = Intent(imageView.context, BoardArtActivity::class.java )
                imageView.context.startActivity(intent)
            }
            "운동 게시판"-> {
                val intent = Intent(imageView.context, BoardExerciseActivity::class.java )
                imageView.context.startActivity(intent)
            }
            "자유 게시판"-> {
                val intent = Intent(imageView.context, BoardFreeActivity::class.java )
                imageView.context.startActivity(intent)
            }

        }
    }
}