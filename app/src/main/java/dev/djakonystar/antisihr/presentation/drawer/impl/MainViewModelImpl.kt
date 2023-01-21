package dev.djakonystar.antisihr.presentation.drawer.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.djakonystar.antisihr.data.models.GenericResponse
import dev.djakonystar.antisihr.data.models.ResultData
import dev.djakonystar.antisihr.data.models.drawerlayout.AddFeedbackData
import dev.djakonystar.antisihr.data.models.drawerlayout.LanguageData
import dev.djakonystar.antisihr.domain.usecase.MainUseCase
import dev.djakonystar.antisihr.presentation.drawer.MainViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModelImpl @Inject constructor(
    private val useCase: MainUseCase
) : ViewModel(), MainViewModel {
    override val sendFeedbackDataSuccesFlow = MutableSharedFlow<GenericResponse<String>>()
    override val messageFlow = MutableSharedFlow<String>()
    override val errorFlow = MutableSharedFlow<Throwable>()

    override suspend fun addFeedback(feedbackData: AddFeedbackData) {
        useCase.addFeedback(feedbackData).onEach {
            when (it) {
                is ResultData.Success -> {
                    sendFeedbackDataSuccesFlow.emit(it.data)
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

    override val getLanguagesSuccessFlow = MutableSharedFlow<GenericResponse<List<LanguageData>>>()

    override suspend fun getLanguages() {
        useCase.getListOfLanguages().onEach {
            when (it) {
                is ResultData.Success -> {
                    getLanguagesSuccessFlow.emit(it.data)
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
}