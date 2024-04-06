package com.example.myo_jib_sa.community.post

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class PostPictureActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var binding: ActivityPostPictureBinding
    private var filePath=""
    private var current=0;
    private var item: List<ArticleImage> = listOf()

    companion object {
        private const val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPostPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemListJson = intent.getStringExtra("itemListJson")
        val itemType = object : TypeToken<List<ArticleImage>>() {}.type
        item = Gson().fromJson(itemListJson, itemType)
        current = intent.getIntExtra("current", 0)

        binding.postPictureIndexTxt.text=item.size.toString()

        Log.d("post picture 확인 엑티비티 item", item.toString())

        if (item != null) {
            linkViewpager(item , current)
            indexViewPager(item)
        }


        binding.postPictureCloseImg.setOnClickListener {
            finish()
        }

        binding.postPictureDownloadImg.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
                )
            } else {
                // 이미 권한이 부여되어 있으면 이미지를 다운로드합니다.
                download(item[current].filePath)
            }

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
    // 이미지 다운로드 및 저장
    // 이미지 다운로드 및 저장
    private fun download(path: String) {

        val request = Request.Builder()
            .url(path)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 다운로드 실패 처리
                runOnUiThread {
                    showToast("이미지를 다운로드하는 중 오류가 발생했습니다.")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val fileName = path.substringAfterLast("/")
                val imageRoot = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                    "$fileName.jpg"
                ) // 이미지가 저장될 경로를 지정
                Log.d("이미지 루트 imageRoot", imageRoot.toString())

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        try {
                            FileOutputStream(imageRoot).use { outputStream ->
                                outputStream.write(responseBody.bytes())
                                outputStream.close()
                                runOnUiThread {
                                    showToast("이미지가 다운로드되었습니다.")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("PostPictureActivity", "Error: ${e.message}", e)
                            runOnUiThread {
                                showToast("이미지 저장 중 오류가 발생했습니다.")
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        showToast("이미지를 다운로드하는 중 오류가 발생했습니다.")
                    }
                }
            }
        })
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여되면 이미지를 다운로드합니다.
                download(item[current].filePath)
            } else {
                showToast("외부 저장소 쓰기 권한이 거부되었습니다.")
            }
        }
    }

    // 토스트 메시지 출력
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}