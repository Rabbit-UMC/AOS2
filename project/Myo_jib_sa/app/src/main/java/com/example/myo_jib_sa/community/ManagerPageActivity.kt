package com.example.myo_jib_sa.community

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.myo_jib_sa.community.api.manager.ManagerMissionJoinRequest
import com.example.myo_jib_sa.community.adapter.ManagerPageViewpagerAdapter
import com.example.myo_jib_sa.databinding.ActivityManagerPageBinding

class ManagerPageActivity : AppCompatActivity() {

    //todo: 지워
    val sample=listOf(
        ManagerMissionJoinRequest("title1","hello world", "11-24", "12-23","sgdahkjdb")
        ,ManagerMissionJoinRequest("title1","hello world", "11-24", "12-23","sgdahkjdb")
        ,ManagerMissionJoinRequest("title1","hello world", "11-24", "12-23","sgdahkjdb")
    )

    private lateinit var binding:ActivityManagerPageBinding
    private val mAdapter=ManagerPageViewpagerAdapter(this, sample)
    private val REQUEST_CODE=1
    private var missionImg:String=""

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityManagerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //관리자 페이지 이름 설정
        val boardId= intent.getIntExtra("boardId",0).toLong()

        //뷰페이저 프레그먼트 연결
        binding.managerMissionVp2.adapter=mAdapter

        //이미지 설정
       // binding.managerPageImg.clipToOutline=true //모서리 둥글게
        missionImg=intent.getStringExtra("missionImg").toString()

        if(missionImg.isNotBlank()){
           /* binding.managerPageImgIc.visibility= View.GONE
            binding.constraintLayout.backgroundTintList=
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
            Glide.with(this)
                .load(missionImg)
                .into(binding.managerPageImg)*/
        }else{
           /* binding.constraintLayout.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#F1F1F1"))
            setMissionIcon(boardId.toLong())*/
        }

        //게시판 이름
        when(boardId){
            Constance.ART_ID-> {
                binding.managerPageNameTxt.text="예술 묘집사"
            }
            Constance.FREE_ID-> {
                binding.managerPageNameTxt.text="자유 묘집사"
            }
            Constance.EXERCISE_ID-> {
                binding.managerPageNameTxt.text="운동 묘집사"
            }

        }

        //이미지 수정 페이지 이동
        /*binding.managerPageImgEditBtn.setOnClickListener{
            val intent= Intent(this, ManagerPageEditActivity::class.java)
            intent.putExtra("boardId", boardId.toLong())
            intent.putExtra("missionImg", missionImg)
            startActivityForResult(intent, REQUEST_CODE)
        }*/

        //묘방생 페이지로 이동
       /* binding.managerPageByeBtn.setOnClickListener {
            val intent=Intent(this, ManagerMissionCreateActivity::class.java)
            Log.d("묘방생 페이지로 이동", "묘방생 페이지로 이동")
            intent.putExtra("boardId", boardId)
            intent.putExtra("isBye", true)
            startActivity(intent)
        }*/

        //미션 생성 페이지로 이동
        /*binding.managerPageMissionBtn.setOnClickListener {
            Log.d("미션 생성 페이지로 이동", "미션 생성 페이지로 이동")
            val intent=Intent(this, ManagerMissionCreateActivity::class.java)
            intent.putExtra("boardId", boardId)
            startActivity(intent)
        }*/

        //뒤로가기
        binding.managerPageBackBtn.setOnClickListener {
            finish()
        }

    }

    //이미지 설정
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val imgPath = data.getStringExtra("imgPath")
            Log.d("바뀐 이미지", imgPath.toString())
            /*Glide.with(this)
                .load(imgPath)
                .into(binding.managerPageImg)*/


        }
    }

    //기본 이미지 설정
    private fun setMissionIcon(boardId:Long){
        /*when(boardId.toInt()){
            Constance.ART_ID-> {
                val drawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.ic_mission_art_p)
                binding.managerPageImgIc.setImageDrawable(drawable)
            }
            Constance.FREE_ID-> {
                val drawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.ic_mission_free_p)
                binding.managerPageImgIc.setImageDrawable(drawable)
            }
            Constance.EXERCISE_ID-> {
                val drawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.ic_mission_exercise_p)
                binding.managerPageImgIc.setImageDrawable(drawable)
            }
        }*/
    }


}