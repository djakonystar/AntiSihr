package dev.djakonystar.antisihr.presentation.audio.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked
import dev.djakonystar.antisihr.domain.usecase.AudioUseCase
import dev.djakonystar.antisihr.presentation.audio.AudioPlayerScreenViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject


@HiltViewModel
class AudioPlayerScreenViewModelImpl @Inject constructor(
    private val useCase: AudioUseCase
) : AudioPlayerScreenViewModel, ViewModel() {
    override val messageFlow = MutableSharedFlow<String>()
    override val errorFlow = MutableSharedFlow<Throwable>()


    override val isExistsInBookmarkedsFlow= MutableSharedFlow<Boolean>()

    override suspend fun getIsExistsInBookmarkeds(item: AudioBookmarked) {
        isExistsInBookmarkedsFlow.emit(useCase.isExistsInBookmarkeds(item))
    }


    override suspend fun addToBookmarkeds(audioBookmarked: AudioBookmarked) {
        useCase.addAudioToBookmarked(audioBookmarked).launchIn(viewModelScope)
    }

    override suspend fun deleteFromBookmarkeds(audioBookmarked: AudioBookmarked) {
        useCase.deleteAudioFromBookmarked(audioBookmarked).launchIn(viewModelScope)
    }
}