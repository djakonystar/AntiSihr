package dev.djakonystar.antisihr.ui.library

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.databinding.ScreenInnerLibraryBinding
import dev.djakonystar.antisihr.presentation.library.InnerLibraryScreenViewModel
import dev.djakonystar.antisihr.presentation.library.impl.InnerLibraryScreenViewModelImpl
import dev.djakonystar.antisihr.ui.adapters.InnerLibraryAdapter
import dev.djakonystar.antisihr.utils.hide
import dev.djakonystar.antisihr.utils.show
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class InnerLibraryScreen : Fragment(R.layout.screen_inner_library) {
    private val binding: ScreenInnerLibraryBinding by viewBinding(ScreenInnerLibraryBinding::bind)
    private val viewModel: InnerLibraryScreenViewModel by viewModels<InnerLibraryScreenViewModelImpl>()
    private var _adapter: InnerLibraryAdapter? = null
    private val args: InnerLibraryScreenArgs by navArgs()
    private val adapter: InnerLibraryAdapter get() = _adapter!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initListeners()
        initObservers()
        lifecycleScope.launchWhenCreated {
            viewModel.getListOfArticles(args.id)
        }


    }

    private fun initObservers() {
        viewModel.getListOfArticlesFlow.onEach {
            adapter.submitList(it.result)
        }.launchIn(lifecycleScope)
    }

    private fun initListeners() {
        _adapter = InnerLibraryAdapter()
        binding.rcArticles.adapter = adapter
        binding.tvLibrary.text = args.name


        binding.icSearch.clicks().debounce(200).onEach {
            binding.tvLibrary.text = getString(R.string.search)
            binding.icMenu.hide()
            binding.icSearch.hide()
            binding.icClose.show()
            binding.tvBody.hide()
            binding.expandableLayout.expand()
        }.launchIn(lifecycleScope)

        binding.icClose.clicks().debounce(200).onEach {
            binding.tvLibrary.text = args.name
            binding.tvBody.show()
            binding.icClose.hide()
            binding.icMenu.show()
            binding.icSearch.show()
            binding.expandableLayout.collapse()
        }.launchIn(lifecycleScope)

        binding.icMenu.clicks().debounce(200).onEach {
            findNavController().navigateUp()
        }.launchIn(lifecycleScope)

        adapter.setOnItemClickListener {
            findNavController().navigate(
                InnerLibraryScreenDirections.actionInnerLibraryScreenToBottomArticleDialog(
                    it.id
                )
            )

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _adapter = null
    }
}