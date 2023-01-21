package dev.djakonystar.antisihr.ui.library

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.library.InnerLibraryBookmarkData
import dev.djakonystar.antisihr.data.models.library.LibraryResultData
import dev.djakonystar.antisihr.data.room.entity.ArticlesBookmarked
import dev.djakonystar.antisihr.databinding.ScreenInnerLibraryBinding
import dev.djakonystar.antisihr.presentation.library.InnerLibraryScreenViewModel
import dev.djakonystar.antisihr.presentation.library.impl.InnerLibraryScreenViewModelImpl
import dev.djakonystar.antisihr.ui.adapters.InnerLibraryAdapter
import dev.djakonystar.antisihr.utils.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.swiperefreshlayout.refreshes
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class InnerLibraryScreen : Fragment(R.layout.screen_inner_library) {
    private val binding: ScreenInnerLibraryBinding by viewBinding(ScreenInnerLibraryBinding::bind)
    private val viewModel: InnerLibraryScreenViewModel by viewModels<InnerLibraryScreenViewModelImpl>()
    private val args: InnerLibraryScreenArgs by navArgs()
    private var _adapter: InnerLibraryAdapter? = null
    private val adapter: InnerLibraryAdapter get() = _adapter!!

    private val allArticles = mutableListOf<InnerLibraryBookmarkData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initListeners()
        initObservers()
        lifecycleScope.launchWhenCreated {
            if (args.isFavourite) {
                viewModel.getFavouriteListOfArticles()
            } else {
                viewModel.getListOfArticles(args.id)
            }
            visibilityOfLoadingAnimationView.emit(true)
        }
    }

    private fun initObservers() {
        viewModel.getListOfArticlesFlow.onEach {
            adapter.submitList(it)
            allArticles.clear()
            allArticles.addAll(it)
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
        _adapter = InnerLibraryAdapter()
        binding.rcArticles.adapter = adapter
        binding.tvLibrary.text = args.name
        binding.tvBody.text = args.description


        binding.icSearch.clicks().debounce(200).onEach {
            binding.tvLibrary.text = getString(R.string.search)
            binding.icMenu.hide()
            binding.icSearch.hide()
            binding.icClose.show()
            binding.tvBody.hide()
            binding.expandableLayout.expand()
        }.launchIn(lifecycleScope)

        binding.icClose.clicks().debounce(200).onEach {
            binding.tvLibrary.text = getString(R.string.library)
            binding.icClose.hide()
            binding.icMenu.show()
            binding.icSearch.show()
            binding.expandableLayout.collapse()
            binding.etSearch.setText("")
            hideKeyboard()
        }.launchIn(lifecycleScope)

        binding.icMenu.clicks().debounce(200).onEach {
            findNavController().navigateUp()
        }.launchIn(lifecycleScope)

        adapter.setOnItemClickListener {
            findNavController().navigate(
                InnerLibraryScreenDirections.actionInnerLibraryScreenToBottomArticleDialog(
                    it.id, it.isBookmarked
                )
            )
        }

        adapter.setOnBookmarkClickListener {
            lifecycleScope.launchWhenResumed {
                if (it.isBookmarked) {
                    viewModel.addArticleToBookmarkeds(ArticlesBookmarked(it.id, it.lead, it.title))
                } else {
                    viewModel.deleteArticleFromBookmarkeds(
                        ArticlesBookmarked(
                            it.id, it.lead, it.title
                        )
                    )
                }
            }
        }

        binding.swipeRefreshLayout.refreshes().onEach {
            binding.swipeRefreshLayout.isRefreshing = false
            if (args.isFavourite) {
                viewModel.getFavouriteListOfArticles()
            } else {
                viewModel.getListOfArticles(args.id)
            }
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)


        binding.etSearch.doAfterTextChanged {
            if (binding.etSearch.text.toString().isEmpty()) {
                hideKeyboard()
                adapter.submitList(allArticles)
            } else {
                val newList = allArticles.filter { r ->
                    r.title.contains(
                        binding.etSearch.text.toString(), ignoreCase = true
                    ) || r.lead.contains(binding.etSearch.text.toString(), ignoreCase = true)
                }
                adapter.submitList(newList)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _adapter = null
    }
}