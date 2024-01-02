package com.example.myo_jib_sa.community

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.api.post.ArticleImage
import com.example.myo_jib_sa.community.api.post.CommentList
import com.example.myo_jib_sa.community.api.post.PostRetrofitManager
import com.example.myo_jib_sa.community.api.post.PostViewResponse
import com.example.myo_jib_sa.community.adapter.PostCommentAdapter
import com.example.myo_jib_sa.community.adapter.PostImgAdapter
import com.example.myo_jib_sa.community.dialog.CommunityPopupOk
import com.example.myo_jib_sa.databinding.ActivityPostBinding


class PostActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityPostBinding
    private lateinit var sharedPreferences: SharedPreferences //좋아요 상태 저장
    private lateinit var heartButton: AppCompatImageButton
    private var isHearted: Boolean = false
    private var postId:Long=0
    private var boardId:Long=0
    private var myPost:Boolean=false
    private lateinit var imageList: List<ArticleImage>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //원형 이미지
        binding.postWriterProfileImg.apply {
            background = ContextCompat.getDrawable(context, R.drawable.background_circle)
            clipToOutline = true

            // 원형 모양의 OutlineProvider 설정
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val radius = view.width / 2.0f
                    outline.setRoundRect(0, 0, view.width, view.height, radius)
                }
            }
        }

        //정보 저장
        boardId=intent.getIntExtra("boardId", 0).toLong()
        postId=intent.getLongExtra("postId", 0L)
        Log.d("게시물 ID", "게시물 id : ${postId}")

        //게시글 뷰 설정
        Constance.jwt?.let { setPostData(it, binding, boardId.toInt(), postId) }


        binding.postBackBtn.setOnClickListener {
            finish() //종료
        }

        //댓글 달기
        /*binding.postEnterBtn.setOnClickListener {
            Constance.jwt?.let { it1 ->
                commenting(
                    it1,
                    binding.postCommentInputEtxt.text.toString().replace("\n", "<br>"),postId){isSuccess->
                    if(isSuccess){
                        setPostData(Constance.jwt!!, binding, boardId.toInt(), postId)
                    }else{
                        showToast("댓글 달기 실패")
                    }
                    binding.postCommentInputEtxt.text.clear()
                }
            }
        }*/


        //좋아요 버튼 상태 불러오기
        /*heartButton = findViewById(R.id.post_heart_btn)
        setHeartButtonIcon()
        //좋아요 누르기 기능
        binding.postHeartBtn.setOnClickListener {
            Constance.jwt?.let { it1 ->
                setHeartApi(it1, postId){ isSuccess->
                    if(isSuccess){
                        isHearted = !isHearted
                        setHeartButtonIcon()
                    }
                }
            }
        }*/

        //작성자, 일반 유저 별로 보이는 메뉴 다르게
        //메뉴 클릭
        binding.postListBtn.setOnClickListener {
            showPopup(binding.postListBtn) }



    }

    override fun onResume() {
        super.onResume()
        Constance.jwt?.let { setPostData(it, binding, boardId.toInt(), postId) }
    }


    //    팝업 메뉴 보여주는 커스텀 메소드
    private fun showPopup(v: View) {
        if(myPost){
            val popup = PopupMenu(this, v) // PopupMenu 객체 선언
            popup.menuInflater.inflate(R.menu.post_writer_menu, popup.menu) // 메뉴 레이아웃 inflate
            popup.setOnMenuItemClickListener(this) // 메뉴 아이템 클릭 리스너 달아주기
            popup.show() // 팝업 보여주기
        }else{
            val popup = PopupMenu(this, v) // PopupMenu 객체 선언
            popup.menuInflater.inflate(R.menu.post_viewer_menu, popup.menu) // 메뉴 레이아웃 inflate
            popup.setOnMenuItemClickListener(this) // 메뉴 아이템 클릭 리스너 달아주기
            popup.show() // 팝업 보여주기
        }

    }

    // 팝업 메뉴 아이템 클릭 시 실행되는 메소드, 글 작성자와 일반 유저가 보이는 메뉴가 다름
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(myPost){
            when (item?.itemId) { // 메뉴 아이템에 따라 동작 다르게 하기
                R.id.postMenu_edit -> {
                    //수정 page 이동
                    val intent = Intent(this, WritePostingActivity::class.java)
                    intent.putExtra("title", binding.postPostNameTxt.text.toString())
                    intent.putExtra("postText", binding.postPostTextTxt.text.toString())
                    intent.putExtra("postId", postId)
                    intent.putExtra("boardId", boardId.toInt())
                    //사진 리스트 첨부
                    intent.putExtra("imgList1_id", imageList[0].imageId)
                    intent.putExtra("imgList1_path", imageList[0].filePath)
                    intent.putExtra("imgList2_id", imageList[1].imageId)
                    intent.putExtra("imgList2_path", imageList[1].filePath)
                    //수정인지 구별
                    intent.putExtra("isEdit", true)
                    startActivity(intent)
                }
                R.id.postMenu_delete -> {
                    val DelDialog = CommunityPopupOk(this,"해당 게시물을 삭제 하나요?")
                    DelDialog.setCustomDialogListener(object : CommunityPopupOk.CustomDialogListener {
                        override fun onPositiveButtonClicked(value: Boolean) {
                            if (value){
                                //신고 재확인 팝업창 띄우고 확인 누르면 api 연결
                                Constance.jwt?.let {
                                    postDelete(it, postId){ isSuccess->
                                        if(isSuccess){
                                            finish()
                                        }else{
                                            showToast("게시글 삭제 실패")
                                        }
                                    }
                                }

                            }
                        }
                    })
                    DelDialog.show()

                }
            }
            return item != null // 아이템이 null이 아닌 경우 true, null인 경우 false 리턴
        }else{
            when (item?.itemId) { // 메뉴 아이템에 따라 동작 다르게 하기
                R.id.postMenu_report -> {

                    val DelDialog = CommunityPopupOk(this,"해당 게시물을 신고 하나요?")
                    DelDialog.setCustomDialogListener(object : CommunityPopupOk.CustomDialogListener {
                        override fun onPositiveButtonClicked(value: Boolean) {
                            if (value){
                                //신고 재확인 팝업창 띄우고 확인 누르면 api 연결
                                Constance.jwt?.let {
                                    postReport(it,postId){ isSuccess->
                                        if(isSuccess){
                                            showToast("해당 게시글이 신고 되었습니다")
                                        }else{
                                            showToast("신고 실패했습니다")
                                        }
                                    }
                                }
                            }
                        }
                    })
                    DelDialog.show()

                }
            }
            return item != null // 아이템이 null이 아닌 경우 true, null인 경우 false 리턴
        }

    }


    //API 연결, 게시물 뷰 설정, 리사이클러뷰(이미지) 띄우기
    private fun setPostData(author:String, binding: ActivityPostBinding,
                            boardId:Int, postId:Long){
        val retrofitManager = PostRetrofitManager.getInstance(this)
        retrofitManager.postView(author, postId){response ->
            if(response.isSuccess=="true"){
                val imgList:List<ArticleImage> = response.result.articleImage

                //로그
                if(imgList.isNotEmpty()){
                    imageList=imgList
                    Log.d("게시글 API List 확인", imgList[0].filePath)
                    Log.d("게시글 API List 확인", imgList[0].imageId.toString())
                }else{
                    imageList= listOf(ArticleImage(0,""), ArticleImage(0,""))
                }
                Log.d("게시글 API List 확인", response.result.articleTitle)

                //콘텐츠 설정
                setPost(response, binding, boardId)

                val boardName=response.result.categoryName
                binding.postNameTxt.text="$boardName 게시판"

                //내 게시글인지 아닌지
                if(Constance.USER_ID==response.result.authorId){
                    myPost=true
                }

                //하트 상태
                isHearted=response.result.likeArticle
                setHeartButtonIcon()



            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                val returnCode = response.code
                val returnMsg = response.message
                showToast("게시물을 불러오지 못했습니다")
                Log.d("게시글 API isSuccess가 false", "${returnCode}  ${returnMsg}")
            }


        }
    }

    //게시물 뷰 설정
    private fun setPost(contents:PostViewResponse, binding: ActivityPostBinding, boardId: Int, ){
        binding.postWriterNameTxt.text=contents.result.authorName
        binding.postPostNameTxt.text=contents.result.articleTitle
        binding.postPostTextTxt.text=contents.result.articleContent.replace("<br>", "\n")
        binding.postWritinTimeTxt.text=contents.result.uploadTime

        //프로필 이미지 설정 필요
        if(contents.result.authorProfileImage.isNotEmpty()&&contents.result.authorProfileImage!=null){
            Glide.with(binding.root.context)
                .load(contents.result.authorProfileImage)
                .into(binding.postWriterProfileImg)
        }

        //이미지 리사이클러뷰 todo:다시 바꾸기
        if(contents.result.articleImage.isNotEmpty()){
            Log.d("게시글 이미지",contents.result.articleImage.toString() )

            //이미지가 있으면 아래 구분선 없애기
            binding.postLineLinear.visibility=View.INVISIBLE

            linkImgRecyclr(contents.result.articleImage)
        }


        //댓글 리사이클러뷰
        val isWriter:Boolean=(contents.result.authorId== Constance.USER_ID)
        linkCommentRecyclr(contents.result.commentList,isWriter, contents.result.articleId)

        //게시판 이름
        when(boardId.toLong()){
            Constance.ART_ID-> {
                binding.postNameTxt.text="예술 게시판"
            }
            Constance.FREE_ID-> {
                binding.postNameTxt.text="자유 게시판"
            }
            Constance.EXERCISE_ID-> {
                binding.postNameTxt.text="운동 게시판"
            }

        }
    }

    //댓글 달기
    private fun commenting(author: String, content:String, articleId:Long, callback: (Boolean) -> Unit){
        val retrofitManager = PostRetrofitManager.getInstance(this)
        retrofitManager.postComment(author,content ,articleId){response ->
            if(response){
                //로그
                Log.d("댓글 달기", "${response.toString()}")
                callback(true)


            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                Log.d("댓글 달기 API isSuccess가 false", "${response.toString()}")
                callback(false)
            }
        }
    }

    //게시물 신고
    private fun postReport(author:String, articleId: Long, callback: (Boolean) -> Unit){
        val retrofitManager = PostRetrofitManager.getInstance(this)
        retrofitManager.postReport(author,articleId){response ->
            if(response){
                //로그
                Log.d("게시물 신고", "${response.toString()}")
                callback(true)

            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                Log.d("게시물 신고 API isSuccess가 false", "${response.toString()}")
                showToast("게시물 신고 실패")
                callback(false)
            }
        }
    }

    //게시물 삭제
    private fun postDelete(author:String, articleId: Long, callback: (Boolean) -> Unit){

        val retrofitManager = PostRetrofitManager.getInstance(this)
        retrofitManager.postDelete(author,articleId){response ->
            if(response){
                //로그
                Log.d("게시물 삭제", "${response.toString()}")
                callback(true)

            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                Log.d("게시물 삭제 API isSuccess가 false", "${response.toString()}")
                callback(false)
            }
        }
    }

    //이미지 리사이클러뷰, 어댑터 연결
    private fun linkImgRecyclr(list:List<ArticleImage>){
        //이미지 뷰
      val adapter = PostImgAdapter(this,list)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.postImgRecyclr.layoutManager = layoutManager
        binding.postImgRecyclr.adapter = adapter

        adapter.setItemSpacing(binding.postImgRecyclr, 15)
    }

    //댓글 리사이클러뷰, 어댑터 연결
    private fun linkCommentRecyclr(list:List<CommentList>, isPostWriter:Boolean, postId: Long){
        //이미지 뷰
        val adapter = Constance.jwt?.let { PostCommentAdapter(this,list,isPostWriter, postId, it) }
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.postCommentRecyclr.layoutManager = layoutManager
        binding.postCommentRecyclr.adapter = adapter

        if (adapter != null) {
            adapter.setItemSpacing(binding.postCommentRecyclr, 15)
        }
    }

    //하트 버튼 아이콘 설정
    private fun setHeartButtonIcon() {
        val icon: Drawable? = if (isHearted) {
            ContextCompat.getDrawable(this, R.drawable.ic_heart_blue)
        } else {
            ContextCompat.getDrawable(this, R.drawable.ic_heart_blueline)
        }
        heartButton.setImageDrawable(icon)
    }

    //하트 api 연결
    private fun setHeartApi(author:String, postId:Long, callback: (Boolean) -> Unit){
        val retrofitManager = PostRetrofitManager.getInstance(this)
        if(!isHearted){
            //좋아요 api 연결
            retrofitManager.postLike(author, postId){response ->
                if(response){
                    //로그
                    Log.d("좋아요 ", "${response.toString()}")
                    callback(true)

                } else {
                    // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                    Log.d("좋아요 API isSuccess가 false", "${response.toString()}")
                    showToast("좋아요 누르기 실패")
                    callback(false)
                }
            }
        }else{
            //좋아요 삭제 api 연결
            retrofitManager.postUnlike(author, postId){response ->
                if(response){
                    //로그
                    Log.d("좋아요 삭제", "${response.toString()}")
                    callback(true)

                } else {
                    // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                    Log.d("좋아요 삭제 API isSuccess가 false", "${response.toString()}")
                    showToast("좋아요 삭제 실패")
                    callback(false)
                }
            }
        }
    }

    //토스트 메시지 띄우기
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}