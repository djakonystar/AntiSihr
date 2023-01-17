package dev.djakonystar.antisihr.ui.audio

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
import dev.djakonystar.antisihr.data.models.AudioModel
import dev.djakonystar.antisihr.databinding.ScreenAudioBinding
import dev.djakonystar.antisihr.databinding.ScreenAudioPlayerBinding
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
class AudioPlayerScreen : Fragment(R.layout.screen_audio_player) {
    private val binding: ScreenAudioPlayerBinding by viewBinding(ScreenAudioPlayerBinding::bind)
    private val viewModel: AudioScreenViewModel by viewModels<AudioScreenViewModelImpl>()
    private val args: AudioPlayerScreenArgs by navArgs()


    //temp value
    private var isFavourite = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.icBack.clicks().debounce(200).onEach {
            findNavController().navigateUp()
        }.launchIn(lifecycleScope)

        binding.icFavourite.clicks().debounce(200).onEach {
            isFavourite=!isFavourite
            if (isFavourite){
                binding.icFavourite.setImageResource(R.drawable.ic_favourites)
            }else{
                binding.icFavourite.setImageResource(R.drawable.ic_favourites_filled)
            }
        }.launchIn(lifecycleScope)
    }


    private fun initObservers() {
        binding.tvAudioAuthor.text = args.author
        binding.tvAudioName.text = args.name
        binding.icAudioImage.setImageWithGlide(requireContext(), args.image)
        lifecycleScope.launchWhenResumed {
            visibilityOfBottomNavigationView.emit(false)
        }
    }
}