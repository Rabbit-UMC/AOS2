package com.example.myo_jib_sa.community.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myo_jib_sa.community.PostPictureFragment
import com.example.myo_jib_sa.community.api.post.ArticleImage

class PostPictureViewpagerAdapter
    (
    fragmentActivity: FragmentActivity
    , private val item: List<ArticleImage>
    ) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = item.size

    override fun createFragment(position: Int): Fragment {

        val fragment = PostPictureFragment()

        val bundle = Bundle()
        bundle.putString("filePath", item[position].filePath)
        fragment.arguments = bundle

        return fragment
    }

}