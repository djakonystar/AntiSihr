package dev.djakonystar.antisihr.ui.library.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.databinding.BottomArticleInfoBinding
import dev.djakonystar.antisihr.databinding.BottomReaderInfoBinding
import dev.djakonystar.antisihr.presentation.library.InnerLibraryScreenViewModel
import dev.djakonystar.antisihr.presentation.library.impl.InnerLibraryScreenViewModelImpl
import dev.djakonystar.antisihr.presentation.readers.ReadersScreenViewModel
import dev.djakonystar.antisihr.presentation.readers.impl.ReadersScreenViewModelImpl
import dev.djakonystar.antisihr.ui.adapters.SocialMediaAdapter
import dev.djakonystar.antisihr.utils.setImageWithGlide
import dev.djakonystar.antisihr.utils.showSnackBar
import dev.djakonystar.antisihr.utils.toPhoneNumber
import dev.djakonystar.antisihr.utils.toast
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks

@AndroidEntryPoint
class ArticleDetailBottomFragment : BottomSheetDialogFragment() {
    private val viewModel: InnerLibraryScreenViewModel by viewModels<InnerLibraryScreenViewModelImpl>()
    private val args: ArticleDetailBottomFragmentArgs by navArgs()
    private val binding: BottomArticleInfoBinding by viewBinding(BottomArticleInfoBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_article_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenResumed {
            viewModel.getArticle(args.id)
        }

        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.tvClose.clicks().debounce(200).onEach {
            dismiss()
        }.launchIn(lifecycleScope)
    }

    private fun initObservers() {
        viewModel.getArticleSuccessFlow.onEach {
            val article = it.result!!.first()
            binding.tvTitle.text = article.title
            binding.tvBody.text = article.lead
            binding.tvBodySecond.text = article.description
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
