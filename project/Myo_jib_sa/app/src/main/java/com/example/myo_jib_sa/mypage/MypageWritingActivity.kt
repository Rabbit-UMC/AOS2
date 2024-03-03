package com.example.myo_jib_sa.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.base.MyojibsaApplication
import com.example.myo_jib_sa.databinding.ActivityMypageWritingBinding
import com.example.myo_jib_sa.mypage.adapter.MypageWritingRVAdapter
import com.example.myo_jib_sa.mypage.api.GetWritingResponse
import com.example.myo_jib_sa.mypage.api.MypageAPI
import com.example.myo_jib_sa.mypage.api.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageWritingActivity : AppCompatActivity() {
    lateinit var binding : ActivityMypageWritingBinding
    private var isPost : Boolean = true

    val retrofit: MypageAPI = MyojibsaApplication.sRetrofit.create(MypageAPI::class.java)

    private lateinit var writingAdapter: MypageWritingRVAdapter

    private lateinit var recyclerView: RecyclerView
    private var decoration: RecyclerView.ItemDecoration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isPost = intent.getBooleanExtra("isPost", true)
        setToolbar()
        setRecyclerView()
    }
    private fun setToolbar() {
        binding.toolbarTitleTv.text = getString(if(isPost) R.string.mypage_post else R.string.mypage_comment)
        binding.editProfileBackBtn.setOnClickListener { finish() }
    }

    private fun setRecyclerView() {
        recyclerView = binding.myPageWritingRv
        getWritingCommentAPI(if(isPost) retrofit.getMyPost(0) else retrofit.getMyComment(0))
    }

    private fun getWritingCommentAPI(call: Call<GetWritingResponse>) {
        call.enqueue(object : Callback<GetWritingResponse> {
            override fun onResponse(call: Call<GetWritingResponse>, response: Response<GetWritingResponse>) {
                if (response.isSuccessful) {
                    val commentResponse = response.body()
                    val dataList = commentResponse?.result
                    if (commentResponse != null) {
                        if(commentResponse.errorCode=="403"){
                            val placeholderData = listOf(
                                Post(0L, "Placeholder Title2", "2023-08-25 13:30", 2, 3),
                                Post(1L, "Another Placeholder Title2", "2023-08-21 15:11", 4, 0)
                            )
                            setWritingRecyclerview(placeholderData)
                        }
                        else{
                            if (dataList != null) {
                                Log.d("mypost", commentResponse.toString())
                                // 어댑터 생성 및 리사이클러뷰에 설정
                                setWritingRecyclerview(dataList)
                            }
                        }
                    }
                } else {
                    // API 요청 실패 처리
                    Toast.makeText(this@MypageWritingActivity, "${response.body()}", Toast.LENGTH_SHORT).show()
                    val placeholderData = listOf(
                        Post(0L, "Placeholder Title", "2023-08-25 13:30", 2, 3),
                        Post(1L, "Another Placeholder Title", "2023-08-21 15:11", 4, 0)
                        // 원하는 만큼의 Placeholder 데이터를 추가할 수 있습니다.
                    )
                    setWritingRecyclerview(placeholderData)
                }
            }
            override fun onFailure(call: Call<GetWritingResponse>, t: Throwable) {
                // 네트워크 등의 문제로 API 요청이 실패한 경우 처리
                Toast.makeText(this@MypageWritingActivity, "서버 오류 발생", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun setWritingRecyclerview(dataList: List<Post>) {
        decoration?.let { recyclerView.removeItemDecoration(it) }

        writingAdapter = MypageWritingRVAdapter(
            this,
            dataList
        )

        recyclerView.adapter = writingAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 아이템 간격 설정 (옵션)
        decoration = MypageWritingRVAdapter.CustomItemDecoration(15) // decoration 변수 초기화
        recyclerView.addItemDecoration(decoration as MypageWritingRVAdapter.CustomItemDecoration)

        // 어댑터 설정 후 데이터 변경을 알림
        writingAdapter.notifyDataSetChanged()
    }
}