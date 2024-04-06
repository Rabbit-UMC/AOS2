package com.example.myo_jib_sa.community.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class ImageDownloadTask(private val listener: OnImageDownloadedListener) :
    AsyncTask<String, Void, Bitmap?>() {

    // 다운로드 완료 후 호출 할 리스너
    interface OnImageDownloadedListener {
        fun onImageDownloaded(bitmap: Bitmap?)
    }

    override fun doInBackground(vararg strings: String): Bitmap? {
        return try {
            // 파라미터로 받은 url로 부터 이미지 다운로드
            val inputStream = URL(strings[0]).openStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(bitmap: Bitmap?) {
        super.onPostExecute(bitmap)
        // 이미지 다운로드 완료 후 리스너 호출
        listener.onImageDownloaded(bitmap)
    }

    // 이미지를 디바이스에 저장하는 메서드
    companion object {
        fun saveBitmapToDevice(bitmap: Bitmap?, fileName: String) {
            bitmap?.let {
                try {
                    // 외부 저장소에 저장할 디렉토리 생성
                    val directory =
                        File(Environment.getExternalStorageDirectory().toString() + File.separator + "MyAppImages")
                    if (!directory.exists()) {
                        directory.mkdirs()
                    }
                    // 저장할 파일 경로 설정
                    val file = File(directory, fileName)

                    // 이미지를 파일에 쓰기
                    val outputStream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
