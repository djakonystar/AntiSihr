package dev.djakonystar.antisihr.ui.shop

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.MainActivity
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.library.LibraryResultData
import dev.djakonystar.antisihr.data.models.shop.SellerData
import dev.djakonystar.antisihr.data.room.entity.ShopItemBookmarked
import dev.djakonystar.antisihr.databinding.ScreenShopBinding
import dev.djakonystar.antisihr.presentation.shop.ShopScreenViewModel
import dev.djakonystar.antisihr.presentation.shop.impl.ShopScreenViewModelImpl
import dev.djakonystar.antisihr.ui.adapters.ShopsAdapter
import dev.djakonystar.antisihr.utils.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.swiperefreshlayout.refreshes
import ru.ldralighieri.corbind.view.clicks
import kotlin.random.Random
import kotlin.random.nextInt

@AndroidEntryPoint
class ShopScreen : Fragment(R.layout.screen_shop) {
    private val binding by viewBinding(ScreenShopBinding::bind)
    private val viewModel: ShopScreenViewModel by viewModels<ShopScreenViewModelImpl>()
    private val sellersList = mutableListOf<SellerData>()
    private val allProducts = mutableListOf<ShopItemBookmarked>()
    private var _adapter: ShopsAdapter? = null
    private val adapter get() = _adapter!!
    private var selectedCategoryId = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    (requireActivity() as MainActivity).changeBottomNavigationSelectedItem(true)
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as MainActivity).setStatusBarColor(R.color.white)

        initVariables()
        initListeners()
        initObservers()

        lifecycleScope.launchWhenResumed {
            viewModel.getAllSellers()
            viewModel.getAllProducts()
            visibilityOfLoadingAnimationView.emit(true)
        }
    }

    private fun initListeners() {
        _adapter = ShopsAdapter()
        binding.recyclerView.adapter = adapter
        binding.expandableLayout.duration = 300


        adapter.setOnItemClickListener {
            findNavController().navigate(
                ShopScreenDirections.actionShopScreenToGoodInfoScreen(
                    it.id, it.isFavourite
                )
            )
        }

        adapter.setOnItemBookmarkClickListener {
            lifecycleScope.launchWhenResumed {
                if (it.isFavourite) {
                    viewModel.addToBookmarked(it)
                } else {
                    viewModel.deleteFromBookmarked(it)
                }
            }
        }

        binding.icFavorites.clicks().debounce(200).onEach {
            findNavController().navigate(ShopScreenDirections.actionShopScreenToProductsBookmarkedBottomSheet())
        }.launchIn(lifecycleScope)

        binding.icSearch.clicks().debounce(200).onEach {
            binding.tvTitle.text = getString(R.string.search)
            binding.icFavorites.hide()
            binding.icSearch.hide()
            binding.icClose.show()
            binding.expandableLayout.expand()
        }.launchIn(lifecycleScope)

        binding.icClose.clicks().debounce(200).onEach {
            binding.tvTitle.text = getString(R.string.shop)
            binding.icClose.hide()
            binding.icFavorites.show()
            binding.icSearch.show()
            binding.expandableLayout.collapse()
            binding.etSearch.setText("")
            hideKeyboard()
        }.launchIn(lifecycleScope)

        binding.etSearch.addTextChangedListener {
            val searchList = if (selectedCategoryId != -1) {
                allProducts.filter { it.sellerId == selectedCategoryId }
            } else {
                allProducts
            }

            if (binding.etSearch.text.toString().isEmpty()) {
                adapter.shopItems = searchList
            } else {
                val searchValue = binding.etSearch.text.toString()
                val newList = searchList.filter { p ->
                    p.name.contains(searchValue, ignoreCase = true)
                }
                adapter.shopItems = newList
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab?) {
                val tvTab = tab?.customView as TextView
                tvTab.typeface =
                    ResourcesCompat.getFont(requireContext(), R.font.nunito_extrabold_ttf)
                tvTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))

                lifecycleScope.launchWhenResumed {
                    val sellerData = sellersList.find { it.name == tvTab.text.toString() }
                    sellerData?.let {
                        viewModel.getAllProductsOfSeller(it.id)
                        selectedCategoryId = it.id
                    } ?: let {
                        viewModel.getAllProducts()
                        selectedCategoryId = -1
                    }
                }
            }

            override fun onTabUnselected(tab: Tab?) {
                val tvTab = tab?.customView as TextView
                tvTab.typeface = ResourcesCompat.getFont(requireContext(), R.font.nunito_medium)
                tvTab.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.second_text_color
                    )
                )
            }

            override fun onTabReselected(tab: Tab?) {}
        })

        binding.swipeRefreshLayout.refreshes().onEach {
            binding.swipeRefreshLayout.isRefreshing = false
            viewModel.getAllSellers()
            viewModel.getAllProducts()
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun initObservers() {
        viewModel.getGoodsSuccessFlow.onEach {
            allProducts.clear()
            allProducts.addAll(it)
            adapter.shopItems = allProducts
            visibilityOfLoadingAnimationView.emit(false)
        }.launchIn(lifecycleScope)

        closeOfShopBottomSheetFlow.onEach {
            viewModel.getAllProducts()
        }.launchIn(lifecycleScope)


        viewModel.getAllSellersSuccessFlow.onEach {
            sellersList.clear()
            binding.tabLayout.removeAllTabs()
            sellersList.addAll(it.result!!)
            val tab = binding.tabLayout.newTab()
            val tvTab = TextView(requireContext())
            tab.customView = tvTab
            tvTab.text = getString(R.string.all)
            tvTab.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            val typeface = ResourcesCompat.getFont(requireContext(), R.font.nunito_extrabold_ttf)
            tvTab.typeface = typeface
            tvTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
            binding.tabLayout.addTab(tab)

            it.result.forEach { sellerData ->
                val tabb = binding.tabLayout.newTab()
                val tvTabb = TextView(requireContext())
                tvTabb.ellipsize = TextUtils.TruncateAt.END
                tvTabb.isSingleLine = true
                tabb.customView = tvTabb
                tvTabb.text = sellerData.name
                tvTabb.layoutParams =
                    LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                tvTabb.typeface = ResourcesCompat.getFont(requireContext(), R.font.nunito_medium)
                tvTabb.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.second_text_color
                    )
                )
                binding.tabLayout.addTab(tabb)
            }
        }.launchIn(lifecycleScope)

        viewModel.messageFlow.onEach {
            showSnackBar(requireView(), it)
        }.launchIn(lifecycleScope)

        viewModel.errorFlow.onEach {
            Log.d("TTTT", "Error in ShopScreen, cause: ${it.message}")
            it.printStackTrace()
        }.launchIn(lifecycleScope)
    }

    private fun initVariables() {
        _adapter = ShopsAdapter()
        binding.recyclerView.adapter = adapter

        binding.expandableLayout.duration = 300

    }
}
