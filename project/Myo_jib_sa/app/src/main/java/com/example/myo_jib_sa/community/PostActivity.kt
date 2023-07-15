package com.example.myo_jib_sa.community

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.post.ArticleImage
import com.example.myo_jib_sa.community.Retrofit.post.CommentList
import com.example.myo_jib_sa.community.Retrofit.post.PostRetrofitManager
import com.example.myo_jib_sa.community.Retrofit.post.PostViewResponse
import com.example.myo_jib_sa.community.adapter.PostCommentAdapter
import com.example.myo_jib_sa.community.adapter.PostImgAdapter
import com.example.myo_jib_sa.databinding.ActivityPostBinding
import com.example.myo_jib_sa.mission.MissionFragment
import com.example.myo_jib_sa.mypage.MypageFragment
import com.example.myo_jib_sa.schedule.ScheduleFragment


class PostActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityPostBinding
    private lateinit var sharedPreferences: SharedPreferences //좋아요 상태 저장
    private lateinit var heartButton: AppCompatImageButton
    private var isHearted: Boolean = false
    private var postId:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val boardId:Int=intent.getIntExtra("boardId", 0)
        postId=intent.getLongExtra("postId", 0L)
        //setPostData(AUTHOR, binding, boardId, postId)


        binding.postBackBtn.setOnClickListener {
            finish() //종료
        }

        //댓글 달기
        binding.postEnterBtn.setOnClickListener {
            //api 연결 후 구현
        }


        //좋아요 버튼 상태 불러오기
        sharedPreferences=getSharedPreferences("${postId.toString()}", Context.MODE_PRIVATE)
        isHearted = sharedPreferences.getBoolean("isHearted", false)
        heartButton = findViewById(R.id.post_heart_btn)
        setHeartButtonIcon()
        //좋아요 누르기 기능
        binding.postHeartBtn.setOnClickListener {
            //api 연결 후 구현
            isHearted = !isHearted
            setHeartButtonIcon()
            saveState()
            //setHeartApi() -> 토스트메시지 띄우는 부분 추가 필요
        }

        //상단 목록 메뉴 상태 지정
        //작성자, 일반 유저 별로 보이는 메뉴 다르게

        //댓글 옆 버튼 유저별로 다르게 보이게 하기

        //묘집사 파란 닉네임


        //메뉴 클릭
        binding.postListBtn.setOnClickListener {
            showPopup(binding.postListBtn) }



    }

    //    팝업 메뉴 보여주는 커스텀 메소드
    private fun showPopup(v: View) {
        val popup = PopupMenu(this, v) // PopupMenu 객체 선언
        popup.menuInflater.inflate(R.menu.post_writer_menu, popup.menu) // 메뉴 레이아웃 inflate
        popup.setOnMenuItemClickListener(this) // 메뉴 아이템 클릭 리스너 달아주기
        popup.show() // 팝업 보여주기
    }

    // 팝업 메뉴 아이템 클릭 시 실행되는 메소드
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) { // 메뉴 아이템에 따라 동작 다르게 하기
            R.id.postMenu_edit -> {
                //수정 page 이동
                val intent = Intent(this, WritePostingActivity::class.java)
                intent.putExtra("title", binding.postPostNameTxt.text.toString())
                intent.putExtra("postText", binding.postPostTextTxt.text.toString())
                intent.putExtra("postId", postId)
                //사진 리스트 첨부 intent.putExtra("")
                startActivity(intent)
            }
            R.id.postMenu_delete -> {
            }
        }
        return item != null // 아이템이 null이 아닌 경우 true, null인 경우 false 리턴
    }


    //API 연결, 게시물 뷰 설정, 리사이클러뷰(이미지) 띄우기
    private fun setPostData(author:String, binding: ActivityPostBinding, boardId:Int, postId:Long){
        val retrofitManager = PostRetrofitManager.getInstance(this)
        retrofitManager.postView(author, postId){response ->
            if(response.isSuccess=="TRUE"){
                val imgList:List<ArticleImage> = response.articleImage

                //로그
                Log.d("List 확인", imgList[0].filePsth)
                Log.d("List 확인", imgList[0].imageId.toString())
                Log.d("List 확인", response.articleTitle)

                //콘텐츠 설정
                setPost(response, binding, boardId)

            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                val returnCode = response.code
                val returnMsg = response.message

                Log.d("홈 API isSuccess가 false", "${returnCode}  ${returnMsg}")
            }


        }
    }

    //게시물 뷰 설정
    private fun setPost(contents:PostViewResponse, binding: ActivityPostBinding, boardId: Int){
        binding.postWriterNameTxt.text=contents.authorName
        binding.postPostNameTxt.text=contents.articleTitle
        binding.postPostTextTxt.text=contents.articleContent
        binding.postWritinTimeTxt.text=contents.uploadTime

        //프로필 이미지 설정 필요
        Glide.with(binding.root.context)
            .load(contents.authorProfileImage)
            .into(binding.postWriterProfileImg)

        //이미지 리사이클러뷰
        linkImgRecyclr(contents.articleImage)
        //댓글 리사이클러뷰
        linkCommentRecyclr(contents.commentList)

        //게시판 이름
        when(boardId){
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
    private fun linkCommentRecyclr(list:List<CommentList>){
        //이미지 뷰
        val adapter = PostCommentAdapter(this,list)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.postCommentRecyclr.layoutManager = layoutManager
        binding.postCommentRecyclr.adapter = adapter

        adapter.setItemSpacing(binding.postCommentRecyclr, 15)
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


    //하트 버튼 상태 저장
    private fun saveState() {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("isHearted", isHearted)
        editor.apply()
    }
    //하트 api 연결
    private fun setHeartApi(author:String, postId:Long){
        val retrofitManager = PostRetrofitManager.getInstance(this)
        if(isHearted){
            //좋아요 api 연결
            retrofitManager.postLike(author, postId){response ->
                if(response){
                    //로그
                    Log.d("좋아요 ", "${response.toString()}")


                } else {
                    // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                    Log.d("좋아요 API isSuccess가 false", "${response.toString()}")
                    //토스트 메시지 띄우기
                }
            }
        }else{
            //좋아요 삭제 api 연결
            retrofitManager.postUnlike(author, postId){response ->
                if(response){
                    //로그
                    Log.d("좋아요 삭제", "${response.toString()}")


                } else {
                    // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                    Log.d("좋아요 삭제 API isSuccess가 false", "${response.toString()}")
                    //토스트 메시지 띄우기
                }
            }
        }
    }



}