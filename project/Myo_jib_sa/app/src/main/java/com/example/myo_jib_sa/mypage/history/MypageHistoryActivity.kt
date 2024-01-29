package com.example.myo_jib_sa.mypage.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myo_jib_sa.R
import com.example.myo_jib_sa.databinding.ActivityMypageHistoryBinding

class MypageHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypageHistoryBinding
    private var isSuccessFragment = true
    private var nickname = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nickname = intent.getStringExtra("nickname").toString()

        initView()
        initListener()
    }

    private fun initView() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mypage_history_fl, MypageHistorySuccessFragment(nickname))
            .commit()
    }

    private fun initListener() {
        binding.mypageChangeBtn.setOnClickListener {
            if (isSuccessFragment) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.mypage_history_fl, MypageHistoryFailFragment(nickname))
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.mypage_history_fl, MypageHistorySuccessFragment(nickname))
                    .commit()
            }
            isSuccessFragment = !isSuccessFragment
        }

        binding.mypageBackBtn.setOnClickListener {
            finish()
        }
    }
}