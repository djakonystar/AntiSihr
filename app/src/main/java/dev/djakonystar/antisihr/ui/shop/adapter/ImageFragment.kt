package dev.djakonystar.antisihr.ui.shop.adapter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.databinding.ItemViewpagerImageBinding

class ImageFragment : Fragment(R.layout.item_viewpager_image) {

    private val binding by viewBinding(ItemViewpagerImageBinding::bind)
    private val imageUri: String by lazy { arguments?.getString("imageUri") ?: "" }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this).load(imageUri).into(binding.icImage)
    }
}