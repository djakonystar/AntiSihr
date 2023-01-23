package dev.djakonystar.antisihr.ui.shop.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.databinding.BottomShopFavouritesBinding
import dev.djakonystar.antisihr.presentation.shop.ShopScreenViewModel
import dev.djakonystar.antisihr.presentation.shop.impl.ShopScreenViewModelImpl
import dev.djakonystar.antisihr.ui.shop.adapter.BookmarkedProductsAdapter
import dev.djakonystar.antisihr.utils.showSnackBar
import dev.djakonystar.antisihr.utils.toast
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks

@AndroidEntryPoint
class ProductsBookmarkedBottomSheet : BottomSheetDialogFragment() {
    private val viewModel: ShopScreenViewModel by viewModels<ShopScreenViewModelImpl>()
    private val binding: BottomShopFavouritesBinding by viewBinding(BottomShopFavouritesBinding::bind)
    private var _adapter: BookmarkedProductsAdapter? = null
    private val adapter get() = _adapter!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_shop_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenResumed {
            viewModel.getAllBookmarkedProducts()
        }
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        _adapter = BookmarkedProductsAdapter()
        binding.recyclerView.adapter = adapter

        binding.tvClose.clicks().debounce(200).onEach {
            dismiss()
        }.launchIn(lifecycleScope)


        adapter.setOnClickListener {
            showSnackBar(requireView(),"Basildi: ${it.name} and ${it.price}")
        }

        adapter.setOnRemoveClickListener {
            lifecycleScope.launchWhenResumed {
                viewModel.deleteFromBookmarked(it)
            }
        }
    }

    private fun initObservers() {
        viewModel.getGoodsSuccessFlow.onEach {
            adapter.submitList(it)
        }.launchIn(lifecycleScope)

        viewModel.messageFlow.onEach {
            showSnackBar(requireView(), it)
        }.launchIn(lifecycleScope)

        viewModel.errorFlow.onEach {
            it.localizedMessage?.let { message -> toast(message) }
        }.launchIn(lifecycleScope)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                behaviour.skipCollapsed = true
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }
}
