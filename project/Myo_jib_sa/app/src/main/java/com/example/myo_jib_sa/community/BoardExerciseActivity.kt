package com.example.myo_jib_sa.community

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private var boardId:Int=0
    private var missionImg:String=""
    private var page:Int=1

    private var isLoading = false
    private var isLoadingMore = false

    private lateinit var Badapter: BoardAdapter


    private var boardList:MutableList<Articles> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBoardExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardId=intent.getIntExtra("boardId", 0)

        //게시판 화면 띄우기
        getBoardData(Constance.jwt, boardId.toLong())

        //뒤로가기 버튼
        binding.boardExcsBackBtn.setOnClickListener {
            finish()
        }

        //글쓰기
        binding.boardPostingBtn.setOnClickListener {
            val intent= Intent(this, WritePostingActivity::class.java)
            intent.putExtra("boardId", boardId)
            startActivity(intent)
        }

        //페이징
        binding.boardExcsPostRecyclr.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // 스크롤이 최하단에 도달했을 때만 페이지 요청
                if (!isLoading && !isLoadingMore && visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                    // 스크롤이 마지막 아이템에 도달하면 다음 페이지 요청
                    isLoadingMore = true
                    page++
                    getBoardData(Constance.jwt, boardId.toLong())
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                // 스크롤 상태 변경 시 호출되는 부분
                // newState: 스크롤 상태
                // 이곳에서 스크롤 상태에 따른 작업을 수행
            }
        })


        //관리자 페이지 넘어가기
        binding.boardExcsNameTxt.setOnClickListener(View.OnClickListener {
            // TextView 클릭될 시 할 코드작성 todo: 주석 제거 하기
            if(true /*hostId==Constance.USER_ID*/){
                val intent=Intent(this, ManagerPageActivity::class.java)
                intent.putExtra("boardId", boardId)
                intent.putExtra("missionImg", missionImg)
                startActivity(intent)
            }

        })

        //미션 인증 페이지 넘어가기
        //todo: missionId 값 인텐트로 넘겨주기
        binding.boardExcsMissiomTxt.setOnClickListener {
            val intent=Intent(this, MissionCertificationActivity::class.java)
            intent.putExtra("missionId", missionId)
            intent.putExtra("boardId", boardId)
            startActivity(intent)
        }

    }

    //API 연결, 리사이클러뷰 띄우기
    private fun getBoardData(author:String ,id:Long){

        //게시판 이름
        when(id.toInt()){
            Constance.ART_ID-> {
                binding.boardExcsNameTxt.text="예술 게시판"
            }
            Constance.FREE_ID-> {
                binding.boardExcsNameTxt.text="자유 게시판"
            }
            Constance.EXERCISE_ID-> {
                binding.boardExcsNameTxt.text="운동 게시판"
            }
        }


        val retrofitManager = PostBoardRetrofitManager.getInstance(this)
        retrofitManager.board(author,page ,id){response ->
            if(response.isSuccess=="true"){
                val list:List<Articles> = response.result.articleLists
                boardList=list.toMutableList()
                hostId=response.result.categoryHostId
                Log.d("페이지 수", page.toString())
                if(list?.isNotEmpty()==true){
                    if(page!=1){
                        boardList.addAll(list)
                        Badapter.updateData(boardList)
                        Badapter.notifyDataSetChanged()

                    }else{
                        //로그
                        Log.d("게시판 API boardList 확인", boardList[0].articleTitle)
                        Log.d("게시판 API boardList 확인", boardList[0].likeCount.toString())
                        Log.d("게시판 API boardList 확인", boardList[0].commentCount.toString())
                        Log.d("게시판 API boardList 확인", boardList[0].uploadTime.toString())

                        Log.d("게시판 API missionID 확인", response.result.mainMissionId.toString())

                        missionId=response.result.mainMissionId
                        missionImg=response.result.categoryImage

                        //리사이클러뷰 연결
                        linkBrecyclr(boardList)
                    }
                    isLoadingMore = false // 페이지 요청 완료 후 isLoadingMore를 false로 설정
                }else{
                    Log.d("게시판 API 리사이클러뷰 어댑터로 리스트 전달", "List가 비었다네요")
                    page--
                }
                isLoading = false
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
        if (!::Badapter.isInitialized) { // 어댑터가 초기화되지 않았다면
            Badapter = BoardAdapter(this, boardList.toMutableList(), boardId)
            val BlayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            binding.boardExcsPostRecyclr.layoutManager = BlayoutManager
            binding.boardExcsPostRecyclr.adapter = Badapter
        } else {
            // 어댑터가 이미 초기화되었다면 기존 어댑터의 데이터를 업데이트
            Badapter.updateData(boardList)
        }

        //Badapter.setItemSpacing(binding.boardExcsPostRecyclr, 15)
    }

    override fun onResume() {
        super.onResume()
        //게시판 화면 띄우기
        page=1
        getBoardData(Constance.jwt, boardId.toLong())

    }

}