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
import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.databinding.ScreenAudioBinding
import dev.djakonystar.antisihr.presentation.audio.AudioScreenViewModel
import dev.djakonystar.antisihr.presentation.audio.impl.AudioScreenViewModelImpl
import dev.djakonystar.antisihr.service.manager.PlayerManager
import dev.djakonystar.antisihr.ui.adapters.AudioAdapter
import dev.djakonystar.antisihr.utils.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks


@AndroidEntryPoint
class AudioScreen : Fragment(R.layout.screen_audio) {
    private val binding: ScreenAudioBinding by viewBinding(ScreenAudioBinding::bind)
    private val viewModel: AudioScreenViewModel by viewModels<AudioScreenViewModelImpl>()
    private val mediaPlayerManager: PlayerManager
        get() = (requireActivity() as MainActivity).audioPlayerManager
    private var isClickedFavourite = false
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
            visibilityOfLoadingAnimationView.emit(true)
            showBottomNavigationView.emit(Unit)
            viewModel.getListOfAudios()
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

        binding.icFavourites.clicks().debounce(200).onEach {
            isClickedFavourite = isClickedFavourite.not()
            if (isClickedFavourite){
                binding.icFavourites.setImageResource(R.drawable.ic_favourites_filled)
                viewModel.getBookmarkedAudios()
            }else{
                binding.icFavourites.setImageResource(R.drawable.ic_favourites)
                viewModel.getListOfAudios()
            }
        }.launchIn(lifecycleScope)


        adapter.setOnItemClickListener {
            val audio = AudioResultData(
                it.author,
                it.date_create?:"",
                it.date_update?:"",
                it.id,
                it.image,
                it.name,
                it.url
            )
            if (mediaPlayerManager.isPlaying() && mediaPlayerManager.currentAudio==audio){
                findNavController().navigate(
                    AudioScreenDirections.actionAudioScreenToAudioInfoScreen(
                        it.id, it.name, it.author, it.url, it.image,true
                    )
                )
            }else{
                findNavController().navigate(
                    AudioScreenDirections.actionAudioScreenToAudioInfoScreen(
                        it.id, it.name, it.author, it.url, it.image,false
                    )
                )
                (requireActivity() as MainActivity).pause()
            }
            (requireActivity() as MainActivity).visibilityMediaPlayer(View.GONE)
        }

        adapter.setOnPlayClickListener {
            val audio = AudioResultData(
                it.author,
                it.date_create?:"",
                it.date_update?:"",
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
            adapter.submitList(it)
            val audioList = mutableListOf<AudioResultData>()
            it.forEach {
              audioList.add(
                  AudioResultData(
                  it.author,
                  it.date_create?:"",
                  it.date_update?:"",
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
        if (mediaPlayerManager.isPlaying()){
            (requireActivity() as MainActivity).visibilityMediaPlayer(View.VISIBLE)
        }
    }


}