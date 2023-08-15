package com.example.myo_jib_sa.community

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myo_jib_sa.community.Retrofit.BoardPost.Articles
import com.example.myo_jib_sa.community.Retrofit.BoardPost.PostBoardRetrofitManager
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.adapter.BoardAdapter
import com.example.myo_jib_sa.community.missionCert.MissionCertificationActivity
import com.example.myo_jib_sa.databinding.ActivityBoardExerciseBinding
import java.sql.Timestamp

class BoardExerciseActivity : AppCompatActivity() {

    private lateinit var binding:ActivityBoardExerciseBinding
    private var hostId:Long=0
    private var missionId:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBoardExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //게시판 화면 띄우기
        getBoardData(Constance.jwt, 1, Constance.EXERCISE_ID.toLong())

        //뒤로가기 버튼
        binding.boardExcsBackBtn.setOnClickListener {
            finish()
        }

        //글쓰기
        binding.boardPostingBtn.setOnClickListener {
            val intent= Intent(this, WritePostingActivity::class.java)
            val boardId=Constance.EXERCISE_ID
            intent.putExtra("boardId", boardId)
            startActivity(intent)
        }

        //관리자 페이지 넘어가기
        binding.boardExcsNameTxt.setOnClickListener(View.OnClickListener {
            // TextView 클릭될 시 할 코드작성 todo: 주석 제거 하기
            if(true /*hostId==Constance.USER_ID*/){
                val intent=Intent(this, ManagerPageActivity::class.java)
                val boardId=Constance.EXERCISE_ID
                intent.putExtra("boardId", boardId)
                startActivity(intent)
            }

        })

        //미션 인증 페이지 넘어가기
        //todo: missionId 값 인텐트로 넘겨주기
        binding.boardExcsMissiomTxt.setOnClickListener {
            missionId=1
            val intent=Intent(this, MissionCertificationActivity::class.java)
            intent.putExtra("missionId", missionId)
            intent.putExtra("boardId", Constance.EXERCISE_ID)
            startActivity(intent)
        }

    }

    //API 연결, 리사이클러뷰 띄우기
    private fun getBoardData(author:String,page:Int ,id:Long){
        val retrofitManager = PostBoardRetrofitManager.getInstance(this)
        retrofitManager.board(author,page ,id){response ->
            if(response.isSuccess=="true"){
                val boardList:List<Articles> = response.result.articleLists
                hostId=response.result.categoryHostId
                if(boardList?.isNotEmpty()==true){

                    //로그
                    Log.d("게시판 API boardList 확인", boardList[0].articleTitle)
                    Log.d("게시판 API boardList 확인", boardList[0].likeCount.toString())
                    Log.d("게시판 API boardList 확인", boardList[0].commentCount.toString())
                    Log.d("게시판 API boardList 확인", boardList[0].uploadTime.toString())


                    //리사이클러뷰 연결
                    linkBrecyclr(boardList)


                }else{
                    Log.d("게시판 API 리사이클러뷰 어댑터로 리스트 전달", "List가 비었다네요")
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
        val Badapter = BoardAdapter(this,boardList, Constance.EXERCISE_ID)
        val BlayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.boardExcsPostRecyclr.layoutManager = BlayoutManager
        binding.boardExcsPostRecyclr.adapter = Badapter

        Badapter.setItemSpacing(binding.boardExcsPostRecyclr, 15)
    }


}