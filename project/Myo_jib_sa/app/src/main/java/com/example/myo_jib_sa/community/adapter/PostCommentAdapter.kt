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
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Constance
import com.example.myo_jib_sa.community.api.post.ArticleImage
import com.example.myo_jib_sa.community.api.post.CommentList
import com.example.myo_jib_sa.community.api.post.PostRetrofitManager
import com.example.myo_jib_sa.community.dialog.CommunityPopupOk
import com.example.myo_jib_sa.databinding.ItemCommentBinding

class PostCommentAdapter(
    private val context: Context,
    private var dataList:List<CommentList>,
    private val isPostWriter:Boolean,
    private val postId:Long
    ,private val jwt:String)
    : RecyclerView.Adapter<PostCommentAdapter.ViewHolder>(){


    //뷰홀더
    inner class ViewHolder(
        private val binding: ItemCommentBinding
    )
        : RecyclerView.ViewHolder(binding.root){
        fun bind(item: CommentList){
            //댓글 작성자 이름, 내용 세팅
            binding.commentWriterNameTxt.text=item.commentAuthorName
            binding.commentPostTextTxt.text=item.commentContent.replace("<br>", "\n")

            //commentBtn 아이콘 상태 설정
            if(item.commentUserId== Constance.USER_ID){
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
                if(item.commentUserId== Constance.USER_ID){
                    //댓글 작성자 일떄
                        val DelDialog = CommunityPopupOk(context,"해당 댓글을 삭제 하나요?")

                        DelDialog.setCustomDialogListener(object : CommunityPopupOk.CustomDialogListener {
                            override fun onPositiveButtonClicked(value: Boolean) {
                                if (value){
                                    //댓글 삭제하기
                                    Log.d("댓글 id", item.commentId.toString())
                                    commentDelete(jwt, item.commentId){isSuccess->
                                        if(isSuccess){
                                            setCommentData(jwt,postId)
                                            notifyDataSetChanged()
                                        }else{
                                            showToast("댓글 삭제에 실패 했습니다.")
                                        }
                                    }

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
                                    commentChange(jwt, item.commentId){isSuccess->
                                        if(isSuccess){
                                            setCommentData(jwt,postId)
                                        }else{
                                            showToast("댓글 변경에 실패 했습니다.")
                                        }
                                    }
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
    private fun commentDelete(author:String,  commentId:Long, callback:(Boolean)->Unit){
        val retrofitManager=PostRetrofitManager.getInstance(context)
        retrofitManager.postCommentDelete(author, commentId){response->
            if(response){
                //로그
                Log.d("댓글 삭제", "${response.toString()}")
                callback(true)

            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                Log.d("댓글 삭제 API isSuccess가 false", "${response.toString()}")
                callback(false)
            }
        }
    }

    //댓글 바꾸기
    private fun commentChange(author:String, commentId: Long,callback: (Boolean) -> Unit){
        val retrofitManager = PostRetrofitManager.getInstance(context)
        retrofitManager.postCommentLock(author, commentId){response ->
            if(response){
                Log.d("댓글 변경", "${response.toString()}")
                callback(true)

            } else {
                Log.d("댓글 변경 API isSuccess가 false", "${response.toString()}")
                callback(false)
            }


        }
    }

    //토스트 메시지 띄우기
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    //댓글 부분 데이터 리스트 받기
    private fun setCommentData(author:String, postId:Long){
        dataList= emptyList()

        val retrofitManager = PostRetrofitManager.getInstance(context)
        retrofitManager.postView(author, postId){response ->
            if(response.isSuccess=="true"){
                val imgList:List<ArticleImage> = response.result.articleImage

                //로그
                if(imgList.isNotEmpty()){
                    Log.d("게시글 API List 확인", imgList[0].filePath)
                    Log.d("게시글 API List 확인", imgList[0].imageId.toString())
                }
                Log.d("게시글 API List 확인", response.result.articleTitle)


                dataList=response.result.commentList
                notifyDataSetChanged()

            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                val returnCode = response.code
                val returnMsg = response.message
                showToast("댓글 부분을 불러오지 못했습니다.")
                Log.d("게시글 API isSuccess가 false", "${returnCode}  ${returnMsg}")
            }


        }
    }

    // 데이터 리스트를 업데이트하는 메서드
    fun updateData(newDataList: List<CommentList>) {
        dataList = newDataList
        notifyDataSetChanged()
    }
}