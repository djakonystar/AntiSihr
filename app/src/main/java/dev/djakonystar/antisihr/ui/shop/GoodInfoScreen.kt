package dev.djakonystar.antisihr.ui.shop

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.databinding.ScreenGoodInfoBinding
import dev.djakonystar.antisihr.ui.shop.adapter.ImageAdapter
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class GoodInfoScreen : Fragment(R.layout.screen_good_info) {
    private val binding: ScreenGoodInfoBinding by viewBinding(ScreenGoodInfoBinding::bind)
    private lateinit var adapter: ImageAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initListeners()
    }

    private fun initListeners() {
        binding.icFavourites.clicks().debounce(200).onEach {

        }.launchIn(lifecycleScope)
    }

    private fun initViewPagerAdapter(list: List<String>) {
        adapter = ImageAdapter(list, requireActivity().supportFragmentManager, lifecycle)
        binding.vpImages.adapter = adapter
        binding.dotsIndicator.setViewPager2(binding.vpImages)
    }
}