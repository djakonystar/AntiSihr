package dev.djakonystar.antisihr.ui.audio

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.MainActivity
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.databinding.ScreenAudioPlayerBinding
import dev.djakonystar.antisihr.presentation.audio.AudioScreenViewModel
import dev.djakonystar.antisihr.presentation.audio.impl.AudioScreenViewModelImpl
import dev.djakonystar.antisihr.service.manager.PlayerManager
import dev.djakonystar.antisihr.service.models.AudioStatus
import dev.djakonystar.antisihr.service.models.PlayerManagerListener
import dev.djakonystar.antisihr.utils.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class AudioPlayerScreen : Fragment(R.layout.screen_audio_player), SeekBar.OnSeekBarChangeListener,
    PlayerManagerListener {
    private val binding: ScreenAudioPlayerBinding by viewBinding(ScreenAudioPlayerBinding::bind)
    private val viewModel: AudioScreenViewModel by viewModels<AudioScreenViewModelImpl>()
    private val args: AudioPlayerScreenArgs by navArgs()
    private var isFirstTime = true
    private val mediaPlayerManager: PlayerManager
        get() = (requireActivity() as MainActivity).audioPlayerManager

    //temp value
    private var isFavourite = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        mediaPlayerManager.jcPlayerManagerListener = this
        binding.icBack.clicks().debounce(200).onEach {
            findNavController().navigateUp()
        }.launchIn(lifecycleScope)

        binding.icFavourite.clicks().debounce(200).onEach {
            isFavourite = !isFavourite
            if (isFavourite) {
                binding.icFavourite.setImageResource(R.drawable.ic_favourites)
            } else {
                binding.icFavourite.setImageResource(R.drawable.ic_favourites_filled)
            }
        }.launchIn(lifecycleScope)

        binding.btnPlay.clicks().debounce(200).onEach {
            if (binding.icPlay.isVisible) {
                if (isFirstTime) {
                    playAudio(args.id)
                    isFirstTime = false
                } else {
                    continueAudio()
                }
            } else {
                pause()
            }
        }.launchIn(lifecycleScope)

        binding.icPrevious.clicks().debounce(200).onEach {
            previous()
        }.launchIn(lifecycleScope)

        binding.icForward.clicks().debounce(200).onEach {
            next()
        }.launchIn(lifecycleScope)

        binding.icRepeat.clicks().debounce(200).onEach {
            mediaPlayerManager.activeRepeat()
            binding.icRepeat.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
            if (mediaPlayerManager.repeatCurrAudio) {
                binding.icRepeat.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(), R.color.main_color
                    )
                )
            }
        }.launchIn(lifecycleScope)

        binding.icShuffle.clicks().debounce(200).onEach {
            mediaPlayerManager.onShuffleMode = mediaPlayerManager.onShuffleMode.not()
            if (mediaPlayerManager.onShuffleMode) {
                binding.icShuffle.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(), R.color.main_color
                    )
                )
            } else {
                binding.icShuffle.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(), R.color.black
                    )
                )
            }
        }.launchIn(lifecycleScope)

        binding.musicController.setOnSeekBarChangeListener(this)
    }


    private fun initObservers() {
        binding.tvAudioAuthor.text = args.author
        binding.tvAudioName.text = args.name
        binding.icAudioImage.setImageWithGlide(requireContext(), args.image)
        binding.endTime.text = args.url.getMediaDuration().milliSecondsToTimer()

        lifecycleScope.launchWhenResumed {
            visibilityOfBottomNavigationView.emit(false)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, i: Int, fromUser: Boolean) {
        if (fromUser) {
            mediaPlayerManager.seekTo(i)
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        if (mediaPlayerManager.isPaused()) {
            binding.icPlay.show()
            binding.icPause.hide()
        }
    }

    override fun onPreparedAudio(status: AudioStatus) {
        val mediaDuration = status.audio!!.url.getMediaDuration()
        binding.icPause.show()
        binding.icPlay.hide()
        resetPlayerInfo()
        onUpdateTitle(status.audio)
        binding.musicController.max = mediaDuration.toInt()
        binding.endTime.text = mediaDuration.milliSecondsToTimer()
    }

    private fun onUpdateTitle(audio: AudioResultData?) {
        audio?.let {
            binding.tvTitle.text = it.name
            binding.tvAudioName.text = it.name
            binding.tvAudioAuthor.text = it.author
            binding.icAudioImage.setImageWithGlide(requireContext(), it.image)
        }
    }

    override fun onCompletedAudio() {
        resetPlayerInfo()
        try {
            mediaPlayerManager.nextAudio()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun resetPlayerInfo() {
        binding.tvTitle.text = ""
        binding.tvAudioName.text = ""
        binding.tvAudioAuthor.text = ""
        binding.musicController.progress = 0
        binding.icAudioImage.setImageResource(R.drawable.ic_launcher_background)
    }

    override fun onPaused(status: AudioStatus) {
    }

    override fun onContinueAudio(status: AudioStatus) {
        binding.icPause.show()
        binding.icPlay.hide()
    }

    override fun onPlaying(status: AudioStatus) {
        binding.icPlay.hide()
        binding.icPause.show()
    }

    override fun onTimeChanged(status: AudioStatus) {
        val currentPosition = status.currentPosition
        requireActivity().runOnUiThread {
            binding.musicController.post {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.musicController.setProgress(
                        currentPosition.toInt(),
                        true
                    )
                } else {
                    binding.musicController.progress = currentPosition.toInt()
                }
            }
            binding.currentTime.post {
                binding.currentTime.text = currentPosition.milliSecondsToTimer()
            }
        }

    }

    override fun onStopped(status: AudioStatus) {
    }

    override fun onJcpError(throwable: Throwable) {
    }

    fun playAudio(jcAudio: Int) {
        mediaPlayerManager.playlist.let {
            val audio = it.find { it.id == jcAudio }
            mediaPlayerManager.playAudio(audio!!)
        }
    }

    fun next() {
        mediaPlayerManager.let { player ->
            player.currentAudio?.let {
                resetPlayerInfo()

                try {
                    player.nextAudio()
                } catch (e: Exception) {
                    binding.icPause.show()
                    binding.icPlay.hide()
                    e.printStackTrace()
                }
            }
        }
    }

    fun previous() {
        resetPlayerInfo()
        try {
            mediaPlayerManager.previousAudio()
        } catch (e: Exception) {
            binding.icPause.show()
            binding.icPlay.hide()
            e.printStackTrace()
        }

    }

    fun continueAudio() {
        try {
            mediaPlayerManager.continueAudio()
        } catch (e: Exception) {
            binding.icPause.show()
            binding.icPlay.hide()
            e.printStackTrace()
        }
    }

    fun pause() {
        mediaPlayerManager.pauseAudio()
        binding.icPause.hide()
        binding.icPlay.show()
    }
}
