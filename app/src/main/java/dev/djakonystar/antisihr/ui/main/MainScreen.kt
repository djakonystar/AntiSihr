package dev.djakonystar.antisihr.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUiSaveStateControl
import by.kirich1409.viewbindingdelegate.viewBinding
import dev.djakonystar.antisihr.MainActivity
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.AudioStatus
import dev.djakonystar.antisihr.data.models.PlayerManagerListener
import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked
import dev.djakonystar.antisihr.databinding.ScreenMainBinding
import dev.djakonystar.antisihr.service.manager.PlayerManager
import dev.djakonystar.antisihr.service.notification.MusicService
import dev.djakonystar.antisihr.utils.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks

@OptIn(NavigationUiSaveStateControl::class)
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
        NavigationUI.setupWithNavController(binding.bottomNavigationBar, childNavController, false)

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
            mediaPlayerManager.continueAudio()
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
            if (mediaPlayerManager.isPlaying()) {
                mediaPlayerManager.currentAudio?.let {
                    binding.tvName.text = it.name
                    binding.tvAuthor.text = it.author
                    binding.icImage.setImageWithGlide(requireContext(), it.image)
                    binding.icPause.show()
                    binding.icPlay.hide()
                    binding.icSkipForward.show()
                    binding.icClose.hide()
                    binding.pbLoadingBar.hide()
                    binding.icImage.show()
                    binding.btnPause.show()
                    binding.layoutMusicPlayer.expand()
                }
            } else {
                if ((requireActivity() as MainActivity).isFirstTime.not() && mediaPlayerManager.onShuffleMode.not()) {
                    mediaPlayerManager.currentAudio?.let {
                        binding.tvName.text = it.name
                        binding.tvAuthor.text = it.author
                        binding.icImage.setImageWithGlide(requireContext(), it.image)
                        binding.icPlay.show()
                        binding.icPause.hide()
                        binding.icClose.show()
                        binding.icSkipForward.hide()
                        binding.pbLoadingBar.hide()
                        binding.icImage.show()
                        binding.btnPause.show()
                        binding.layoutMusicPlayer.expand()
                    }
                } else {
                    binding.layoutMusicPlayer.collapse()
                }
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

        errorBottomPlayerInfoFlow.onEach {
            if (this.view != null) {
                binding.icPause.show()
                binding.icPlay.hide()
                binding.icSkipForward.hide()
                binding.icClose.hide()
            }
        }.launchIn(lifecycleScope)

        resetBottomPlayerInfoFlow.onEach {
            resetPlayerInfo()
        }.launchIn(lifecycleScope)
    }

    private fun onUpdateTitle(audio: AudioBookmarked) {
        if (this.view != null) {
            audio.let {
                binding.tvName.text = it.name
                binding.tvAuthor.text = it.author
                binding.icImage.setImageWithGlide(requireContext(), it.image)
                binding.icClose.hide()
                binding.icSkipForward.show()
                binding.btnPause.show()
                binding.icPause.show()
                binding.icPlay.hide()
                binding.icSkipForward.show()
                binding.icClose.hide()
                binding.icImage.show()
                binding.pbLoadingBar.hide()
                if (binding.bottomNavigationBar.selectedItemId == R.id.audioScreen) {
                    binding.layoutMusicPlayer.expand()
                }
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
        if (this.view != null) {
            resetPlayerInfo()
            status.audio?.let {
                onUpdateTitle(it)
            }
        }
    }

    override fun onCompletedAudio() {
        if (this.view != null) {
            resetPlayerInfo()
        }
    }

    override fun onPaused(status: AudioStatus) {
        if (this.view != null) {
            binding.icPause.hide()
            binding.icPlay.show()
            binding.icSkipForward.hide()
            binding.icClose.show()
        }
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

    override fun onResume() {
        super.onResume()
        if (binding.bottomNavigationBar.selectedItemId == R.id.audioScreen) {
            if (mediaPlayerManager.isPlaying()) {
                mediaPlayerManager.currentAudio?.let {
                    binding.tvName.text = it.name
                    binding.tvAuthor.text = it.author
                    binding.icImage.setImageWithGlide(requireContext(), it.image)
                    binding.icPause.show()
                    binding.icPlay.hide()
                    binding.icSkipForward.show()
                    binding.icClose.hide()
                    binding.pbLoadingBar.hide()
                    binding.icImage.show()
                    binding.btnPause.show()
                    binding.layoutMusicPlayer.expand()
                }
            } else {
                if ((requireActivity() as MainActivity).isFirstTime.not()) {
                    mediaPlayerManager.currentAudio?.let {
                        binding.tvName.text = it.name
                        binding.tvAuthor.text = it.author
                        binding.icImage.setImageWithGlide(requireContext(), it.image)
                        binding.icPlay.show()
                        binding.icPause.hide()
                        binding.icClose.show()
                        binding.icSkipForward.hide()
                        binding.pbLoadingBar.hide()
                        binding.icImage.show()
                        binding.btnPause.show()
                        binding.layoutMusicPlayer.expand()
                    }
                }
            }
        }
    }
}
