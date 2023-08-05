package com.example.myo_jib_sa.community.Retrofit.post

import android.content.Context
import android.util.Log
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.RetrofitClient
import com.example.myo_jib_sa.community.Retrofit.communityHome.HomeResponse
import retrofit2.Call
import retrofit2.Response

class PostRetrofitManager (context: Context){
    //레트로핏 인터페이스 가져오기기
    private val retrofit : PostRetrofitITFC? = RetrofitClient.getClient(Constance.BASEURL)?.create(
        PostRetrofitITFC::class.java)

    companion object {
        private var instance: PostRetrofitManager? = null

        // 싱글톤 인스턴스를 가져오는 메서드
        fun getInstance(context: Context): PostRetrofitManager {
            if (instance == null) {
                instance = PostRetrofitManager(context)
            }
            return instance as PostRetrofitManager
        }

    }

    //게시물 조회 PostViewResponse를 반환
    fun postView(author:String, articleId: Long,completion: (PostViewResponse) -> Unit){
        val call: Call<PostViewResponse> = retrofit?.postView(author,articleId) ?: return

        call.enqueue(object : retrofit2.Callback<PostViewResponse> {
            override fun onResponse(
                call: Call<PostViewResponse>,
                response: Response<PostViewResponse>
            ) {
                Log.d("게시물 조회", "RetrofitManager 게시물 조회 onResponse \t :${response.message()} ")
                val response: PostViewResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess=="true") {
                        Log.d("게시물 조회",
                            "RetrofitManager 게시물 조회 is Success\t :${response.code} ")
                        completion(response)
                    } else {
                        Log.d("게시물 조회",
                            "RetrofitManager 게시물 조회 is NOT Success\t :${response.code} ")
                    }
                } else {
                    Log.d("게시물 조회", "RetrofitManager 게시물 조회 null")
                }
            }

            override fun onFailure(call: Call<PostViewResponse>, t: Throwable) {
                Log.d("게시물 조회", "RetrofitManager 게시물 조회 onFailure \t :$t ")
            }
        })
    }



    //게시물 삭제, 삭제 완료제지 봔환 (boolean)
    fun postDelete(author:String, articleId: Long,completion: (isSucces:Boolean) -> Unit){
        val call: Call<SimpleResponse> = retrofit?.postDelete(author,articleId) ?: return

        call.enqueue(object : retrofit2.Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                Log.d("게시물 삭제", "RetrofitManager 게시물 삭제 onResponse \t :${response.message()} ")
                val response: SimpleResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess=="true") {
                        Log.d("게시물 삭제",
                            "RetrofitManager 게시물 삭제 is Success\t :${response.code} ")
                        completion(true)
                    } else {
                        Log.d("게시물 삭제",
                            "RetrofitManager 게시물 삭제 is NOT Success\t :${response.code} ")
                        completion(false)
                    }
                } else {
                    Log.d("게시물 삭제", "RetrofitManager 게시물 삭제 null")
                }
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.d("게시물 삭제", "RetrofitManager 게시물 삭제 onFailure \t :$t ")
            }
        })
    }

    //게시물 생성, 생성 완료인지 반환(boolean)
    fun postCreate(author:String,request:PostCreateRequest, categoryId:Long, completion: (isSucces:Boolean) -> Unit){
        val call: Call<SimpleResponse> = retrofit?.postCreate(author, request, categoryId) ?: return

        call.enqueue(object : retrofit2.Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                Log.d("게시물 생성", "RetrofitManager 게시물 생성 onResponse \t :${response.message()} ")
                val response: SimpleResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess=="true") {
                        Log.d("게시물 생성",
                            "RetrofitManager 게시물 생성 is Success\t :${response.code} ")
                        completion(true)
                    } else {
                        Log.d("게시물 생성",
                            "RetrofitManager 게시물 생성 is NOT Success\t :${response.code} ")
                        completion(false)
                    }
                } else {
                    Log.d("게시물 생성", "RetrofitManager 게시물 생성 null")
                }
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.d("게시물 생성", "RetrofitManager 게시물 조회 onFailure \t :$t ")
            }
        })
    }


    //게시물 수정, 수정 완료인지 봔환 (boolean)
    fun postEdit(author:String,request:PostEditRequest, articleId:Long,articleID: Long, completion: (isSucces:Boolean) -> Unit){
        val call: Call<SimpleResponse> = retrofit?.postEdit(author, request, articleId, articleID) ?: return

        call.enqueue(object : retrofit2.Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                Log.d("게시물 수정", "RetrofitManager 게시물 수정 onResponse \t :${response.message()} ")
                val response: SimpleResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess=="true") {
                        Log.d("게시물 수정",
                            "RetrofitManager 게시물 수정 is Success\t :${response.code} ")
                        completion(true)
                    } else {
                        Log.d("게시물 수정",
                            "RetrofitManager 게시물 수정 is NOT Success\t :${response.code} ")
                        completion(false)
                    }
                } else {
                    Log.d("게시물 수정", "RetrofitManager 게시물 수정 null")
                }
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.d("게시물 수정", "RetrofitManager 게시물 수정 onFailure \t :$t ")
            }
        })
    }


    //게시물 신고, 신고 완료인지 반환(Boolean)
    fun postReport(author: String, articleId: Long, completion: (isSucces:Boolean) -> Unit){
        val call: Call<SimpleResponse> = retrofit?.postReport(author, articleId) ?: return

        call.enqueue(object : retrofit2.Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                Log.d("게시물 신고", "RetrofitManager 게시물 신고 onResponse \t :${response.message()} ")
                val response: SimpleResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess=="true") {
                        Log.d("게시물 신고",
                            "RetrofitManager 게시물 신고 is Success\t :${response.code} ")
                        completion(true)
                    } else {
                        Log.d("게시물 신고",
                            "RetrofitManager 게시물 신고 is NOT Success\t :${response.code} ")
                        completion(false)
                    }
                } else {
                    Log.d("게시물 신고", "RetrofitManager 게시물 신고 null")
                }
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.d("게시물 신고", "RetrofitManager 게시물 신고 onFailure \t :$t ")
            }
        })
    }


    //게시물 좋아요, 좋아요 완료인지 반환(Boolean)
    fun postLike(author: String, articleId: Long, completion: (isSucces:Boolean) -> Unit){
        val call: Call<SimpleResponse> = retrofit?.postLike(author, articleId) ?: return

        call.enqueue(object : retrofit2.Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                Log.d("게시물 좋아요", "RetrofitManager 게시물 좋아요 onResponse \t :${response.message()} ")
                val response: SimpleResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess=="true") {
                        Log.d("게시물 좋아요",
                            "RetrofitManager 게시물 종아요 is Success\t :${response.code} ")
                        completion(true)
                    } else {
                        Log.d("게시물 좋아요",
                            "RetrofitManager 게시물 좋아요 is NOT Success\t :${response.code} ")
                        Log.d("게시물 좋아요",
                            "RetrofitManager 게시물 좋아요 is NOT Success\t :${response.result} ")
                        completion(false)
                    }
                } else {
                    Log.d("게시물 좋아요", "RetrofitManager 게시물 좋아요 null")
                }
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.d("게시물 좋아요", "RetrofitManager 게시물 좋아요 onFailure \t :$t ")
            }
        })
    }

    //게시물 좋아요 취소,완료인지 반환(Boolean)
    fun postUnlike(author: String, articleId: Long, completion: (isSucces:Boolean) -> Unit){
        val call: Call<SimpleResponse> = retrofit?.postUnlike(author, articleId) ?: return

        call.enqueue(object : retrofit2.Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                Log.d("게시물 좋아요 취소", "RetrofitManager 게시물 좋아요 취소 onResponse \t :${response.message()} ")
                val response: SimpleResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess=="true") {
                        Log.d("게시물 좋아요 취소",
                            "RetrofitManager 게시물 종아요 취소 is Success\t :${response.code} ")
                        completion(true)
                    } else {
                        Log.d("게시물 좋아요 취소",
                            "RetrofitManager 게시물 좋아요 is NOT Success\t :${response.code} ")
                        completion(false)
                    }
                } else {
                    Log.d("게시물 좋아요 취소", "RetrofitManager 게시물 좋아요 취소 null")
                }
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.d("게시물 좋아요 취소", "RetrofitManager 게시물 좋아요 취소 onFailure \t :$t ")
            }
        })
    }

    //게시물 댓글 작성 ,완료인지 반환(Boolean)
    fun postComment(author: String,content:String ,articleId: Long, completion: (isSucces:Boolean) -> Unit){
        val call: Call<SimpleResponse> = retrofit?.postComment(author, content,articleId) ?: return

        call.enqueue(object : retrofit2.Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                Log.d("게시물 댓글 달기", "RetrofitManager 게시물 댓글 달기 onResponse \t :${response.message()} ")
                val response: SimpleResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess=="true") {
                        Log.d("게시물 댓글 달기",
                            "RetrofitManager 게시물 댓글 달기 is Success\t :${response.code} ")
                        completion(true)
                    } else {
                        Log.d("게시물 댓글 달기",
                            "RetrofitManager 게시물 댓글 달기 is NOT Success\t :${response.code} ")
                        completion(false)
                    }
                } else {
                    Log.d("게시물 댓글 달기", "RetrofitManager 게시물 댓글 달기 null")
                }
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.d("게시물 댓글 달기", "RetrofitManager 게시물 댓글 달기 onFailure \t :$t ")
            }
        })
    }

    //게시글 댓글 삭제
    fun postCommentDelete(author: String,articleId: Long,commentId:Long, completion: (isSucces:Boolean) -> Unit){
        val call: Call<SimpleResponse> = retrofit?.postCommentDelete(author, articleId, commentId) ?: return

        call.enqueue(object : retrofit2.Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                Log.d("게시물 댓글 삭제", "RetrofitManager 게시물 댓글 삭제 onResponse \t :${response.message()} ")
                val response: SimpleResponse? = response?.body()
                if (response != null) {
                    if (response.isSuccess=="true") {
                        Log.d("게시물 댓글 삭제",
                            "RetrofitManager 게시물 댓글 삭제 is Success\t :${response.code} ")
                        Log.d("게시물 댓글 삭제",
                            "RetrofitManager 게시물 댓글 삭제 is Success\t :${response.result} ")
                        completion(true)
                    } else {
                        Log.d("게시물 댓글 삭제",
                            "RetrofitManager 게시물 댓글 삭제 is NOT Success\t :${response.code} ")
                        completion(false)
                    }
                } else {
                    Log.d("게시물 댓글 삭제", "RetrofitManager 게시물 댓글 삭제 null")
                }
            }
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Log.d("게시물 댓글 삭제", "RetrofitManager 게시물 댓글 삭제 onFailure \t :$t ")
            }
        })
    }
}