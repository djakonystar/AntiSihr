package dev.djakonystar.antisihr.ui.shop.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ImageAdapter(private val adsList: List<String>, fm: FragmentManager, lc: Lifecycle) :
    FragmentStateAdapter(fm, lc) {
    override fun getItemCount(): Int = adsList.size

    override fun createFragment(position: Int): Fragment = ImageFragment().apply {
        val bundle = Bundle()
        adsList[position].apply {
            bundle.putString("imageUri", adsList[position])
        }
        arguments = bundle
    }
}