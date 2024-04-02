package com.example.myo_jib_sa.community.post

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.myo_jib_sa.community.adapter.PostPictureViewpagerAdapter
import com.example.myo_jib_sa.community.api.post.ArticleImage
import com.example.myo_jib_sa.databinding.ActivityPostPictureBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PostPictureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostPictureBinding
    private var filePath=""
    private var current=0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPostPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemListJson = intent.getStringExtra("itemListJson")
        val itemType = object : TypeToken<List<ArticleImage>>() {}.type
        val item: List<ArticleImage> = Gson().fromJson(itemListJson, itemType)
        val currentPosition = intent.getIntExtra("current", 0)

        binding.postPictureIndexTxt.text=item.size.toString()

        Log.d("post picture 확인 엑티비티 item", item.toString())

        if (item != null) {
            linkViewpager(item , currentPosition)
            indexViewPager(item)
        }


        binding.postPictureCloseImg.setOnClickListener {
            finish()
        }

        binding.postPictureDownloadImg.setOnClickListener {
            Log.d("다운로드 눌림!", "다운로드 눌림 !!!")
            download(item[current].filePath)
        }

    }

    //뷰페이저 연결
    private fun linkViewpager(item:List<ArticleImage>, current:Int){
        val adapter = PostPictureViewpagerAdapter(this, item)
        //뷰페이져 어댑터 연결
        binding.postPictureViewPager.adapter = adapter
        binding.postPictureViewPager.currentItem=current
    }

    //뷰페이저 선택되었을 떄
    private fun indexViewPager(item:List<ArticleImage>){
        //뷰페이저 페이지 선택 되었을 때
        val onPageChangeListener = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // 페이지가 선택되었을 때 실행되는 로직을 여기에 작성합니다.
                val page = position + 1
                binding.postPictureCurrentIndexTxt.text=page.toString()
                filePath=item[position].filePath
                current=position

            }

            override fun onPageScrollStateChanged(state: Int) {
                // 페이지 스크롤 상태가 변경될 때 실행되는 로직을 여기에 작성합니다.
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // 페이지가 스크롤되는 동안 실행되는 로직을 여기에 작성합니다.
            }
        }
        binding.postPictureViewPager.registerOnPageChangeCallback(onPageChangeListener)
    }

    //사진 다운로드
    private fun download(path:String){
        val request = Request.Builder()
            .url(path)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 다운로드 실패 처리
                runOnUiThread {
                    // 토스트 메시지 출력 (UI 스레드에서 실행되도록 보장)
                    showToast("사진 저장 실패1")
                }

            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val inputStream = responseBody.byteStream()
                        val fileName = path.substringAfterLast("/")
                        val picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        val file = File(picturesDirectory, fileName)

                        Log.d("파일", file.toString())

                        try {
                            Log.d("트라이", file.toString())
                            FileOutputStream(file).use { outputStream ->
                                Log.d("카피  inputStream.copyTo(outputStream)", outputStream.toString())
                                inputStream.copyTo(outputStream)
                                Log.d("카피  inputStream.copyTo(outputStream)", outputStream.toString())
                            }
                            inputStream.close()

                            runOnUiThread {
                                showToast("사진 저장 완료")
                            }
                        } catch (e: IOException) {
                            runOnUiThread {
                                showToast("사진 저장 실패2")
                            }
                        } finally {
                            inputStream.close()
                        }
                    }
                } else {
                    runOnUiThread {
                        showToast("사진 저장 실패3")
                    }
                }
            }
        })
    }

    //토스트 메시지
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}