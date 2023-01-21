package dev.djakonystar.antisihr.ui.audio

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.MainActivity
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.AudioModel
import dev.djakonystar.antisihr.databinding.ScreenAudioBinding
import dev.djakonystar.antisihr.presentation.audio.AudioScreenViewModel
import dev.djakonystar.antisihr.presentation.audio.impl.AudioScreenViewModelImpl
import dev.djakonystar.antisihr.ui.adapters.AudioAdapter
import dev.djakonystar.antisihr.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class AudioScreen : Fragment(R.layout.screen_audio) {
    private val binding: ScreenAudioBinding by viewBinding(ScreenAudioBinding::bind)
    private val viewModel: AudioScreenViewModel by viewModels<AudioScreenViewModelImpl>()

    private var _adapter: AudioAdapter? = null
    private val adapter: AudioAdapter get() = _adapter!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    (requireActivity() as MainActivity).changeBottomNavigationSelectedItem(true)
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenResumed {
            visibilityOfBottomNavigationView.emit(true)
            visibilityOfLoadingAnimationView.emit(true)
            viewModel.getListOfTests()
        }

        initListeners()
        initObservers()

    }

    private fun initListeners() {
        _adapter = AudioAdapter()
        binding.rcAudios.adapter = adapter
        binding.expandableLayout.duration = 500


        binding.icSearch.clicks().debounce(200).onEach {
            binding.icFavourites.hide()
            binding.icSearch.hide()
            binding.icClose.show()
            binding.expandableLayout.expand()
        }.launchIn(lifecycleScope)

        binding.icClose.clicks().debounce(200).onEach {
            binding.expandableLayout.collapse()
            binding.icClose.hide()
            binding.icFavourites.show()
            binding.icSearch.show()
        }.launchIn(lifecycleScope)

        adapter.setOnItemClickListener {
            findNavController().navigate(
                AudioScreenDirections.actionAudioScreenToAudioInfoScreen(
                    it.id, it.name, it.author, it.url, it.image
                )
            )
        }

        adapter.setOnPlayClickListener {
            lifecycleScope.launchWhenResumed {
                bottomAudioPlayer.emit(it)
                visibilityAudioPlayer.emit(true)
            }
        }

    }


    private fun initObservers() {
        viewModel.getListOfAudiosSuccessFlow.onEach {
            adapter.submitList(it)
            visibilityOfLoadingAnimationView.emit(false)
        }.launchIn(lifecycleScope)
    }
}