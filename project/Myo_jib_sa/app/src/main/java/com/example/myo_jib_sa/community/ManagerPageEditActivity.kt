package com.example.myo_jib_sa.community

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.manager.ManagerRetrofitManager
import com.example.myo_jib_sa.community.Retrofit.post.ArticleImage
import com.example.myo_jib_sa.community.Retrofit.post.PostRetrofitManager
import com.example.myo_jib_sa.community.adapter.WritePostImgAdapter
import com.example.myo_jib_sa.databinding.ActivityManagerPageBinding
import com.example.myo_jib_sa.databinding.ActivityManagerPageEditBinding
import java.io.ByteArrayOutputStream

class ManagerPageEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManagerPageEditBinding
    private var imgUri:Uri= Uri.EMPTY

    //갤러리 REQUEST_CODE
    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityManagerPageEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val boardId =intent.getLongExtra("boardId", 0)

        //바꾼 사진 저장
        binding.managerPageCompleteBtn.setOnClickListener {
            setPhoto(Constance.jwt, boardId){isSuccess->
                if(isSuccess){ //저장 성공 시에만 종료
                    val resultIntent = Intent()
                    resultIntent.putExtra("imgPath", imgUri)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }else{
                    showToast("바꾼 사진을 저장하지 못했습니다.")
                }

            }
        }

        //갤러리에서 사진 선택하기
        binding.managerPageImgEditBtn.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }
        binding.managerPageImg

    }

    //사진 저장 api
    private fun setPhoto(author:String, boardId:Long, callback: (Boolean) -> Unit){
        val retrofitManager = ManagerRetrofitManager.getInstance(this)
        val imgpath = getRealPathFromURI(imgUri)
        val base64Img = encodeImageToBase64(imgpath.toString())
        retrofitManager.missionImgEdit(author,base64Img.toString() ,boardId){response ->
            if(response){
                callback(true)
            } else {
                callback(false)
            }


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            // 선택한 이미지를 해당 이미지뷰에 표시
            selectedImageUri?.let { uri ->
                val imageView: ImageView = binding.managerPageImg
                imageView.setImageURI(uri)
                imgUri=uri
            }
        }
    }

    //fun getRealPathFromURI() 이미지 url을 실제 파일 경로로 변환
    private fun getRealPathFromURI(uri: Uri?): String? {
        if (uri == null) return null

        var realPath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = this.contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                realPath = it.getString(columnIndex)
            }
            it.close()
        }
        return realPath
    }

    //Base64로 인코딩하기
    fun encodeImageToBase64(imagePath: String): String? {
        val bitmap = BitmapFactory.decodeFile(imagePath)
        if (bitmap != null) {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val byteArray = baos.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
        return null
    }

    //토스트 메시지 띄우기
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}