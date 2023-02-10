package dev.djakonystar.antisihr.ui.test.dialog

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.MainActivity
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.TestQuestionData
import dev.djakonystar.antisihr.databinding.BottomTestBinding
import dev.djakonystar.antisihr.presentation.test.TestScreenViewModel
import dev.djakonystar.antisihr.presentation.test.impl.TestScreenViewModelImpl
import dev.djakonystar.antisihr.utils.hide
import dev.djakonystar.antisihr.utils.show
import dev.djakonystar.antisihr.utils.toast
import dev.djakonystar.antisihr.utils.visibilityOfLoadingAnimationView
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks

@AndroidEntryPoint
class TestBottomFragment : BottomSheetDialogFragment() {
    private val viewModel: TestScreenViewModel by viewModels<TestScreenViewModelImpl>()
    private val args: TestBottomFragmentArgs by navArgs()
    private val binding: BottomTestBinding by viewBinding(BottomTestBinding::bind)
    private var currentPosition = -1
    private lateinit var list: List<TestQuestionData>
    private var countOfYes = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenResumed {
            viewModel.getTests(args.id)
        }
        initObservers()
        initListeners()
    }

    private fun initObservers() {
        viewModel.getTestsSuccessFlow.onEach {
            list = it.result.questions
            setQuestion()
            binding.maxNumber.text = "/${list.size}"
            binding.tvTitleTest.text = args.name
        }.launchIn(lifecycleScope)

        viewModel.getResultForTestsSuccessFlow.onEach {
            binding.linearTestButtons.hide()
            binding.tvQuestion.hide()
            binding.tvResult.show()
            binding.linearResultButtons.show()
            binding.tvTitleTest.text = getString(R.string.score, args.name.lowercase())
            binding.tvResult.text = it.result.first().name
            binding.loadingAnimation.hide()

            if (!it.result.first().disease) {
                binding.treatmentCourse.hide()
            }
        }.launchIn(lifecycleScope)
    }

    private fun initListeners() {
        binding.btnYes.clicks().debounce(200).onEach {
            countOfYes++
            setQuestion()
        }.launchIn(lifecycleScope)

        binding.btnNo.clicks().debounce(200).onEach {
            setQuestion()
        }.launchIn(lifecycleScope)

        binding.btnNext.clicks().debounce(200).onEach {
            setQuestion()
        }.launchIn(lifecycleScope)

        binding.tvCloseTest.clicks().debounce(200).onEach {
            dialog?.dismiss()
        }.launchIn(lifecycleScope)

        binding.btnClose.clicks().debounce(200).onEach {
            dialog?.dismiss()
        }.launchIn(lifecycleScope)

        binding.treatmentCourse.clicks().debounce(200).onEach {
            (requireActivity() as MainActivity).changeBottomNavigationSelectedItem(false)
        }.launchIn(lifecycleScope)
    }


    private fun setQuestion() {
        currentPosition++
        if (currentPosition <= list.size - 1) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                binding.progressStep.setProgress(
                    (((currentPosition + 1) / list.size.toDouble()) * 100).toInt(), true
                )
            } else {
                binding.progressStep.progress =
                    (((currentPosition + 1) / list.size.toDouble()) * 100).toInt()
            }
            binding.currentNumber.text = "${currentPosition + 1}"
            binding.tvQuestion.text = list[currentPosition].name
        } else {
            lifecycleScope.launchWhenResumed {
                binding.loadingAnimation.show()
                viewModel.getResultForTests(args.id, countOfYes)
            }
        }
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