package com.example.myo_jib_sa.community

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.community.manager.ManagerPageActivity
import com.example.myo_jib_sa.community.api.BoardPost.Articles
import com.example.myo_jib_sa.community.api.BoardPost.PostBoardRetrofitManager
import com.example.myo_jib_sa.community.adapter.BoardAdapter
import com.example.myo_jib_sa.community.missionCert.MissionCertificationActivity
import com.example.myo_jib_sa.community.post.PostWrtieActivity
import com.example.myo_jib_sa.databinding.ActivityBoardBinding

class BoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardBinding
    private var hostId:Long=0

    //버튼 클릭
    private var isClick=false

    //아래 두개 관리자 페이지로 넘겨줌
    private var missionId:Long=0
    private var missionImg:String=""

    private var boardId:Long=0
    private var page:Int=0

    private var isLoading = false
    private var isLoadingMore = false

    private lateinit var Badapter: BoardAdapter

    //베스트 게시판인지
    private var isBest:Boolean=false

    private var boardList:MutableList<Articles> = mutableListOf()


    //플로팅 토글
    private var isFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardId=intent.getLongExtra("boardId", 0)
        isBest=intent.getBooleanExtra("isBest", false)
        Log.d("게시판 아이디", boardId.toString())


        if(isBest){
            setBestBoard()
        }

        //게시판 화면 띄우기
        getBoardData(boardId)

        //뒤로가기 버튼
        binding.boardExcsBackBtn.setOnClickListener {
            finish()
        }

        //페이징
        paging()

        setFABClickEvent()
        setupOutsideTouchClose()

    }

    override fun onResume() {
        super.onResume()
        //게시판 화면 띄우기
        page=0
        getBoardData(boardId)

    }


    //페이징 기능
    private fun paging(){
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
                    getBoardData(boardId)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                // 스크롤 상태 변경 시 호출되는 부분
                // newState: 스크롤 상태
                // 이곳에서 스크롤 상태에 따른 작업을 수행
            }
        })
    }

    //API 연결, 리사이클러뷰 띄우기
    private fun getBoardData(id:Long){

        //게시판 이름
        if(isBest){
            binding.boardExcsNameTxt.text="인기 게시글"
            getBestData()
            return
        }
            when(id){
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
        retrofitManager.board(page ,id){response ->
            if(response.isSuccess){
                val list:List<Articles> = response.result.articleLists
                boardList=list.toMutableList()
                hostId=response.result.categoryHostId
                missionId=response.result.mainMissionId
                Log.d("페이지 수", page.toString())
                if(list?.isNotEmpty()==true){
                    if(page!=0){
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
                        if(response.result.categoryImage.isNotEmpty()){
                            missionImg=response.result.categoryImage
                        }else{
                            missionImg=""
                        }


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
                val returnCode = response.errorCode
                val returnMsg = response.errorMessage

                Log.d("게시판 API isSuccess가 false", "${returnCode}  ${returnMsg}")
            }


        }
    }

    //미션 리사이클러뷰, 어댑터 연결
    private fun linkBrecyclr(boardList:List<Articles>){

        //미션
            Badapter = BoardAdapter(this, boardList.toMutableList(), boardId)
            val BlayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            binding.boardExcsPostRecyclr.layoutManager = BlayoutManager
            binding.boardExcsPostRecyclr.adapter = Badapter
    }
    private fun getBestData(){
        val retrofitManager = PostBoardRetrofitManager.getInstance(this)
        retrofitManager.popular(page){response ->
            if(response.isSuccess){
                val list:MutableList<Articles> = mutableListOf()
                var tempList=Articles(0,",",",",-1,-1)
                for(i in 1..response.result.size){
                    tempList= Articles(response.result[i-1].articleId
                        ,response.result[i-1].articleTitle
                        ,response.result[i-1].uploadTime
                        ,response.result[i-1].likeCount
                        ,response.result[i-1].commentCount)
                    list.add(tempList)
                }

                boardList=list.toMutableList()
                //hostId=response.result.categoryHostId
                //missionId=response.result.mainMissionId
                Log.d("페이지 수", page.toString())
                if(list?.isNotEmpty()==true){
                    if(page!=0){
                        boardList.addAll(list)
                        Badapter.updateData(boardList)
                        Badapter.notifyDataSetChanged()

                    }else{
                        //로그
                        Log.d("게시판 API boardList 확인", boardList[0].articleTitle)
                        Log.d("게시판 API boardList 확인", boardList[0].likeCount.toString())
                        Log.d("게시판 API boardList 확인", boardList[0].commentCount.toString())
                        Log.d("게시판 API boardList 확인", boardList[0].uploadTime.toString())

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
                val returnCode = response.errorCode
                val returnMsg = response.errorMessage
                Log.d("게시판 API isSuccess가 false", "${returnCode}  ${returnMsg}")
            }


        }
    }

    private fun setBestBoard(){
        binding.boardPostingBtn.hide()
        binding.boardMissionBtn.hide()
    }

    //플로팅 버튼
    private fun setFABClickEvent() {

        // 플로팅 버튼 클릭시 애니메이션 동작 기능
        binding.boardPostingBtn.setOnClickListener {
            if(isClick){
                val intent=Intent(this,PostWrtieActivity::class.java)
                intent.putExtra("boardId", boardId)
                startActivity(intent)
            }else{
                isClick=!isClick
                toggleFab()
            }
        }

        // 플로팅 버튼 클릭 이벤트
        binding.boardMissionBtn.setOnClickListener {
            val intent=Intent(this, MissionCertificationActivity::class.java)
            intent.putExtra("missionId", missionId)
            intent.putExtra("boardId", boardId)
            startActivity(intent)
        }

    }

    // onCreate() 메서드나 다른 곳에서 호출하여 사용할 수 있습니다.
    private fun setupOutsideTouchClose() {
        // 액티비티의 레이아웃에 대한 클릭 이벤트 처리

        binding.boardExcsPostRecyclr.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    // 스크롤 다운 이벤트 처리
                    if (isClick) {
                        // 플로팅 버튼이 열려있는 상태라면 닫습니다.
                        isClick = false
                        toggleFab()
                    }
                } else {
                    // 스크롤 업 이벤트 처리
                    if (isClick) {
                        // 플로팅 버튼이 열려있는 상태라면 닫습니다.
                        isClick = false
                        toggleFab()
                    }
                }
            }
        })

        binding.boardExcsPostRecyclr.setOnClickListener {
            if (isClick) {
                // 플로팅 버튼이 열려있는 상태라면 닫습니다.
                isClick = false
                toggleFab()
            }
        }
    }

    //플로팅 버튼 꺼내기
    private fun toggleFab() {

        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션
        if (isFabOpen) {
            ObjectAnimator.ofFloat(binding.boardMissionBtn, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.boardPostingBtn, View.ROTATION, 0f, 0f).apply { start() }
        } else { // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션
            ObjectAnimator.ofFloat(binding.boardMissionBtn, "translationY", -180f).apply { start() }
            ObjectAnimator.ofFloat(binding.boardPostingBtn, View.ROTATION, 0f, 0f).apply { start() }
        }

        isFabOpen = !isFabOpen

    }
}