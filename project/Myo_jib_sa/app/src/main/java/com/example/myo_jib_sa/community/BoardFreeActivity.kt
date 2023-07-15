package com.example.myo_jib_sa.community

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Retrofit.BoardPost.Articles
import com.example.myo_jib_sa.community.Retrofit.BoardPost.PostBoardRetrofitManager
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.adapter.BoardAdapter
import com.example.myo_jib_sa.databinding.ActivityBoardFreeBinding

class BoardFreeActivity : AppCompatActivity() {

    private lateinit var binding:ActivityBoardFreeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityBoardFreeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_free)

        //뒤로 가기 버튼
        binding.boardFreeBackBtn.setOnClickListener {
            finish()
        }

        //글쓰기
        binding.boardPostingBtn.setOnClickListener {
            val intent= Intent(this, WritePostingActivity::class.java)
            val postId=Constance.FREE_ID
            intent.putExtra("postId", postId)
            startActivity(intent)
        }

    }


    //API 연결, 리사이클러뷰 띄우기
    private fun getBoardData(author:String,page:Int ,id:Long){
        val retrofitManager = PostBoardRetrofitManager.getInstance(this)
        retrofitManager.board(author,page ,id){response ->
            if(response.isSuccess=="TRUE"){
                val boardList:List<Articles> = response.articles
                if(boardList.isNotEmpty()){

                    //로그
                    Log.d("boardList 확인", boardList[0].articleTitle)
                    Log.d("boardList 확인", boardList[0].LikeCount.toString())
                    Log.d("boardList 확인", boardList[0].commentCount.toString())
                    Log.d("boardList 확인", boardList[0].uploadTime.toString())


                    //리사이클러뷰 연결
                    linkBrecyclr(boardList)


                }else{
                    Log.d("리사이클러뷰 어댑터로 리스트 전달", "List가 비었다네요")
                }
            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                val returnCode = response.code
                val returnMsg = response.message

                Log.d("게시판 API isSuccess가 false", "${returnCode}  ${returnMsg}")
            }


        }
    }

    //미션 리사이클러뷰, 어댑터 연결
    private fun linkBrecyclr(boardList:List<Articles>){
        //미션
        val Badapter = BoardAdapter(this,boardList, Constance.FREE_ID)
        val BlayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.boardFreePostRecyclr.layoutManager = BlayoutManager
        binding.boardFreePostRecyclr.adapter = Badapter

        Badapter.setItemSpacing(binding.boardFreePostRecyclr, 15)
    }
}
