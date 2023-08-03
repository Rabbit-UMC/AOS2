package com.example.myo_jib_sa.community.missionCert

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myo_jib_sa.community.Retrofit.Constance
import com.example.myo_jib_sa.community.Retrofit.missionCert.MissionCertRetrofitManager
import com.example.myo_jib_sa.community.WritePostingActivity
import com.example.myo_jib_sa.databinding.ActivityMissionCertificationWriteBinding

class MissionCertificationWriteActivity: AppCompatActivity() {
    private lateinit var binding:ActivityMissionCertificationWriteBinding
    private var imgPath:String=""
    private var boardId:Int=0

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMissionCertificationWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardId=intent.getIntExtra("boardId", 0)

        binding.missionCertImgBtn.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }

        binding.missionCertCompleteTxt.setOnClickListener {
            //todo: api 연결
            //todo: 게시판 아이디 첨부
            postImg(Constance.jwt, boardId.toLong()){ isSuccess->
                if(isSuccess){
                    finish()
                }else{
                    showToast("이미지 업로드에 실패했습니다.")
                }
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            // 선택한 이미지를 해당 이미지뷰에 표시
            selectedImageUri?.let { uri ->
                val imageView: ImageView = binding.missionCertImg
                imageView.setImageURI(uri)
                imgPath=uri.toString()
                // todo:이미지를 서버로 전송하거나 파일 경로로 변환하여 api에 전달할 수 있습니다.
                // 선택한 이미지를 서버로 전송하는 부분은 retrofitManager.postImageUpload(author, uri) 등의 메소드를 활용하여 구현하면 됩니다.
            }
        }
    }

    //이미지 첨부 api
    private fun postImg(author:String ,boardId:Long, callback: (Boolean) -> Unit){
        val retrofitManager = MissionCertRetrofitManager.getInstance(this)
        retrofitManager.postImg(author, boardId, imgPath){response ->
            if(response){
                Log.d("mission postImg", "postImge 성공")
                callback(true)
            } else {
                // API 호출은 성공했으나 isSuccess가 false인 경우 처리
                Log.d("mission postImg", "postImg 실패")
                callback(true)
            }


        }
    }

    //토스트 메시지
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}