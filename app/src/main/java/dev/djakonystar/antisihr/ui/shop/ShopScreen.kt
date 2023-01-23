package dev.djakonystar.antisihr.ui.shop

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.MainActivity
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.shop.SellerData
import dev.djakonystar.antisihr.databinding.ScreenShopBinding
import dev.djakonystar.antisihr.presentation.shop.ShopScreenViewModel
import dev.djakonystar.antisihr.presentation.shop.impl.ShopScreenViewModelImpl
import dev.djakonystar.antisihr.ui.adapters.ShopsAdapter
import dev.djakonystar.antisihr.utils.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks
import kotlin.random.Random

@AndroidEntryPoint
class ShopScreen : Fragment(R.layout.screen_shop) {
    private val binding by viewBinding(ScreenShopBinding::bind)
    private val viewModel: ShopScreenViewModel by viewModels<ShopScreenViewModelImpl>()
    private val sellersList = mutableListOf<SellerData>()
    private var _adapter: ShopsAdapter? = null
    private val adapter get() = _adapter!!


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
        initVariables()
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        adapter.setOnItemClickListener {
            findNavController().navigate(
                ShopScreenDirections.actionShopScreenToGoodInfoScreen(
                    it.id, it.isFavourite
                )
            )
        }

        binding.icSearch.clicks().debounce(200).onEach {
            binding.tvTitle.text = getString(R.string.search)
            binding.icFavorites.hide()
            binding.icSearch.hide()
            binding.icClose.show()
            binding.expandableLayout.expand()
        }.launchIn(lifecycleScope)

        binding.icClose.clicks().debounce(200).onEach {
            binding.tvTitle.text = getString(R.string.library)
            binding.icClose.hide()
            binding.icFavorites.show()
            binding.icSearch.show()
            binding.expandableLayout.collapse()
            binding.etSearch.setText("")
            hideKeyboard()
        }.launchIn(lifecycleScope)

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab?) {
                val tvTab = tab?.customView as TextView
                tvTab.typeface =
                    ResourcesCompat.getFont(requireContext(), R.font.nunito_extrabold_ttf)
                tvTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))

                lifecycleScope.launchWhenResumed {
                    viewModel.getAllProductsOfSeller(sellersList.find {
                        it.name == tvTab.text.toString()
                    }!!.id)
                }
            }

            override fun onTabUnselected(tab: Tab?) {
                val tvTab = tab?.customView as TextView
                tvTab.typeface = ResourcesCompat.getFont(requireContext(), R.font.nunito_medium)
                tvTab.setTextColor(Color.parseColor("#66000000"))
                lifecycleScope.launchWhenResumed {
                    viewModel.getAllProducts()
                }
            }

            override fun onTabReselected(tab: Tab?) {}
        })
    }

    private fun initObservers() {
        viewModel.getGoodsSuccessFlow.onEach {
            adapter.submitList(it)
            visibilityOfLoadingAnimationView.emit(false)
        }.launchIn(lifecycleScope)

        viewModel.getAllSellersSuccessFlow.onEach {
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

            it.result.forEachIndexed { index, sellerData ->
                val tabb = binding.tabLayout.newTab()
                val tvTabb = TextView(requireContext())
                tabb.customView = tvTabb
                tvTabb.text = sellerData.name
                tvTabb.layoutParams =
                    LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                tvTabb.typeface = ResourcesCompat.getFont(requireContext(), R.font.nunito_medium)
                tvTabb.setTextColor(Color.parseColor("#66000000"))
                binding.tabLayout.addTab(tabb)
            }
        }.launchIn(lifecycleScope)

        viewModel.messageFlow.onEach {
            showSnackBar(requireView(), it)
        }.launchIn(lifecycleScope)

        viewModel.errorFlow.onEach {
            Log.d("TTTT", "Error in ShopScreen, cause: ${it.message}")
        }.launchIn(lifecycleScope)
    }

    private fun initVariables() {
        _adapter = ShopsAdapter()
        binding.recyclerView.adapter = adapter

        binding.expandableLayout.duration = 300

    }
}
