package dev.djakonystar.antisihr.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dev.djakonystar.antisihr.MainActivity
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.data.models.AudioStatus
import dev.djakonystar.antisihr.data.models.PlayerManagerListener
import dev.djakonystar.antisihr.databinding.ScreenMainBinding
import dev.djakonystar.antisihr.service.manager.PlayerManager
import dev.djakonystar.antisihr.service.notification.MusicService
import dev.djakonystar.antisihr.utils.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks

class MainScreen : Fragment(R.layout.screen_main), PlayerManagerListener {
    private val binding by viewBinding(ScreenMainBinding::bind)
    private lateinit var navController: NavController
    private lateinit var childNavController: NavController
    private val mediaPlayerManager: PlayerManager
        get() = (requireActivity() as MainActivity).audioPlayerManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = findNavController()
        childNavController =
            (childFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment).navController
        binding.bottomNavigationBar.setupWithNavController(childNavController)

        initListeners()
        initObservers()
    }

    private fun initListeners() {
        mediaPlayerManager.jcPlayerManagerListener = this

        binding.btnPause.clicks().debounce(200).onEach {
            val activity = requireActivity() as MainActivity
            if (mediaPlayerManager.isPlaying()) {
                activity.pause()
                binding.icSkipForward.hide()
                binding.icClose.show()
            } else {
                activity.continueAudio()
                binding.icClose.hide()
                binding.icSkipForward.show()
            }
        }.launchIn(lifecycleScope)

        binding.icSkipForward.clicks().debounce(200).onEach {
            (requireActivity() as MainActivity).next()
        }.launchIn(lifecycleScope)

        binding.icClose.clicks().debounce(200).onEach {
            binding.layoutMusicPlayer.collapse()
            val intent = Intent(requireContext(), MusicService::class.java)
            (requireActivity() as MainActivity).stopService(intent)
        }.launchIn(lifecycleScope)

        binding.icPlay.clicks().debounce(200).onEach {
//            mediaPlayerManager.playAudio(mediaPlayerManager.currentAudio)
        }.launchIn(lifecycleScope)

        binding.layoutMusicPlayer.clicks().debounce(200).onEach {
            val audio = (requireActivity() as MainActivity).audioPlayerManager.currentAudio!!
            navController.navigate(
                MainScreenDirections.actionMainScreenToAudioPlayerScreen(
                    audio.id,
                    audio.name,
                    audio.author,
                    audio.url,
                    audio.image,
                    (requireActivity() as MainActivity).audioPlayerManager.isPlaying()
                )
            )
        }.launchIn(lifecycleScope)


        binding.bottomNavigationBar.menu.findItem(R.id.testScreen)?.setOnMenuItemClickListener {
            binding.layoutMusicPlayer.collapse()
            return@setOnMenuItemClickListener false
        }
        binding.bottomNavigationBar.menu.findItem(R.id.audioScreen)?.setOnMenuItemClickListener {
            if ((requireActivity() as MainActivity).audioPlayerManager.onShuffleMode.not() && (requireActivity() as MainActivity).audioPlayerManager.isPlaying()) {
                binding.layoutMusicPlayer.expand()
            } else {
                binding.layoutMusicPlayer.collapse()
            }
            return@setOnMenuItemClickListener false
        }
        binding.bottomNavigationBar.menu.findItem(R.id.libraryScreen)?.setOnMenuItemClickListener {
            binding.layoutMusicPlayer.collapse()
            return@setOnMenuItemClickListener false
        }
        binding.bottomNavigationBar.menu.findItem(R.id.shopScreen)?.setOnMenuItemClickListener {
            binding.layoutMusicPlayer.collapse()
            return@setOnMenuItemClickListener false
        }
        binding.bottomNavigationBar.menu.findItem(R.id.readersScreen)?.setOnMenuItemClickListener {
            binding.layoutMusicPlayer.collapse()
            return@setOnMenuItemClickListener false
        }
    }

    private fun initObservers() {
        changeBottomNavItemFlow.onEach {
            if (!it) {
                binding.bottomNavigationBar.selectedItemId = R.id.libraryScreen

            }
        }.launchIn(lifecycleScope)

//        playAudioFlow.onEach {
//            (requireActivity() as MainActivity).playAudio(it.id)
//        }.launchIn(lifecycleScope)

//        preparingAudioFlow.onEach {
//            if (this.view != null) {
//                val audioPlayerManager = it.first
//                val status = it.second
//
//                binding.icPause.show()
//                binding.icPlay.hide()
//                resetPlayerInfo()
//                onUpdateTitle(status.audio)
//
//                if (audioPlayerManager.onShuffleMode.not() && audioPlayerManager.currentPositionList == audioPlayerManager.playlist.lastIndex) {
//                    binding.icSkipForward.isEnabled = false
//                    binding.icSkipForward.setColorFilter(
//                        ContextCompat.getColor(requireContext(), R.color.grey)
//                    )
//                } else {
//                    binding.icSkipForward.isEnabled = true
//                    binding.icSkipForward.setColorFilter(
//                        ContextCompat.getColor(requireContext(), R.color.black)
//                    )
//                }
//            }
//        }.launchIn(lifecycleScope)

//        completeAudioFlow.onEach {
//            resetPlayerInfo()
//            try {
//                it.nextAudio(true)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }.launchIn(lifecycleScope)
//
//        pausedAudioFlow.onEach {
//            if (this.view != null) {
//                binding.icPause.hide()
//                binding.icPlay.show()
//                binding.icSkipForward.hide()
//                binding.icClose.show()
//            }
//        }.launchIn(lifecycleScope)

//        continueAudioFlow.onEach {
//            if (this.view != null) {
//                binding.icPause.show()
//                binding.icPlay.hide()
//                binding.icSkipForward.show()
//                binding.icClose.hide()
//            }
//        }.launchIn(lifecycleScope)

//        playingAudioFlow.onEach {
//            if (this.view != null) {
//                binding.icPlay.hide()
//                binding.icPause.show()
//                binding.icSkipForward.show()
//                binding.icClose.hide()
//            }
//        }.launchIn(lifecycleScope)

//        showBottomPlayerFlow.onEach {
//            if (it && (requireActivity() as MainActivity).audioPlayerManager.isPlaying()) {
//                (requireActivity() as MainActivity).audioPlayerManager.currentAudio?.let {
//                    binding.tvName.text = it.name
//                    binding.tvAuthor.text = it.author
//                    binding.icImage.setImageWithGlide(requireContext(), it.image)
//                    binding.icSkipForward.show()
//                    binding.icClose.hide()
//                    binding.btnPause.show()
//                    binding.pbLoadingBar.hide()
//                    binding.icImage.show()
//                    binding.layoutMusicPlayer.expand()
//                }
//            } else {
//                binding.layoutMusicPlayer.collapse()
//            }
//        }.launchIn(lifecycleScope)

//        audioPlayClickFlow.onEach {
//            resetPlayerInfo()
//        }.launchIn(lifecycleScope)

//        audioNextClickFlow.onEach {
//            if (this.view != null) {
//                it.let { player ->
//                    player.currentAudio?.let {
//                        resetPlayerInfo()
//                        try {
//                            player.nextAudio(false)
//                        } catch (e: Exception) {
//                            binding.icPause.show()
//                            binding.icPlay.hide()
//                            binding.icSkipForward.hide()
//                            binding.icClose.hide()
//                            e.printStackTrace()
//                        }
//                    }
//                }
//            }
//        }.launchIn(lifecycleScope)

//        audioContinueClickFlow.onEach {
//            if (this.view != null) {
//                try {
//                    it.continueAudio()
//                } catch (e: Exception) {
//                    binding.icPause.show()
//                    binding.icPlay.hide()
//                    binding.icSkipForward.hide()
//                    binding.icClose.hide()
//                    e.printStackTrace()
//                }
//            }
//        }.launchIn(lifecycleScope)

//        audioPauseClickFlow.onEach {
//            if (this.view != null) {
//                it.pauseAudio()
//                binding.icPause.hide()
//                binding.icPlay.show()
//                binding.icSkipForward.show()
//                binding.icClose.hide()
//            }
//        }.launchIn(lifecycleScope)

//        audioPreviousClickFlow.onEach {
//            if (this.view != null) {
//                resetPlayerInfo()
//                try {
//                    it.previousAudio()
//                } catch (e: Exception) {
//                    binding.icPause.show()
//                    binding.icPlay.hide()
//                    binding.icSkipForward.hide()
//                    binding.icClose.hide()
//                    e.printStackTrace()
//                }
//            }
//        }.launchIn(lifecycleScope)
    }

    private fun onUpdateTitle(audio: AudioResultData?) {
        if (this.view != null) {
            audio?.let {
                binding.tvName.text = it.name
                binding.tvAuthor.text = it.author
                binding.icImage.setImageWithGlide(requireContext(), it.image)
                binding.icSkipForward.show()
                binding.icClose.hide()
                if (binding.bottomNavigationBar.selectedItemId == R.id.audioScreen) {
                    binding.layoutMusicPlayer.expand()
                }
                binding.btnPause.show()
                binding.pbLoadingBar.hide()
                binding.icImage.show()
            }
        }
    }

    private fun resetPlayerInfo() {
        if (this.view != null) {
            binding.tvName.text = ""
            binding.tvAuthor.text = ""
            binding.icSkipForward.hide()
            binding.icClose.hide()
            binding.pbLoadingBar.show()
            binding.icImage.invisible()
            binding.btnPause.hide()
        }
    }

    override fun onPreparedAudio(status: AudioStatus) {
        resetPlayerInfo()
        if (this.view != null) {
            status.audio?.let {
                onUpdateTitle(it)
//                binding.tvName.text = it.name
//                binding.tvAuthor.text = it.author
//                binding.icImage.setImageWithGlide(requireContext(), it.url)
//                binding.icPause.show()
//                binding.icPlay.hide()
//                binding.icSkipForward.show()
//                binding.icClose.hide()
            }
        }
    }

    override fun onCompletedAudio() {
        mediaPlayerManager.nextAudio(true)
    }

    override fun onPaused(status: AudioStatus) {
        binding.icPause.hide()
        binding.icPlay.show()
        binding.icSkipForward.hide()
        binding.icClose.show()
    }

    override fun onContinueAudio(status: AudioStatus) {
        if (this.view != null) {
            binding.icPause.show()
            binding.icPlay.hide()
            binding.icSkipForward.show()
            binding.icClose.hide()
        }
    }

    override fun onPlaying(status: AudioStatus) {
        if (this.view != null) {
            binding.icPause.show()
            binding.icPlay.hide()
            binding.icSkipForward.show()
            binding.icClose.hide()
        }
    }

    override fun onTimeChanged(status: AudioStatus) {

    }

    override fun onStopped(status: AudioStatus) {

    }

    override fun onJcpError(throwable: Throwable) {}

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayerManager.removeFromPlayerManagerListener(this)
    }
}
