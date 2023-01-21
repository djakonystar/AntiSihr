package dev.djakonystar.antisihr.ui.library

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.LibraryModel
import dev.djakonystar.antisihr.databinding.ScreenLibraryBinding
import dev.djakonystar.antisihr.presentation.library.LibraryScreenViewModel
import dev.djakonystar.antisihr.presentation.library.impl.LibraryScreenViewModelImpl
import dev.djakonystar.antisihr.ui.adapters.LibraryAdapter
import dev.djakonystar.antisihr.utils.hide
import dev.djakonystar.antisihr.utils.show
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class LibraryScreen : Fragment(R.layout.screen_library) {
    private val binding: ScreenLibraryBinding by viewBinding(ScreenLibraryBinding::bind)
    private val viewModel: LibraryScreenViewModel by viewModels<LibraryScreenViewModelImpl>()
    private var _adapter: LibraryAdapter? = null
    private val adapter: LibraryAdapter get() = _adapter!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initListeners()
        initObservers()
        lifecycleScope.launchWhenCreated {
            viewModel.getListOfSectionsLibrary()
        }


    }

    private fun initObservers() {
        viewModel.getListOfSectionsLibraryFlow.onEach {
            adapter.submitList(it.result!!)
        }.launchIn(lifecycleScope)
    }

    private fun initListeners() {
        _adapter = LibraryAdapter()
        binding.rcLibrary.adapter = adapter
        binding.expandableLayout.duration = 500


        binding.icSearch.clicks().debounce(200).onEach {
            binding.tvSearch.text = getString(R.string.search)
            binding.icMenu.hide()
            binding.icSearch.hide()
            binding.icClose.show()
            binding.expandableLayout.expand()
        }.launchIn(lifecycleScope)

        binding.icClose.clicks().debounce(200).onEach {
            binding.tvSearch.text = getString(R.string.library)
            binding.icClose.hide()
            binding.icMenu.show()
            binding.icSearch.show()
            binding.expandableLayout.collapse()
        }.launchIn(lifecycleScope)


        adapter.setOnItemClickListener {
            findNavController().navigate(
                LibraryScreenDirections.actionLibraryScreenToInnerLibraryScreen(
                    it.id, it.name, it.description
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _adapter = null
    }
}