package com.example.myo_jib_sa.community.adapter

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.communityHome.MainMission
import com.example.myo_jib_sa.community.Retrofit.post.CommentList
import com.example.myo_jib_sa.community.Retrofit.post.PostRetrofitManager
import com.example.myo_jib_sa.community.dialog.CommunityPopupOk
import com.example.myo_jib_sa.databinding.ItemCommentBinding
import com.example.myo_jib_sa.databinding.ItemCommunityMissionBinding

class PostCommentAdapter(
    private val context: Context,
    private val dataList:List<CommentList>,
    private val isPostWriter:Boolean,
    private val postId:Long)
    : RecyclerView.Adapter<PostCommentAdapter.ViewHolder>(){


    //뷰홀더
    inner class ViewHolder(
        private val binding: ItemCommentBinding
    )
        : RecyclerView.ViewHolder(binding.root){
        fun bind(item: CommentList){
            //댓글 작성자 이름, 내용 세팅
            binding.commentWriterNameTxt.text=item.commentAuthorName
            binding.commentPostTextTxt.text=item.commentContent


            // todo: 프로필 이미지 설정
            /*Glide.with(context)
                 .load(item.commentAuthorProfileImage)
                 .into(binding.commentProfileImg)*/

            //commentBtn 아이콘 상태 설정
            if(item.commentUserId==Constance.USER_ID){
                //댓글 작성자 일떄
                binding.commentBtn.setImageResource(R.drawable.ic_delete)
                binding.commentBtn.isEnabled = true // Enable the button

            }else if(isPostWriter){
                //게시글 작성자 일때
                binding.commentBtn.setImageResource(R.drawable.ic_change)
                binding.commentBtn.isEnabled = true // Enable the button
            }else{
                //일반 사용자 일때
                binding.commentBtn.setImageDrawable(null)
                binding.commentBtn.isEnabled = false
            }

            binding.commentBtn.setOnClickListener {
                Log.d("댓글 id", "${item.commentId}")
                if(item.commentUserId==Constance.USER_ID){
                    //댓글 작성자 일떄
                        val DelDialog = CommunityPopupOk(context,"해당 댓글을 삭제 하나요?")

                        DelDialog.setCustomDialogListener(object : CommunityPopupOk.CustomDialogListener {
                            override fun onPositiveButtonClicked(value: Boolean) {
                                if (value){
                                    //댓글 삭제하기
                                    commentDelete(Constance.jwt, postId, item.commentId)

                                }
                            }
                        })
                    DelDialog.show()
                }else if(isPostWriter){
                    //게시글 작성자 일때
                        val DelDialog = CommunityPopupOk(context,"댓글 변경 후 되돌릴 수 없습니다")
                        DelDialog.setCustomDialogListener(object : CommunityPopupOk.CustomDialogListener {
                            override fun onPositiveButtonClicked(value: Boolean) {
                                if (value){
                                    //댓글 변경하기
                                    commentChange()
                                    // TODO: 댓글 바꾸기 api 연결 함수 넣기, 댓글 바꾸기 api 추가해

                                }
                            }
                        })
                    DelDialog.show()
                }else{
                    //일반 사용자 일때
                    binding.commentBtn.setImageDrawable(null)
                    binding.commentBtn.isEnabled = false
                }
            }

            // TODO:바뀐 글인지 아닌지 상태 체크 후 내용 색상 바꾸기

            // 묘집사는 파란 닉네임
            if(item.userPermission=="HOST"){
                val blueColor = ContextCompat.getColor(context, R.color.my_blue)
                binding.commentWriterNameTxt.setTextColor(blueColor)
            }

            }
        }


    // 뷰홀더를 생성하여 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
            outRect.bottom = spaceHeight // 아이템 사이의 간격을 설정 (아래
        }
    }
    // 아이템 간격 설정을 위한 메소드
    fun setItemSpacing(recyclerView: RecyclerView, spacing: Int) {
        recyclerView.addItemDecoration(CustomItemDecoration(spacing))
    }

    //댓글 삭제
    private fun commentDelete(author:String, articleId:Long, commentId:Long){
        val retrofitManager=PostRetrofitManager.getInstance(context)
        retrofitManager.postCommentDelete(author, articleId,commentId){response->
            if(response){
                //로그
                Log.d("댓글 삭제", "${response.toString()}")


            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                Log.d("댓글 삭제 API isSuccess가 false", "${response.toString()}")
                showToast("댓글 삭제 실패")
            }
        }
    }

    //댓글 바꾸기
    private fun commentChange(){
        // TODO: 댓글 바꾸기 api 연결 함수 넣기
    }

    //토스트 메시지 띄우기
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}