package dev.djakonystar.antisihr.ui.readers.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.databinding.BottomReaderInfoBinding
import dev.djakonystar.antisihr.presentation.readers.ReadersScreenViewModel
import dev.djakonystar.antisihr.presentation.readers.impl.ReadersScreenViewModelImpl
import dev.djakonystar.antisihr.ui.adapters.SocialMediaAdapter
import dev.djakonystar.antisihr.utils.setImageWithGlide
import dev.djakonystar.antisihr.utils.toPhoneNumber
import dev.djakonystar.antisihr.utils.toast
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class ReaderDetailBottomFragment : BottomSheetDialogFragment() {
    private val viewModel: ReadersScreenViewModel by viewModels<ReadersScreenViewModelImpl>()
    private val args: ReaderDetailBottomFragmentArgs by navArgs()
    private val binding: BottomReaderInfoBinding by viewBinding(BottomReaderInfoBinding::bind)
    private var _adapter: SocialMediaAdapter? = null
    private val adapter: SocialMediaAdapter get() = _adapter!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_reader_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenResumed {
            viewModel.getReaderById(args.readerId)
        }

        initListeners()
        initObservers()
    }

    private fun initListeners() {
        _adapter = SocialMediaAdapter()
        binding.apply {
            rvSocialMedia.adapter = adapter
            tvClose.clicks().debounce(200).onEach {
                dismiss()
            }.launchIn(lifecycleScope)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initObservers() {
        viewModel.getReaderByIdSuccessFlow.onEach {
            val reader = it.first()
            binding.apply {
                icPhoto.setImageWithGlide(requireContext(), reader.image)
                tvAuthor.text = "${reader.surname} ${reader.name}"
                if (reader.city != null) {
                    tvCityName.text = reader.city.name
                }
                adapter.submitList(reader.socialNetworks)
                tvAddressTitle.isVisible = reader.address != null
                tvAddress.isVisible = reader.address != null
                dividerFirst.isVisible = reader.address != null
                tvAddress.text = reader.address ?: ""
                tvPhone.text = reader.phone.toPhoneNumber
                tvDescription.text = reader.description

                ivPhone.clicks().debounce(200).onEach {
                    val phone = reader.phone.toPhoneNumber.filter { c -> c == '+' || c.isDigit() }
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phone)))
                    requireActivity().startActivity(intent)
                }.launchIn(lifecycleScope)
            }
        }.launchIn(lifecycleScope)

        viewModel.messageFlow.onEach {
            toast(it)
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
