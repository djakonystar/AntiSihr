package dev.djakonystar.antisihr.ui.shop

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.databinding.ScreenGoodInfoBinding
import dev.djakonystar.antisihr.presentation.shop.GoodInfoScreenViewModel
import dev.djakonystar.antisihr.presentation.shop.impl.GoodInfoScreenViewModelImpl
import dev.djakonystar.antisihr.ui.shop.adapter.ImageAdapter
import dev.djakonystar.antisihr.utils.hide
import dev.djakonystar.antisihr.utils.showSnackBar
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class GoodInfoScreen : Fragment(R.layout.screen_good_info) {
    private val binding: ScreenGoodInfoBinding by viewBinding(ScreenGoodInfoBinding::bind)
    private val viewModel: GoodInfoScreenViewModel by viewModels<GoodInfoScreenViewModelImpl>()
    private lateinit var adapter: ImageAdapter
    private val args: GoodInfoScreenArgs by navArgs()
    private var isFavourite: Boolean = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initListeners()
        initObservers()
        lifecycleScope.launchWhenResumed {
            viewModel.getProductInfo(args.id)
            isFavourite = args.isFavourite
        }
    }

    private fun initListeners() {
        binding.icFavourites.clicks().debounce(200).onEach {
            isFavourite = !isFavourite
            setFavouriteDrawable()
        }.launchIn(lifecycleScope)

        binding.icBack.clicks().debounce(200).onEach {
            findNavController().popBackStack()
        }.launchIn(lifecycleScope)
    }

    private fun initObservers() {
        viewModel.getProductInfoSuccesFlow.onEach {
            val item = it.result!!.first()
            setFavouriteDrawable()
            binding.tvTitle.text = item.name
            binding.tvCapacityTitle.isVisible = item.weight != null
            binding.tvCapacity.isVisible = item.weight != null
            binding.tvCapacity.text = (item.weight ?: 0).toString()
            val price = if (item.price % 1 == 0.0) item.price.toInt().toString()
            else item.price.toString()
            binding.tvPrice.text = price
            binding.tvDescription.text = item.description
            binding.tvShopName.text = item.seller?.name
            binding.tvShopName.isSelected = true
            initViewPagerAdapter(listOf(item.image))

            binding.btnBuy.clicks().debounce(200).onEach {
                val uri = Uri.parse(item.seller?.url)
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }.launchIn(lifecycleScope)
        }.launchIn(lifecycleScope)

        viewModel.messageFlow.onEach {
            showSnackBar(requireView(), it)
        }.launchIn(lifecycleScope)

        viewModel.errorFlow.onEach {
            Log.d("TTTT", "Error in GoodInfoScreen, cause: ${it.message.toString()}")
        }.launchIn(lifecycleScope)
    }

    private fun initViewPagerAdapter(list: List<String>) {
        adapter = ImageAdapter(list, requireActivity().supportFragmentManager, lifecycle)
        binding.vpImages.adapter = adapter
        if (list.size == 1) {
            binding.dotsIndicator.hide()
        } else {
            binding.dotsIndicator.setViewPager2(binding.vpImages)
        }
    }

    private fun setFavouriteDrawable() {
        if (isFavourite) {
            binding.icFavourites.setImageResource(R.drawable.ic_favourites_filled)
            binding.icFavourites.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.fav_color)
            )
        } else {
            binding.icFavourites.setImageResource(R.drawable.ic_favourites)
            binding.icFavourites.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.black)
            )
        }
    }
}