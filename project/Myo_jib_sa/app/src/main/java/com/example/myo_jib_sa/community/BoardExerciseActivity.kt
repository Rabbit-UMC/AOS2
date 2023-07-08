package com.example.myo_jib_sa.community

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Retrofit.Post.Articles
import com.example.myo_jib_sa.community.Retrofit.Post.PostRetrofitManager
import com.example.myo_jib_sa.community.Retrofit.communityHome.PopularTopic
import com.example.myo_jib_sa.community.adapter.BoardAdapter
import com.example.myo_jib_sa.databinding.ActivityBoardExerciseBinding
import java.sql.Timestamp

class BoardExerciseActivity : AppCompatActivity() {

    private lateinit var binding:ActivityBoardExerciseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBoardExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //뒤로가기 버튼
        binding.boardExcsBackBtn.setOnClickListener {
            finish()
        }

        //리사이클러뷰 테스트
        val BList= listOf<Articles>(
            Articles("개시물 제목", Timestamp.valueOf("2023-07-05 12:12:00"), 1,1),
            Articles("개시물 제목", Timestamp.valueOf("2023-07-05 12:12:00"), 2,1),
            Articles("개시물 제목", Timestamp.valueOf("2023-07-05 12:12:00"), 3,1),
            Articles("개시물 제목", Timestamp.valueOf("2023-07-05 12:12:00"), 4,1),
            Articles("개시물 제목", Timestamp.valueOf("2023-07-05 12:12:00"), 5,1),
            Articles("개시물 제목", Timestamp.valueOf("2023-07-05 12:12:00"), 6,1),
            Articles("개시물 제목", Timestamp.valueOf("2023-07-05 12:12:00"), 7,1),
            Articles("개시물 제목", Timestamp.valueOf("2023-07-05 12:12:00"), 8,1),
            Articles("개시물 제목", Timestamp.valueOf("2023-07-05 12:12:00"), 9,1)
            )
        linkBrecyclr(BList)

    }

    //API 연결, 리사이클러뷰 띄우기
    private fun getBoardData(author:String){
        val retrofitManager = PostRetrofitManager.getInstance(this)
        retrofitManager.board(author){response ->
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
        val Badapter = BoardAdapter(this,boardList)
        val BlayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.boardExcsPostRecyclr.layoutManager = BlayoutManager
        binding.boardExcsPostRecyclr.adapter = Badapter

        Badapter.setItemSpacing(binding.boardExcsPostRecyclr, 15)
    }
}