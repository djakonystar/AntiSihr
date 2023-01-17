package dev.djakonystar.antisihr.presentation.audio.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.djakonystar.antisihr.data.models.AudioResultData
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.ListOfTestsResultData
import dev.djakonystar.antisihr.data.models.TestData
import dev.djakonystar.antisihr.domain.usecase.AudioUseCase
import dev.djakonystar.antisihr.domain.usecase.TestUseCase
import dev.djakonystar.antisihr.presentation.audio.AudioPlayerScreenViewModel
import dev.djakonystar.antisihr.presentation.audio.AudioScreenViewModel
import dev.djakonystar.antisihr.presentation.test.HomeScreenViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AudioPlayerScreenViewModelImpl @Inject constructor(
    private val useCase: AudioUseCase
) : AudioPlayerScreenViewModel, ViewModel() {
    override val messageFlow = MutableSharedFlow<String>()
    override val errorFlow = MutableSharedFlow<Throwable>()
}