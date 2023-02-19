package dev.djakonystar.antisihr.ui.audio

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.djakonystar.antisihr.MainActivity
import dev.djakonystar.antisihr.R
import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.data.models.library.LibraryResultData
import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked
import dev.djakonystar.antisihr.databinding.ScreenAudioBinding
import dev.djakonystar.antisihr.presentation.audio.AudioScreenViewModel
import dev.djakonystar.antisihr.presentation.audio.impl.AudioScreenViewModelImpl
import dev.djakonystar.antisihr.service.manager.PlayerManager
import dev.djakonystar.antisihr.ui.adapters.AudioAdapter
import dev.djakonystar.antisihr.utils.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.swiperefreshlayout.refreshes
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class AudioScreen : Fragment(R.layout.screen_audio) {
    private val binding: ScreenAudioBinding by viewBinding(ScreenAudioBinding::bind)
    private val viewModel: AudioScreenViewModel by viewModels<AudioScreenViewModelImpl>()
    private val mediaPlayerManager: PlayerManager
        get() = (requireActivity() as MainActivity).audioPlayerManager

    private val allAudio = mutableListOf<AudioBookmarked>()
    private var isClickedFavourite = false
    private var _adapter: AudioAdapter? = null
    private val adapter: AudioAdapter get() = _adapter!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    (requireActivity() as MainActivity).changeBottomNavigationSelectedItem(true)
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)

        lifecycleScope.launchWhenResumed {
            showBottomNavigationView.emit(Unit)
        }

        initListeners()
        initObservers()

    }

    private fun initListeners() {
        _adapter = AudioAdapter()
        binding.rcAudios.adapter = adapter
        binding.expandableLayout.duration = 300

        binding.swipeRefreshLayout.refreshes().onEach {
            binding.swipeRefreshLayout.isRefreshing = false
            if (isClickedFavourite) {
                viewModel.getBookmarkedAudios()
            } else {
                viewModel.getListOfAudios()
            }
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        binding.etAudio.doAfterTextChanged {
            if (binding.etAudio.text.toString().isEmpty()) {
                hideKeyboard()
                adapter.audios = allAudio
            } else {
                val newList = allAudio.filter { r ->
                    r.name.contains(
                        binding.etAudio.text.toString(), ignoreCase = true
                    ) || r.author.contains(binding.etAudio.text.toString(), ignoreCase = true)
                }
                adapter.audios = newList
            }
        }

        binding.icSearch.clicks().debounce(200).onEach {
            binding.tvAudio.text = getString(R.string.search)
            binding.icFavourites.hide()
            binding.icSearch.hide()
            binding.icClose.show()
            binding.tvBody.hide()
            binding.expandableLayout.expand()
        }.launchIn(lifecycleScope)

        binding.icClose.clicks().debounce(200).onEach {
            binding.tvAudio.text = getString(R.string.audio)
            binding.icClose.hide()
            binding.icFavourites.show()
            binding.icSearch.show()
            binding.tvBody.show()
            binding.expandableLayout.collapse()
            binding.etAudio.setText("")
            hideKeyboard()
        }.launchIn(lifecycleScope)

        binding.icFavourites.clicks().debounce(200).onEach {
            isClickedFavourite = isClickedFavourite.not()
            if (isClickedFavourite) {
                binding.icFavourites.setImageResource(R.drawable.ic_favourites_filled)
                viewModel.getBookmarkedAudios()
            } else {
                binding.icFavourites.setImageResource(R.drawable.ic_favourites)
                viewModel.getListOfAudios()
            }
        }.launchIn(lifecycleScope)


        adapter.setOnItemClickListener {
            val audio = AudioResultData(
                it.author,
                it.date_create ?: "",
                it.date_update ?: "",
                it.id,
                it.image,
                it.name,
                it.url
            )
            if (mediaPlayerManager.isPlaying() && mediaPlayerManager.currentAudio == audio) {
                findNavController().navigate(
                    AudioScreenDirections.actionAudioScreenToAudioInfoScreen(
                        it.id, it.name, it.author, it.url, it.image, true
                    )
                )
            } else {
                findNavController().navigate(
                    AudioScreenDirections.actionAudioScreenToAudioInfoScreen(
                        it.id, it.name, it.author, it.url, it.image, false
                    )
                )
                (requireActivity() as MainActivity).pause()
                (requireActivity() as MainActivity).playAudio(it.id)
            }
            (requireActivity() as MainActivity).visibilityMediaPlayer(View.GONE)
        }

        adapter.setOnPlayClickListener {
            val audio = AudioResultData(
                it.author,
                it.date_create ?: "",
                it.date_update ?: "",
                it.id,
                it.image,
                it.name,
                it.url
            )
            lifecycleScope.launchWhenResumed {
                playAudioFlow.emit(audio)
            }
        }


    }


    private fun initObservers() {
        viewModel.getListOfAudiosSuccessFlow.onEach {
            allAudio.clear()
            allAudio.addAll(it)
            adapter.audios = it
            val audioList = mutableListOf<AudioResultData>()
            it.forEach {
                audioList.add(
                    AudioResultData(
                        it.author,
                        it.date_create ?: "",
                        it.date_update ?: "",
                        it.id,
                        it.image,
                        it.name,
                        it.url
                    )
                )
            }
            (requireActivity() as MainActivity).setAudioList(audioList)
            visibilityOfLoadingAnimationView.emit(false)
        }.launchIn(lifecycleScope)
    }


    override fun onResume() {
        super.onResume()
        if (mediaPlayerManager.isPlaying()) {
            (requireActivity() as MainActivity).visibilityMediaPlayer(View.VISIBLE)
        }
    }
}