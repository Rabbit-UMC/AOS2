package com.example.myo_jib_sa.community.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.community.PostActivity
import com.example.myo_jib_sa.community.Retrofit.communityHome.MainMission
import com.example.myo_jib_sa.community.Retrofit.communityHome.PopularArticle
import com.example.myo_jib_sa.databinding.ItemCommunityMissionBinding
import com.example.myo_jib_sa.databinding.ItemPostBinding

class HomePostAdapter (
    private val context: Context,
    private val dataList:List<PopularArticle>)
    : RecyclerView.Adapter<HomePostAdapter.ViewHolder>(){

        //뷰홀더
        inner class ViewHolder(
            private val binding: ItemPostBinding
        ): RecyclerView.ViewHolder(binding.root){

            fun bind(item: PopularArticle){
                binding.postItemNameTxt.text=item.articleTitle
                binding.postItmeCommentCntTxt.text=item.commentCount.toString()
                binding.postItemUploadTimeTxt.text=item.uploadTime
                binding.postItmeHeartNumTxt.text=item.likeCount.toString()

                //클릭 이벤트
                binding.root.setOnClickListener{
                    //클릭 이벤트 처리
                    //클릭시 해당 게시글 이동
                    val postId=item.articleId
                    //todo:val boardId=item.boardId
                    val intent= Intent(binding.root.context, PostActivity::class.java)
                    intent.putExtra("postId", postId)
                    //todo:intent.putExtra("boardId", boardId)
                    binding.root.context.startActivity(intent)
                }
            }
        }

        // 뷰홀더를 생성하여 반환
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
