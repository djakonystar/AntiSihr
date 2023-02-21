package dev.djakonystar.antisihr.ui.library

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.MainActivity
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.library.LibraryResultData
import dev.djakonystar.antisihr.databinding.ScreenLibraryBinding
import dev.djakonystar.antisihr.presentation.library.LibraryScreenViewModel
import dev.djakonystar.antisihr.presentation.library.impl.LibraryScreenViewModelImpl
import dev.djakonystar.antisihr.ui.adapters.LibraryAdapter
import dev.djakonystar.antisihr.utils.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class LibraryScreen : Fragment(R.layout.screen_library) {
    private val binding: ScreenLibraryBinding by viewBinding(ScreenLibraryBinding::bind)
    private val viewModel: LibraryScreenViewModel by viewModels<LibraryScreenViewModelImpl>()
    private val allLibrary = mutableListOf<LibraryResultData>()

    private var _adapter: LibraryAdapter? = null
    private val adapter: LibraryAdapter get() = _adapter!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as MainActivity).setStatusBarColor(R.color.white)

        initListeners()
        initObservers()

        lifecycleScope.launchWhenResumed {
            viewModel.getListOfSectionsLibrary()
//            visibilityOfLoadingAnimationView.emit(true)

        }
    }

    private fun initObservers() {
        viewModel.getListOfSectionsLibraryFlow.onEach {
            allLibrary.clear()
            allLibrary.addAll(it.result!!)
            adapter.librarySections = allLibrary
            visibilityOfLoadingAnimationView.emit(false)
        }.launchIn(lifecycleScope)

        viewModel.messageFlow.onEach {
            visibilityOfLoadingAnimationView.emit(false)
            showSnackBar(requireView(), it)
        }.launchIn(lifecycleScope)

        viewModel.errorFlow.onEach {
            visibilityOfLoadingAnimationView.emit(false)
        }.launchIn(lifecycleScope)
    }

    private fun initListeners() {
        _adapter = LibraryAdapter()
        binding.rcLibrary.adapter = adapter
        binding.expandableLayout.duration = 300


        binding.etSearch.doAfterTextChanged {
            if (binding.etSearch.text.toString().isEmpty()) {
                hideKeyboard()
                adapter.librarySections = allLibrary
            } else {
                val newList = allLibrary.filter { r ->
                    r.description.contains(
                        binding.etSearch.text.toString(), ignoreCase = true
                    ) || r.name.contains(binding.etSearch.text.toString(), ignoreCase = true)
                }
                adapter.librarySections = newList
            }
        }

        binding.icFavourites.clicks().debounce(200).onEach {
            findNavController().navigate(
                LibraryScreenDirections.actionLibraryScreenToInnerLibraryScreen(
                    -1, getString(R.string.favourites), getString(R.string.only_selected_articles_are_displayed_here), true
                )
            )
        }.launchIn(lifecycleScope)

        binding.icSearch.clicks().debounce(200).onEach {
            binding.tvSearch.text = getString(R.string.search)
            binding.icFavourites.hide()
            binding.icSearch.hide()
            binding.icClose.show()
            binding.tvBody.hide()
            binding.expandableLayout.expand()
        }.launchIn(lifecycleScope)

        binding.icClose.clicks().debounce(200).onEach {
            binding.tvSearch.text = getString(R.string.library)
            binding.icClose.hide()
            binding.icFavourites.show()
            binding.icSearch.show()
            binding.tvBody.show()
            binding.expandableLayout.collapse()
            binding.etSearch.setText("")
            hideKeyboard()
        }.launchIn(lifecycleScope)


        adapter.setOnItemClickListener {
            findNavController().navigate(
                LibraryScreenDirections.actionLibraryScreenToInnerLibraryScreen(
                    it.id, it.name, it.description, false
                )
            )
        }
    }
}