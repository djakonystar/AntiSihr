package dev.djakonystar.antisihr.presentation.audio.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.ListOfTestsResultData
import dev.djakonystar.antisihr.data.models.TestData
import dev.djakonystar.antisihr.data.room.entity.AudioBookmarked
import dev.djakonystar.antisihr.domain.usecase.AudioUseCase
import dev.djakonystar.antisihr.domain.usecase.TestUseCase
import dev.djakonystar.antisihr.presentation.audio.AudioScreenViewModel
import dev.djakonystar.antisihr.presentation.test.HomeScreenViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AudioScreenViewModelImpl @Inject constructor(
    private val useCase: AudioUseCase
) : AudioScreenViewModel, ViewModel() {
    override val getListOfAudiosSuccessFlow = MutableSharedFlow<List<AudioBookmarked>>()
    override val messageFlow = MutableSharedFlow<String>()
    override val errorFlow = MutableSharedFlow<Throwable>()

    override suspend fun getListOfAudios() {
        useCase.getListOfAudios().onEach {
            when (it) {
                is ResultData.Success -> {
                    getListOfAudiosSuccessFlow.emit(it.data)
                }
                is ResultData.Message -> {
                    messageFlow.emit(it.message)
                }
                is ResultData.Error -> {
                    errorFlow.emit(it.error)
                }
            }
        }.launchIn(viewModelScope)
    }

    override val isExistsInBookmarkedsFlow= MutableSharedFlow<Boolean>()

    override suspend fun getIsExistsInBookmarkeds(item: AudioBookmarked) {
        isExistsInBookmarkedsFlow.emit(useCase.isExistsInBookmarkeds(item))
    }

    override suspend fun getBookmarkedAudios() {
        useCase.getListOfBookmarkedAudios().onEach {
            when (it) {
                is ResultData.Success -> {
                    Log.d("TTTT","${it.data}")
                    getListOfAudiosSuccessFlow.emit(it.data)
                }
                is ResultData.Message -> {
                    messageFlow.emit(it.message)
                }
                is ResultData.Error -> {
                    errorFlow.emit(it.error)
                }
            }
        }.launchIn(viewModelScope)
    }

    override suspend fun addToBookmarkeds(audioBookmarked: AudioBookmarked) {
        useCase.addAudioToBookmarked(audioBookmarked).launchIn(viewModelScope)
    }

    override suspend fun deleteFromBookmarkeds(audioBookmarked: AudioBookmarked) {
        useCase.deleteAudioFromBookmarked(audioBookmarked).launchIn(viewModelScope)
    }

}