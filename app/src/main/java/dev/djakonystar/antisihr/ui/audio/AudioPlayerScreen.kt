package dev.djakonystar.antisihr.ui.audio

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
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
import dev.djakonystar.antisihr.data.models.AudioStatus
import dev.djakonystar.antisihr.data.models.PlayerManagerListener
import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked
import dev.djakonystar.antisihr.databinding.ScreenAudioPlayerBinding
import dev.djakonystar.antisihr.presentation.audio.AudioPlayerScreenViewModel
import dev.djakonystar.antisihr.presentation.audio.impl.AudioPlayerScreenViewModelImpl
import dev.djakonystar.antisihr.service.manager.PlayerManager
import dev.djakonystar.antisihr.service.notification.MusicService
import dev.djakonystar.antisihr.utils.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks

@AndroidEntryPoint
class AudioPlayerScreen : Fragment(R.layout.screen_audio_player), SeekBar.OnSeekBarChangeListener,
    PlayerManagerListener {
    private val binding: ScreenAudioPlayerBinding by viewBinding(ScreenAudioPlayerBinding::bind)
    private val viewModel: AudioPlayerScreenViewModel by viewModels<AudioPlayerScreenViewModelImpl>()
    private val args: AudioPlayerScreenArgs by navArgs()
    private var currentMusic: AudioBookmarked? = null
    private val mediaPlayerManager: PlayerManager
        get() = (requireActivity() as MainActivity).audioPlayerManager

    private var isFavourite = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as MainActivity).setStatusBarColor(R.color.background_color)

        initListeners()
        initObservers()
        initData()

        lifecycleScope.launchWhenResumed {
            visibilityOfBottomNavigationView.emit(false)
            viewModel.getIsExistsInBookmarkeds(
                AudioBookmarked(
                    args.id, args.author, "", "", args.image, args.name, args.url
                )
            )
        }
    }

    private fun initListeners() {
        mediaPlayerManager.jcPlayerManagerListener = this
        binding.icBack.clicks().debounce(200).onEach {
            findNavController().navigateUp()
        }.launchIn(lifecycleScope)

        binding.icFavourite.clicks().debounce(200).onEach {
            isFavourite = isFavourite.not()
            if (isFavourite) {
                viewModel.addToBookmarkeds(
                    currentMusic!!
                )
                binding.icFavourite.setImageResource(R.drawable.ic_favourites_filled)
                binding.icFavourite.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.fav_color)
                )
            } else {
                viewModel.deleteFromBookmarkeds(
                    currentMusic!!
                )
                binding.icFavourite.setImageResource(R.drawable.ic_favourites)
                binding.icFavourite.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.black)
                )
            }
        }.launchIn(lifecycleScope)

        binding.tvAudioName.isSelected = true
        binding.tvAudioAuthor.isSelected = true

        binding.btnPlay.clicks().debounce(200).onEach {
            if (binding.icPlay.isVisible) {
                mediaPlayerManager.continueAudio()
            } else {
                mediaPlayerManager.pauseAudio()
            }
        }.launchIn(lifecycleScope)

        binding.icPrevious.clicks().debounce(200).onEach {
            resetPlayerInfo()
            try {
                mediaPlayerManager.previousAudio()
            } catch (e: Exception) {
                binding.icPause.show()
                binding.icPlay.hide()
                e.printStackTrace()
            }
        }.launchIn(lifecycleScope)

        binding.icForward.clicks().debounce(200).onEach {
            resetPlayerInfo()
            try {
                mediaPlayerManager.nextAudio(false)
            } catch (e: Exception) {
                binding.icPause.show()
                binding.icPlay.hide()
                e.printStackTrace()
            }
        }.launchIn(lifecycleScope)

        binding.icRepeat.clicks().debounce(200).onEach {
            mediaPlayerManager.repeatCurrAudio = mediaPlayerManager.repeatCurrAudio.not()
            if (mediaPlayerManager.repeatCurrAudio) {
                binding.icRepeat.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(), R.color.main_color
                    )
                )
            } else {
                binding.icRepeat.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(), R.color.black
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


    private fun initData() {
        binding.tvAudioAuthor.text = args.author
        binding.tvAudioName.text = args.name
        binding.icAudioImage.setImageWithGlide(requireContext(), args.image)

        currentMusic = AudioBookmarked(
            args.id, args.author, "", "", args.image, args.name, args.url
        )


        if (args.isCurrentAudioPlaying) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.musicController.setProgress(
                    mediaPlayerManager.currentStatus!!.currentPosition.toInt(), true
                )
            } else {
                binding.musicController.progress =
                    mediaPlayerManager.currentStatus!!.currentPosition.toInt()
            }
            binding.musicController.max = mediaPlayerManager.currentStatus!!.duration
            binding.currentTime.text =
                mediaPlayerManager.currentStatus!!.currentPosition.milliSecondsToTimer()
            binding.endTime.text =
                mediaPlayerManager.currentStatus!!.duration.toLong().milliSecondsToTimer()
            binding.icPause.show()
            binding.icPlay.hide()
        }


        if (mediaPlayerManager.onShuffleMode) {
            binding.icShuffle.setColorFilter(
                ContextCompat.getColor(
                    requireContext(), R.color.main_color
                )
            )
        }

        if (mediaPlayerManager.repeatCurrAudio) {
            binding.icRepeat.setColorFilter(
                ContextCompat.getColor(
                    requireContext(), R.color.main_color
                )
            )
        }
    }

    private fun initObservers() {
        viewModel.isExistsInBookmarkedsFlow.onEach {
            isFavourite = it
            if (it) {
                binding.icFavourite.setImageResource(R.drawable.ic_favourites_filled)
                binding.icFavourite.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.fav_color)
                )
            } else {
                binding.icFavourite.setImageResource(R.drawable.ic_favourites)
                binding.icFavourite.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.black)
                )
            }
        }.launchIn(lifecycleScope)
    }

    override fun onProgressChanged(seekBar: SeekBar?, i: Int, fromUser: Boolean) {
        if (fromUser) {
            mediaPlayerManager.seekTo(i)
            binding.currentTime.text = i.toLong().milliSecondsToTimer()
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        mediaPlayerManager.pauseAudio()
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        if (this.view != null) {
            if (mediaPlayerManager.isPaused()) {
                binding.icPlay.show()
                binding.icPause.hide()
                mediaPlayerManager.continueAudio()
            }
        }
    }

    override fun onPreparedAudio(status: AudioStatus) {
        resetPlayerInfo()
        if (this.view != null) {
            currentMusic = AudioBookmarked(
                status.audio!!.id,
                status.audio!!.author,
                status.audio!!.date_create,
                status.audio!!.date_update,
                status.audio!!.image,
                status.audio!!.name,
                status.audio!!.url,
            )
            lifecycleScope.launchWhenResumed {
                viewModel.getIsExistsInBookmarkeds(
                    currentMusic!!
                )
            }
            onUpdateTitle(status.audio)
            binding.icPause.show()
            binding.icPlay.hide()
            binding.musicController.max = status.duration
            binding.endTime.text = status.duration.toLong().milliSecondsToTimer()
            binding.musicController.isEnabled = true
            binding.icPrevious.isEnabled = true
            binding.icForward.isEnabled = true
            binding.btnPlay.isEnabled = true
            binding.icFavourite.show()
        }
    }

    private fun onUpdateTitle(audio: AudioBookmarked?) {
        if (this.view != null) {
            audio?.let {
                binding.tvTitle.text = it.name
                binding.tvAudioName.text = it.name
                binding.tvAudioAuthor.text = it.author
                binding.icAudioImage.setImageWithGlide(requireContext(), it.image)
            }
        }
    }

    //
    override fun onCompletedAudio() {
        try {
            if (mediaPlayerManager.repeatCurrAudio) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.musicController.setProgress(0, true)
                } else {
                    binding.musicController.progress = 0
                }
                binding.currentTime.text = "00:00"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun resetPlayerInfo() {
        if (this.view != null) {
            binding.currentTime.hide()
            binding.endTime.hide()
            binding.tvTitle.text = ""
            binding.tvAudioName.text = ""
            binding.tvAudioAuthor.text = ""
            binding.musicController.progress = 0
            binding.icAudioImage.setImageDrawable(null)
            binding.pbLoadingBar.show()
            binding.icFavourite.hide()
            binding.musicController.isEnabled = false
            binding.icPrevious.isEnabled = false
            binding.icForward.isEnabled = false
            binding.btnPlay.isEnabled = false
        }
    }

    //
    override fun onPaused(status: AudioStatus) {
        if (this.view != null) {
            binding.icPlay.show()
            binding.icPause.hide()
        }
    }

    override fun onContinueAudio(status: AudioStatus) {
        if (this.view != null) {
            binding.musicController.max = status.duration
            binding.endTime.text = status.duration.toLong().milliSecondsToTimer()
            binding.icPause.show()
            binding.icPlay.hide()
        }
    }

    override fun onPlaying(status: AudioStatus) {
        if (this.view != null) {
            binding.icPlay.hide()
            binding.icPause.show()
        }
    }

    override fun onTimeChanged(status: AudioStatus) {
        if (this.view != null) {
            val currentPosition = status.currentPosition
            lifecycleScope.launchWhenResumed {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.musicController.setProgress(
                        currentPosition.toInt(), true
                    )
                } else {
                    binding.musicController.progress = currentPosition.toInt()
                }
                binding.currentTime.post {
                    try {
                        if (currentPosition.toInt() != status.duration) {
                            binding.currentTime.text = currentPosition.milliSecondsToTimer()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                binding.currentTime.show()
                binding.endTime.show()
            }
        }
    }

    override fun onStopped(status: AudioStatus) {
        if (this.view != null) {
            binding.icPlay.show()
            binding.icPause.hide()
        }
    }

    override fun onJcpError(throwable: Throwable) {
        val intent = Intent(requireActivity(), MusicService::class.java)
        requireActivity().stopService(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayerManager.removeFromPlayerManagerListener(this)
    }

}
