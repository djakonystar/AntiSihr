package dev.djakonystar.antisihr.ui.feedback

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.drawerlayout.AddFeedbackData
import dev.djakonystar.antisihr.databinding.ScreenAboutBinding
import dev.djakonystar.antisihr.databinding.ScreenFeedbackBinding
import dev.djakonystar.antisihr.presentation.drawer.MainViewModel
import dev.djakonystar.antisihr.presentation.drawer.impl.MainViewModelImpl
import dev.djakonystar.antisihr.ui.feedback.validator.FeedbackValidator
import dev.djakonystar.antisihr.utils.showSnackBar
import dev.djakonystar.antisihr.utils.visibilityOfBottomNavigationView
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class FeedbackScreen : Fragment(R.layout.screen_feedback) {
    private val binding: ScreenFeedbackBinding by viewBinding(ScreenFeedbackBinding::bind)
    private val viewModel: MainViewModel by viewModels<MainViewModelImpl>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    lifecycleScope.launchWhenResumed {
                        visibilityOfBottomNavigationView.emit(true)
                    }
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.sendFeedbackDataSuccesFlow.onEach {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setMessage(it.result.toString())
            alertDialog.setPositiveButton(getString(R.string.close)) { _: DialogInterface, _: Int ->
                binding.etName.setText("")
                binding.etPhone.setText("")
                binding.etTopic.setText("")
                binding.etText.setText("")
                lifecycleScope.launchWhenResumed {
                    visibilityOfBottomNavigationView.emit(true)
                }
            }
            alertDialog.show()
        }.launchIn(lifecycleScope)

        viewModel.messageFlow.onEach {
            showSnackBar(requireView(), it)
        }.launchIn(lifecycleScope)
    }

    private fun initListeners() {
        binding.icBack.clicks().debounce(200).onEach {
            visibilityOfBottomNavigationView.emit(true)
        }.launchIn(lifecycleScope)

        binding.btnSend.clicks().debounce(200).onEach {
            val data = AddFeedbackData(
                binding.etName.text.toString(),
                binding.etPhone.text.toString(),
                binding.etTopic.text.toString(),
                binding.etText.text.toString()
            )
            if (FeedbackValidator(data).isValid()) {
                viewModel.addFeedback(data)
            } else {
                if (FeedbackValidator(data).isNotValidName()) {
                    binding.tilName.error = "Please enter your name"
                }
                if (FeedbackValidator(data).isNotValidPhone()) {
                    binding.tilPhone.error = "Please enter your phone"
                }
                if (FeedbackValidator(data).isNotValidTitle()) {
                    binding.tilTopic.error = "Please enter title"
                }
                if (FeedbackValidator(data).isNotValidDescription()) {
                    binding.tilText.error = "Please enter description"
                }
            }
        }.launchIn(lifecycleScope)


        binding.etName.doAfterTextChanged {
            binding.tilName.isErrorEnabled = false
        }
        binding.etPhone.doAfterTextChanged {
            binding.tilPhone.isErrorEnabled = false
        }
        binding.etTopic.doAfterTextChanged {
            binding.tilTopic.isErrorEnabled = false
        }
        binding.etText.doAfterTextChanged {
            binding.tilText.isErrorEnabled = false
        }
    }
}