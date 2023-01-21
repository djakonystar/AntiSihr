package dev.djakonystar.antisihr.ui.language

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.local.LocalStorage
import dev.djakonystar.antisihr.data.models.drawerlayout.LanguageData
import dev.djakonystar.antisihr.databinding.ScreenFeedbackBinding
import dev.djakonystar.antisihr.databinding.ScreenLanguageBinding
import dev.djakonystar.antisihr.presentation.drawer.MainViewModel
import dev.djakonystar.antisihr.presentation.drawer.impl.MainViewModelImpl
import dev.djakonystar.antisihr.utils.toast
import dev.djakonystar.antisihr.utils.visibilityOfBottomNavigationView
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks
import javax.inject.Inject


@AndroidEntryPoint
class LanguageScreen : Fragment(R.layout.screen_language) {
    @Inject
    lateinit var localStorage: LocalStorage

    private val binding: ScreenLanguageBinding by viewBinding(ScreenLanguageBinding::bind)
    private val viewModel: MainViewModel by viewModels<MainViewModelImpl>()
    private val languages = mutableListOf<LanguageData>()
    private var selectedPosition = -1

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
        lifecycleScope.launchWhenCreated {
            viewModel.getLanguages()
        }
    }

    private fun initListeners() {
        binding.icBack.clicks().debounce(200).onEach {
            visibilityOfBottomNavigationView.emit(true)
        }.launchIn(lifecycleScope)

        binding.tvSave.clicks().debounce(200).onEach {
            if (selectedPosition != -1) {
                localStorage.language = languages[selectedPosition].prefix
            } else {
                localStorage.language = languages.first().prefix
            }
            visibilityOfBottomNavigationView.emit(true)
        }.launchIn(lifecycleScope)
    }

    private fun initObservers() {
        viewModel.getLanguagesSuccessFlow.onEach {
            languages.clear()
            languages.addAll(it.result!!)
            //add radio buttons
        }.launchIn(lifecycleScope)
    }
}